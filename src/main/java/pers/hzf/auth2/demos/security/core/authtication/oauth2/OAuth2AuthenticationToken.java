package pers.hzf.auth2.demos.security.core.authtication.oauth2;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author houzhifang
 * @date 2024/5/29 18:08
 */
public class OAuth2AuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    private ClientRegistration clientRegistration;

    private HttpServletRequest request;

    public OAuth2AuthenticationToken(Object principal, ClientRegistration clientRegistration) {
        super(null);
        this.principal = principal;
        this.clientRegistration = clientRegistration;
    }

    public OAuth2AuthenticationToken(Object principal, ClientRegistration clientRegistration, HttpServletRequest request) {
        super(null);
        this.principal = principal;
        this.clientRegistration = clientRegistration;
        this.request = request;
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public OAuth2AuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return new HashMap<>();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public HttpServletRequest getRequest() {
        return request;
    }
}