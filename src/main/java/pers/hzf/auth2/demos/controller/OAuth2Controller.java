package pers.hzf.auth2.demos.controller;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.hzf.auth2.demos.common.web.ApiController;
import pers.hzf.auth2.demos.common.web.R;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author houzhifang
 * @date 2024/5/23 14:57
 */
@RestController
@RequestMapping("/api/bg/auth/oauth2")
public class OAuth2Controller extends ApiController {

    @Resource
    private ClientRegistrationRepository clientRegistrationRepository;

    @RequestMapping({"/{registrationId}"})
    public R<String> loginQR(@PathVariable("registrationId") String registrationId, HttpServletRequest request, HttpServletResponse response) {
        // 生成二维码 用于扫码登录
        return success("https://${qr_code_path}");
    }

}
