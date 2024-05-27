package pers.hzf.auth2.demos.security.core.authtication;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.common.constants.ErrorCode;
import pers.hzf.auth2.demos.common.web.R;
import pers.hzf.auth2.demos.common.web.UserContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author houzhifang
 * @date 2024/5/14 17:18
 */
@Component
@Slf4j
public class AuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String msg;
        if (authException instanceof CredentialsExpiredException
                || authException instanceof UsernameNotFoundException
                || authException instanceof BadCredentialsException) {
            msg = "用户名或密码错误";
        } else if (authException instanceof InsufficientAuthenticationException) {
            msg = "未登录";
        } else {
            msg = authException.getMessage();
        }
        long code = ErrorCode.AUTH_INVALID.getCode();
        new R(code, msg, "").to(response);
    }
}
