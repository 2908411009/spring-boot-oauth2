package pers.hzf.auth2.demos.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.hzf.auth2.demos.common.web.ApiController;
import pers.hzf.auth2.demos.common.web.R;
import pers.hzf.auth2.demos.common.web.UserContext;

/**
 * @author houzhifang
 * @date 2024/5/17 18:16
 */
@RestController
@RequestMapping("/api/bg/user")
public class UserController extends ApiController {

    @GetMapping("/me")
    public R<UserContext> me() {
        return success(UserContext.get());
    }
    
    @PreAuthorize("@ss.hasAuthority('system:user:create')")
    @GetMapping("/create")
    public R<String> create() {
        return success("success");
    }

    @PreAuthorize("@ss.hasAuthority('system:user:list')")
    @GetMapping("/list")
    public R<String> list() {
        return success("success");
    }
    
}
