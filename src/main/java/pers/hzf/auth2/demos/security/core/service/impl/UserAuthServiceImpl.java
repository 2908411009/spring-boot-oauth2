package pers.hzf.auth2.demos.security.core.service.impl;

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
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.app.service.PermissionService;
import pers.hzf.auth2.demos.app.service.SystemUserService;
import pers.hzf.auth2.demos.common.dto.UserDto;
import pers.hzf.auth2.demos.common.enums.UserTypeEnum;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;
import pers.hzf.auth2.demos.common.web.UserInfoDetails;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;
import pers.hzf.auth2.demos.infra.po.SystemUsers;
import pers.hzf.auth2.demos.infra.service.SystemUsersInfraService;
import pers.hzf.auth2.demos.security.core.service.AbstractUserAuthService;
import pers.hzf.auth2.demos.security.core.service.UserAuthService;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

/**
 * @author houzhifang
 * @date 2024/5/13 18:19
 */
@Slf4j
@Service("userAuthService")
public class UserAuthServiceImpl extends AbstractUserAuthService {

    @Resource
    private SystemUserService systemUserService;

    @Override
    public UserDetails authenticate(Authentication authentication) {
        log.info("authentication: {}", authentication);
        UserDetails userDetails = null;
        try {
            String username = (String) authentication.getPrincipal();
            String password = (String) authentication.getCredentials();
            SystemUsers systemUsers = systemUserService.getUserByIdentity(username);
            if (Objects.isNull(systemUsers)) {
                throw new BadCredentialsException("用户不存在");
            }
            if (!DigestUtil.bcryptCheck(password, systemUsers.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            // 这里不进行权限查询 在后续流程中查询
            userDetails = new User(username, password, Sets.newHashSet());
        } catch (Exception e) {
            log.error("authentication error:", e);
            throw new BadCredentialsException(e.getMessage());
        }
        return userDetails;
    }

    @Override
    public boolean supports(String registrationId) {
        return false;
    }

}
