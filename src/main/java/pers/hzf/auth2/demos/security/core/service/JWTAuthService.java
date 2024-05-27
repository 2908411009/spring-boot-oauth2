package pers.hzf.auth2.demos.security.core.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.common.constants.ErrorCode;
import pers.hzf.auth2.demos.common.exception.BusinessException;
import pers.hzf.auth2.demos.common.utils.JWTUtil;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;
import pers.hzf.auth2.demos.common.web.UserContext;
import pers.hzf.auth2.demos.common.web.UserInfoDetails;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class JWTAuthService {

    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${ev.token.expire-time:28800}")
    private long expireTime;
    
    private static final String TOKEN_REDIS_NAMESPACE = "authorization:";
    
    

    /**
     * 创建jwt
     */
    public String addAuthentication(User user) {
        log.info("addAuthentication");
        //实际的过期时间以redis为准
        return JWTUtil.createJWT(UUID.randomUUID().toString(), user.getUsername(), 3600 * 24 * 30);
    }

    /**
     * 解析jwt
     */
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        token = StringUtils.removeStartIgnoreCase(token, "Bearer ");
        log.info("getAuthentication=>" + token);
        if (StrUtil.isEmpty(token) || token.equalsIgnoreCase("undefined")) {
            return null;
        }
        try {
            Claims claims = JWTUtil.parseJWT(token);
            String userName = claims.getSubject();
            if (userName != null) {
                //1.从redis中取出用户上下文,set到ThreadLocal<UserContext>
                UserContext userContext = this.getUserContext(token);
                if (userContext == null) {
                    throw new ExpiredJwtException(null, null, "token过期");
                }
                log.info("updateUserContext: {}", JacksonUtils.toJson(userContext));
                UserContext.set(userContext);
                //2.构造UsernamePasswordAuthenticationToken
                Set<XGrantedAuthority> authorities = userContext.getAuthorities();
                User principal = new User(userName, "", authorities);
                String redisKey = this.getJwtKey(token);
                Object o = redisTemplate.opsForValue().get(redisKey);
                if (o == null) {
                    throw new BusinessException(ErrorCode.AUTH_EXPIRED);
                }
                //利用redis的设置过期功能来实现token的自动续期
                redisTemplate.expire(redisKey, this.expireTime, TimeUnit.SECONDS);
                return new UsernamePasswordAuthenticationToken(principal, null, authorities);
            }
        } catch (ExpiredJwtException e) {
            log.info("抛出异常位置:{}", e.getStackTrace()[0]);
            throw new BusinessException(ErrorCode.AUTH_EXPIRED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ErrorCode.AUTH_EXPIRED);
        }
        return null;
    }

    /**
     * 同步上下文到redis
     * 场景:用户登录
     */
    public void updateUserContext(Authentication auth, String token) {
        User user = (User) auth.getPrincipal();
        log.info("updateUserContext=>" + user.getUsername());
        // 在此处查询用户 同时查询权限
        UserDetails systemUser = userAuthService.getUserByName(user.getUsername());
        UserContext userContext = this.getUserContext(token);
        if (userContext == null) {
            userContext = new UserContext();
        }
        UserInfoDetails udetail = (UserInfoDetails) systemUser;
        userContext.setId(udetail.getUserId());
        this.updateUserContext(systemUser, userContext, token);
    }

    /**
     * 同步上下文到redis
     * 场景:数据库表user或UserService发生了变更
     */
    public void updateUserContext(UserDetails userX, UserContext userContext, String token) {
        BeanUtil.copyProperties(userX, userContext);
        userContext.setToken(token);
        UserContext.set(userContext);
        long expireTimeX = SystemClock.now() / 1000 + this.expireTime;
        userContext.setExpireTime(expireTimeX);
        userContext.setExpire(DateUtil.formatDateTime(new Date(expireTimeX * 1000)));
        String redisKey = this.getJwtKey(token);
        log.info("redisKey:{}, updateUserContext: {}", redisKey, JacksonUtils.toJson(userContext));
        redisTemplate.opsForValue().set(redisKey, userContext);
        redisTemplate.expire(redisKey, this.expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取用户上下文
     */
    private UserContext getUserContext(String token) {
        String redisKey = this.getJwtKey(token);
        HashMap<String, Object> map = (HashMap<String, Object>) redisTemplate.opsForValue().get(redisKey);
        UserContext userContext = JacksonUtils.toObject(JacksonUtils.toJson(map), UserContext.class);
        //如果token过期不存在,会返回null
        return userContext;
    }

    /**
     * 注销用户上下文
     * 场景:退出登录
     */
    public void removeUserContext(String token) {
        String redisKey = this.getJwtKey(token);
        redisTemplate.delete(redisKey);
    }

    /**
     * 取jwt token中的第二部分做为redis key
     */
    private String getJwtKey(String jwt) {
        String[] arr = jwt.split("\\.");
        if (arr.length != 3) {
            throw new RuntimeException("jwt格式不正确");
        }
        return TOKEN_REDIS_NAMESPACE+arr[1];
    }
}