package pers.hzf.auth2.demos.security.core.authtication.credentials;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * 用户名 密码 验证码 登录
 *
 * @author houzhifang
 * @date 2024/5/27 15:59
 */
public class VerificationCodeAuthenticationToken extends UsernamePasswordAuthenticationToken {

    public VerificationCodeAuthenticationToken(Object principal, Object credentials, String codeKey, String code) {
        super(principal, credentials);
        this.code = code;
        this.codeKey = codeKey;
    }

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码唯一标识
     */
    private String codeKey;

    public String getCode() {
        return code;
    }

    public String getCodeKey() {
        return codeKey;
    }
}
