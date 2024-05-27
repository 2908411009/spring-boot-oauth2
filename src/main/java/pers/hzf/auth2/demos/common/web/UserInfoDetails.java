package pers.hzf.auth2.demos.common.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserInfoDetails extends User {
    private String avatar;
    private String email;
    private String mobile;
    private Long userId;
    private String actualName;
    private Set<String> roles;

    public UserInfoDetails(Long userId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public UserInfoDetails(Long userId, String username, String actualName, String password, Collection<? extends GrantedAuthority> authorities) {
        this(userId,username, password, authorities);
        this.actualName = actualName;
    }


}
