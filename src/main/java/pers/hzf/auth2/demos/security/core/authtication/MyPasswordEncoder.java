package pers.hzf.auth2.demos.security.core.authtication;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * SpringSecurity 加密器
 *
 * @author houzhifang
 * @date 2024/5/13 16:02
 */
@Component
@Slf4j
public class MyPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return DigestUtil.bcrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return DigestUtil.bcryptCheck(rawPassword.toString(), encodedPassword);
    }
}
