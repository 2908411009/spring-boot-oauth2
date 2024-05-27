package pers.hzf.auth2.demos.security.core.authorization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorityAuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.common.enums.UserTypeEnum;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author houzhifang
 * @date 2024/5/22 16:06
 */
@Slf4j
public class UserTypeAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    UserTypeEnum userTypeEnum;
    
    public UserTypeAuthorizationManager(UserTypeEnum userTypeEnum) {
        this.userTypeEnum = userTypeEnum; 
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier, RequestAuthorizationContext context) {
        Authentication authentication = supplier.get();
        log.info("[UserTypeAuthorizationManager] authentication {}",authentication);
        boolean contains = authentication.getAuthorities().stream().map(x -> x.getAuthority()).collect(Collectors.toList()).contains(userTypeEnum.getCode());
        return new AuthorizationDecision(contains);
    }
}
