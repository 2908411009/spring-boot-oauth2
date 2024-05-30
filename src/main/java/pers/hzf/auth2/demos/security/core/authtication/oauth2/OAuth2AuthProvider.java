package pers.hzf.auth2.demos.security.core.authtication.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.security.core.service.UserAuthService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author houzhifang
 * @date 2024/5/24 14:45
 */
@Component
@Slf4j
public class OAuth2AuthProvider extends DaoAuthenticationProvider {

    @Resource
    private List<UserAuthService> userAuthServices;

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("[OAuth2AuthProvider][authenticate]{}", authentication);
        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        ClientRegistration clientRegistration = auth2AuthenticationToken.getClientRegistration();
        UserDetails userDetails = null;
        for (UserAuthService userAuthService : userAuthServices) {
            if (userAuthService.supports(clientRegistration.getRegistrationId())) {
                userDetails = userAuthService.authenticate(authentication);
                if (Objects.nonNull(userDetails)) {
                    break;
                }
            }
        }
        if (Objects.isNull(userDetails)) {
            log.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.noopBindAccount", "Noop Bind Account"));
        }
        return this.createSuccessAuthentication(userDetails, authentication, userDetails);
    }

    @Override
    protected void doAfterPropertiesSet() {
        
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(OAuth2AuthenticationToken.class);
    }

}
