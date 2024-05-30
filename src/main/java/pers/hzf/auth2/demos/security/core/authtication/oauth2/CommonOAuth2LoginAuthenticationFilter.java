package pers.hzf.auth2.demos.security.core.authtication.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.common.servlet.ServletUtils;
import pers.hzf.auth2.demos.security.core.authtication.AbstractLoginFilter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;


/**
 * @author houzhifang
 * @date 2024/5/29 17:26
 */
@Component
@Slf4j
public class CommonOAuth2LoginAuthenticationFilter extends AbstractLoginFilter {

    public static final String DEFAULT_FILTER_PROCESSES_URI = "/api/bg/login/oauth2/code/*";

    @Resource
    ClientRegistrationRepository clientRegistrationRepository;

    protected CommonOAuth2LoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_FILTER_PROCESSES_URI);
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            String requestURI = request.getRequestURI();
            log.info("[oauth2]登录开始认证,path={}", requestURI);
            Map<String, String> paramMap = ServletUtils.getParamMapMultiMethod(request);
            if (!paramMap.containsKey(OAuth2ParameterNames.CODE)) {
                throw new OAuth2AuthenticationException("[oauth2]参数错误");
            }
            // 获取 registrationId
            String registrationId = getRegistrationId(requestURI);
            ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
            if (Objects.isNull(clientRegistration)) {
                throw new OAuth2AuthenticationException("Client Registration not found with Id: " + registrationId);
            }
            OAuth2AuthenticationToken authenticationRequest = new OAuth2AuthenticationToken(paramMap.get(OAuth2ParameterNames.CODE),clientRegistration);
            log.info("[oauth2]authenticationRequest={}", authenticationRequest);
            Authentication authResult = this.getAuthenticationManager().authenticate(authenticationRequest);
            log.debug("【oauth2】Authentication success: {}", authResult);
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
    
    
    private String getRegistrationId(String requestURI){
        return StringUtils.substringAfterLast(requestURI, "/");
    }
    
}
