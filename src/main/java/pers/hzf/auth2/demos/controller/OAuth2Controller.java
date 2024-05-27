package pers.hzf.auth2.demos.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.hzf.auth2.demos.common.config.OAuth2Config;
import pers.hzf.auth2.demos.common.exception.BusinessException;
import pers.hzf.auth2.demos.common.web.ApiController;
import pers.hzf.auth2.demos.common.web.R;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author houzhifang
 * @date 2024/5/23 14:57
 */
@RestController
@RequestMapping("/api/bg/auth/oauth2")
public class OAuth2Controller extends ApiController {

    @Resource
    private OAuth2Config oAuth2Config;

    @RequestMapping({"/{platform}"})
    public R<String> gitee(@PathVariable("platform") String platform) {
        OAuth2Config.Provider provider = oAuth2Config.getProviderByPlatform(platform);
        Assert.isTrue(Objects.nonNull(provider), () -> new BusinessException("配置不存在"));

        String clientId = provider.getClientId();

        String authorizationUri = provider.getAuthorizationUri();

        return success(StrUtil.format("{}?client_id={}&redirect_uri={}&response_type=code", authorizationUri
                , clientId, URLUtil.encode(provider.getRedirectUri())));
    }

}
