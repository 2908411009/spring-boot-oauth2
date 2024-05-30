package pers.hzf.auth2.demos.common.template;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pers.hzf.auth2.demos.common.config.OAuth2Config;
import pers.hzf.auth2.demos.common.constants.OAuth2ClientConstants;
import pers.hzf.auth2.demos.common.dto.GiteeDto;
import pers.hzf.auth2.demos.common.dto.GiteeDto.UserInfo;

import javax.annotation.Resource;
import java.util.HashMap;

import static pers.hzf.auth2.demos.common.dto.GiteeDto.*;

/**
 * @author houzhifang
 * @date 2024/5/24 14:47
 */
@Component
@Slf4j
public class GiteeTemplate {


    @Resource
    private ClientRegistrationRepository clientRegistrationRepository;

    @Resource
    private RestTemplate restTemplate;

    private static final String BASE_HOST = "https://gitee.com/api/v5/";

    public AccessToken getAccessToken(String code) {
        try {
            log.info("[Gitee]get user token code {}", code);
            ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(OAuth2ClientConstants.GITEE);
            HashMap<String, String> param = new HashMap<>();
            param.put("grant_type","authorization_code");
            param.put("code",code);
            param.put("client_id",registration.getClientId());
            param.put("redirect_uri",registration.getRedirectUri());
            param.put("client_secret",registration.getClientSecret());
            AccessToken accessToken = restTemplate.postForObject(registration.getProviderDetails().getTokenUri(), param, AccessToken.class);
            log.info("[Gitee]get user access token result {}",accessToken);
            return accessToken;
        } catch (RestClientException e) {
            log.error("[Gitee]get user token error", e);
            throw new RuntimeException(e);
        }
    }
    

    public UserInfo getUserInfo(String accessToken) {
        try {
            log.info("[Gitee]get user info token {}", accessToken);
            String api = StrUtil.format("{}user?access_token={}", BASE_HOST, accessToken);
            UserInfo userInfo = restTemplate.getForObject(api, UserInfo.class);
            log.info("[Gitee]get user info result {}",accessToken);
            return userInfo;
        } catch (RestClientException e) {
            log.error("[Gitee]get user info error", e);
            throw new RuntimeException(e);
        }
    }
}

