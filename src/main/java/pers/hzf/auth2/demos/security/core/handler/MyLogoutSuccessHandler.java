package pers.hzf.auth2.demos.security.core.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.common.constants.ErrorCode;
import pers.hzf.auth2.demos.common.web.R;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author houzhifang
 * @date 2024/5/22 11:14
 */
@Slf4j
@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        new R(ErrorCode.SUCCESS.getCode(), "注销成功", null).to(response);
    }
}
