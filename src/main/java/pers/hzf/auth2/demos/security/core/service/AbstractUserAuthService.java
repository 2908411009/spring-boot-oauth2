package pers.hzf.auth2.demos.security.core.service;

import cn.hutool.core.date.SystemClock;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import pers.hzf.auth2.demos.app.service.PermissionService;
import pers.hzf.auth2.demos.app.service.SystemUserService;
import pers.hzf.auth2.demos.common.dto.UserDto;
import pers.hzf.auth2.demos.common.enums.UserTypeEnum;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;
import pers.hzf.auth2.demos.common.web.UserInfoDetails;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;
import pers.hzf.auth2.demos.infra.po.SystemUsers;
import pers.hzf.auth2.demos.infra.service.SystemUsersInfraService;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

/**
 * @author houzhifang
 * @date 2024/5/24 15:56
 */
@Slf4j
public abstract class AbstractUserAuthService implements UserAuthService {

    @Resource
    private SystemUserService systemUserService;

    @Resource
    private PermissionService permissionService;

    @Value("${ev.token.expire-time:28800}")
    private long expireTime;


    @Override
    public UserDetails getUserByName(String identity) {
        SystemUsers systemUsers = systemUserService.getUserByIdentity(identity);
        if (Objects.isNull(systemUsers)) {
            return null;
        }
        Set<String> roles = permissionService.getUserRoles(systemUsers.getId());
        Set<XGrantedAuthority> grantedAuthorities = permissionService.getUserPermission(systemUsers.getId());
        // 授予后台权限
        grantedAuthorities.add(new XGrantedAuthority(UserTypeEnum.ADMIN.getCode()));
        UserInfoDetails user = new UserInfoDetails(systemUsers.getId(), systemUsers.getUsername(), systemUsers.getPassword(), grantedAuthorities);
        user.setMobile(systemUsers.getPhone());
        user.setEmail(systemUsers.getEmail());
        user.setActualName(systemUsers.getAvatar());
        user.setRoles(roles);
        return user;
    }

    @Override
    public UserDto.LoginRes getLoginRes(String username, String token) {
        UserDto.LoginRes res = new UserDto.LoginRes();
        res.setAccessToken(token);
        res.setUsername(username);
        res.setExpiresIn(SystemClock.now() / 1000 + this.expireTime);
        log.info("loginRes => {}", JacksonUtils.toJson(res));
        return res;
    }

}
