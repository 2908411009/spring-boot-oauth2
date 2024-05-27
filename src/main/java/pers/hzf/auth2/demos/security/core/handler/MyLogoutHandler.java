package pers.hzf.auth2.demos.security.core.handler;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.security.core.service.JWTAuthService;
import pers.hzf.auth2.demos.common.servlet.ServletUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author houzhifang
 * @date 2024/5/22 11:14
 */
@Slf4j
@Component
public class MyLogoutHandler implements LogoutHandler {
    
    @Resource
    JWTAuthService jwtAuthService;
    
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = ServletUtils.obtainBasicAuthorization(request);
        log.info("user logout token {}", token);
        if (StrUtil.isNotBlank(token)) {
            jwtAuthService.removeUserContext(token);
        }
    }
}
