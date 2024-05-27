package pers.hzf.auth2.demos.common.template;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pers.hzf.auth2.demos.common.config.OAuth2Config;
import pers.hzf.auth2.demos.common.dto.GiteeDto.UserInfo;

import javax.annotation.Resource;

/**
 * @author houzhifang
 * @date 2024/5/24 14:47
 */
@Component
@Slf4j
public class GiteeTemplate {


    @Resource
    private OAuth2Config oAuth2Config;

    @Resource
    private RestTemplate restTemplate;

    private static final String BASE_HOST = "https://gitee.com/api/v5/";

    public UserInfo getUserInfo(String accessToken) {
        try {
            log.info("[Gitee]get user info token {}", accessToken);
            String api = StrUtil.format("{}user?access_token={}", BASE_HOST, accessToken);
            return restTemplate.getForObject(api, UserInfo.class);
        } catch (RestClientException e) {
            log.error("[Gitee]get user info error", e);
            throw new RuntimeException(e);
        }
    }
}

