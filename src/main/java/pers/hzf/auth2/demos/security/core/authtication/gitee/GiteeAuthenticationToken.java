package pers.hzf.auth2.demos.security.core.authtication.gitee;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;
import java.util.HashMap;

/**
 * 企业微信扫码登录
 *
 * @author houzhifang
 * @date 2024/5/23 16:29
 */
public class GiteeAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final Object principal;

    public GiteeAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public GiteeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
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
}
