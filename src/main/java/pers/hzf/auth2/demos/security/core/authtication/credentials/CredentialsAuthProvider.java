package pers.hzf.auth2.demos.security.core.authtication.credentials;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.security.core.service.UserAuthService;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author houzhifang
 * @date 2024/5/13 16:26
 * 自定义认证产品
 * 通过Dao方式获取用户名密码进行校验
 */
@Slf4j
@Component
public class CredentialsAuthProvider extends DaoAuthenticationProvider {

    @Resource
    private UserAuthService userAuthService;

    /**
     * 当前认证产品仅支持 用户名 密码的方式
     *
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("[AuthProvider][authenticate]{}", authentication);
        UserDetails userDetails = userAuthService.authenticate(authentication);
        if (Objects.isNull(userDetails)) {
            log.debug("Authentication failed: no credentials provided");
            throw new BadCredentialsException(messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.noopBindAccount", "Noop Bind Account"));
        }
        return this.createSuccessAuthentication(userDetails, authentication, userDetails);
    }

    @Override
    protected void doAfterPropertiesSet() {
        // 重写此方法， 用户跳过校验 userDetailsService
    }
}
