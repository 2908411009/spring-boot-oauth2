package pers.hzf.auth2.demos.security.core.authtication.gitee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.common.template.GiteeTemplate;
import pers.hzf.auth2.demos.security.core.service.UserAuthService;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author houzhifang
 * @date 2024/5/24 14:45
 */
@Component
@Slf4j
public class GiteeAuthProvider implements AuthenticationProvider {

    @Resource
    private UserAuthService giteeAuthService;

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("[GiteeAuthProvider][authenticate]{}", authentication);
        UserDetails userDetails = giteeAuthService.authenticate(authentication);
        if (Objects.isNull(userDetails)) {
            log.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.noopBindAccount", "Noop Bind Account"));
        }
        return this.createSuccessAuthentication(userDetails, authentication, userDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(GiteeAuthenticationToken.class);
    }

    private Authentication createSuccessAuthentication(Object principal, Authentication authentication,
                                                         UserDetails user) {
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal,authentication.getCredentials(), user.getAuthorities());
        result.setDetails(authentication.getDetails());
        log.debug("Authenticated user");
        return result;
    }

}
