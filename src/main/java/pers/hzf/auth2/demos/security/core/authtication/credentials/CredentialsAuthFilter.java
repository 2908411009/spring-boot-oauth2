package pers.hzf.auth2.demos.security.core.authtication.credentials;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.common.constants.Constants;
import pers.hzf.auth2.demos.security.core.authtication.AbstractLoginFilter;
import pers.hzf.auth2.demos.common.servlet.ServletUtils;
import pers.hzf.auth2.demos.security.core.authtication.AuthEntryPoint;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author houzhifang
 * @date 2024/5/13 16:49
 */
@Component
@Slf4j
public class CredentialsAuthFilter extends AbstractLoginFilter {

    @Resource
    AuthEntryPoint authEntryPoint;
    
    @Resource
    CredentialsAuthProvider credentialsAuthProvider;

    /**
     * 通过启动服务时 获取到的登录请求url 创建 AntPathRequestMatcher bean
     *
     * @param antPathRequestMatcher
     */
    protected CredentialsAuthFilter(AntPathRequestMatcher antPathRequestMatcher, AuthenticationManager authenticationManager) {
        super(antPathRequestMatcher);
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            log.info("[JWTLoginFilter]request url {}", request.getRequestURI());
            String body = ServletUtils.getBodyNotCloseStream(request);
            log.info(body);
            Map<String, String> creds = JSONUtil.toBean(body, Map.class);
            String username = creds.get(Constants.USERNAME);
            String password = creds.get(Constants.PASSWORD);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

            Authentication authResult = credentialsAuthProvider.authenticate(authRequest);
            log.debug("Authentication success: {}", authResult);
            if (authResult != null) {
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
            return authResult;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            try {
                authEntryPoint.commence(request, response, new BadCredentialsException(e.getMessage()));
            } catch (Exception ex) {
                log.error("authenticationEntryPoint handler error:{}", ex);
            }
        }
        return null;
    }

}
