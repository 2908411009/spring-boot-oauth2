package pers.hzf.auth2.demos.common.web;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 重写SimpleGrantedAuthority用于jackson反序列化
 */
public class XGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private final String authority;

    public XGrantedAuthority() {
        this(null);
    }

    public XGrantedAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    @Override
    public int hashCode() {
        return this.authority.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SimpleGrantedAuthority) {
            return this.authority.equals(((XGrantedAuthority) obj).authority);
        }

        return false;
    }

    @Override
    public String toString() {
        return this.authority;
    }
}
