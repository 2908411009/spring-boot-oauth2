package pers.hzf.auth2.demos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.hzf.auth2.demos.common.web.ApiController;
import pers.hzf.auth2.demos.common.web.R;
import pers.hzf.auth2.demos.common.web.UserContext;

/**
 * @author houzhifang
 * @date 2024/5/14 17:57
 */
@RequestMapping("/api/bg/auth")
@RestController
public class UserAuthenticationController extends ApiController {

    @PostMapping("/login")
    public R login(String password, String username, String key, String captchaValue) {
        return success(true);
    }

    @PostMapping("/logout")
    public R logout() {
        return success(true);
    }

}
