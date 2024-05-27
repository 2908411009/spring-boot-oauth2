package pers.hzf.auth2.demos.security.core.authtication;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pers.hzf.auth2.demos.common.constants.Constants;
import pers.hzf.auth2.demos.common.dto.UserDto;
import pers.hzf.auth2.demos.common.servlet.ServletUtils;
import pers.hzf.auth2.demos.common.web.R;
import pers.hzf.auth2.demos.security.core.service.JWTAuthService;
import pers.hzf.auth2.demos.security.core.service.UserAuthService;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author houzhifang
 * @date 2024/5/24 15:00
 */
@Slf4j
public abstract class AbstractLoginFilter extends AbstractAuthenticationProcessingFilter {

    @Resource
    JWTAuthService jwtAuthService;

    @Resource
    UserAuthService userAuthService;

    protected AbstractLoginFilter(AntPathRequestMatcher antPathRequestMatcher) {
        super(antPathRequestMatcher);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("successful authentication user: {}", authResult.getDetails());
        UserDto.LoginRes loginRes = this.successfulAuthentication(authResult);
        R.ok("登录成功！", BeanUtil.beanToMap(loginRes, true, true)).to(response);
    }


    private UserDto.LoginRes successfulAuthentication(Authentication auth) {
        User user = (User) auth.getPrincipal();
        log.debug("auth: {}", auth);
        // 派发jwt token
        String token = jwtAuthService.addAuthentication(user);
        jwtAuthService.updateUserContext(auth, token);
        return userAuthService.getLoginRes(user.getUsername(), token);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.debug("unsuccessful authentication user: {}", failed.getMessage());
        SecurityContextHolder.clearContext();
    }
}

