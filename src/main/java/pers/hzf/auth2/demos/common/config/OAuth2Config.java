package pers.hzf.auth2.demos.common.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author houzhifang
 * @date 2024/5/24 14:50
 */
@ConfigurationProperties(prefix = "demo.oauth2")
@Configuration
@Data
public class OAuth2Config {

    private Map<String, Provider> provider = new HashMap<>();
    
    public Provider getProviderByPlatform(String platform){
        return provider.get(platform);
    }
    
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Provider {
        /**
         * clientId
         */
        String clientId;
        /**
         * secret
         */
        String clientSecret;
        /**
         * 认证uri
         */
        String authorizationUri;
        /**
         * 登录后回调地址
         */
        String redirectUri;
        /**
         * 第三方平台名称
         */
        String clientName;
    }

}
