package pers.hzf.auth2.demos.security.core.authorization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
 * @date 2024/5/14 17:19
 */
@Component
@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("访问url{}无权限,用户id={},exception={}", request.getRequestURI(), UserContext.get().getId(), accessDeniedException.getMessage());
        R.failed(ErrorCode.FORBIDDEN).to(response);
    }
}
