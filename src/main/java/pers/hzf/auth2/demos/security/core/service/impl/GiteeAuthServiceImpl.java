package pers.hzf.auth2.demos.security.core.service.impl;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.app.service.SystemUserService;
import pers.hzf.auth2.demos.common.constants.OAuth2ClientConstants;
import pers.hzf.auth2.demos.common.template.GiteeTemplate;
import pers.hzf.auth2.demos.infra.po.SystemUsers;
import pers.hzf.auth2.demos.security.core.service.AbstractUserAuthService;

import javax.annotation.Resource;

import static pers.hzf.auth2.demos.common.dto.GiteeDto.*;

/**
 * @author houzhifang
 * @date 2024/5/13 18:19
 */
@Slf4j
@Service("giteeAuthService")
public class GiteeAuthServiceImpl extends AbstractUserAuthService {

    @Resource
    private GiteeTemplate giteeTemplate;
    @Resource
    private SystemUserService systemUserService;

    @Override
    public UserDetails authenticate(Authentication authentication) {
        log.info("authentication: {}", authentication);
        String userCode = (String) authentication.getPrincipal();
        // 获取gitee用户
        UserInfo userInfo = null;
        try {
            // 获取 token
            AccessToken accessToken = giteeTemplate.getAccessToken(userCode);
            userInfo = giteeTemplate.getUserInfo(accessToken.getAccess_token());
            log.info("gitee user info {}", userInfo);
        } catch (Exception e) {
            log.error("gitee get user info error", e);
            throw new BadCredentialsException("【Gitee】获取用户信息失败");
        }
        // 根据gitee的 邮箱 获取本系统用户
        SystemUsers systemUsers = systemUserService.getGiteeUserIfAbsent(userInfo);

        // 这里不进行权限查询 在后续流程中查询
        return new User(systemUsers.getUsername(), systemUsers.getPassword(), Sets.newHashSet());
    }

    @Override
    public boolean supports(String registrationId) {
        return registrationId.equals(OAuth2ClientConstants.GITEE);
    }

}
