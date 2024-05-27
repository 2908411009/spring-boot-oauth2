package pers.hzf.auth2.demos.security.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.app.service.PermissionService;
import pers.hzf.auth2.demos.common.web.UserContext;
import pers.hzf.auth2.demos.security.core.service.SecurityFrameworkService;

import javax.annotation.Resource;


/**
 * 默认的 {@link SecurityFrameworkService} 实现类
 *
 * @author yshop
 */
@Service("ss")
@Slf4j
public class SecurityFrameworkServiceImpl implements SecurityFrameworkService {

    @Resource
    private PermissionService permissionService;

    @Override
    public boolean hasAuthority(String permission) {
        return hasAnyAuthority(permission);
    }

    @Override
    public boolean hasAnyAuthority(String... permissions) {
        return permissionService.hasAnyAuthority(permissions);
    }

    @Override
    public boolean hasRole(String role) {
        return hasAnyRoles(role);
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        return permissionService.hasAnyRoles(roles);
    }


}
