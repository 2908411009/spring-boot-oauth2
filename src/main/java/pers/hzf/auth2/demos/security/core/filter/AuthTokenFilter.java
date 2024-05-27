package pers.hzf.auth2.demos.security.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pers.hzf.auth2.demos.common.exception.BusinessException;
import pers.hzf.auth2.demos.security.core.service.JWTAuthService;
import pers.hzf.auth2.demos.common.web.R;
import pers.hzf.auth2.demos.common.web.UserContext;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author houzhifang
 * @date 2024/5/17 10:45
 * token校验过滤器
 */
@Component
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    @Resource
    JWTAuthService jwtAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.debug("auth token filter url {}", request.getRequestURI());
            Authentication authentication = jwtAuthService.getAuthentication(request, response);
            if(Objects.nonNull(authentication)){
                SecurityContextHolder.getContext().setAuthentication(authentication);    
            }
            filterChain.doFilter(request,response);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                BusinessException exception = (BusinessException) e;
                // filter抛出的异常不会进入@ExceptionHandler注解的异常处理方法，所以直接在这响应
                new R(exception.getCode(), exception.getMessage(), "").to(response);
            } else {
                throw e;
            }
        } finally {
            UserContext.remove();
            SecurityContextHolder.clearContext();
        }

    }
}
