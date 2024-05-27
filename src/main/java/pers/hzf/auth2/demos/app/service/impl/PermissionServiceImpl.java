package pers.hzf.auth2.demos.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.app.service.PermissionService;
import pers.hzf.auth2.demos.common.enums.CommonStatusEnum;
import pers.hzf.auth2.demos.common.enums.permission.RoleCodeEnum;
import pers.hzf.auth2.demos.common.web.UserContext;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;
import pers.hzf.auth2.demos.infra.po.SystemRoleMenu;
import pers.hzf.auth2.demos.infra.po.SystemRoleUsers;
import pers.hzf.auth2.demos.infra.po.SystemRoles;
import pers.hzf.auth2.demos.infra.po.SystemUsers;
import pers.hzf.auth2.demos.infra.service.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

/**
 * @author houzhifang
 * @date 2024/5/21 10:38
 */
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    @Resource
    SystemUsersInfraService systemUsersInfraService;

    @Resource
    SystemRoleUsersInfraService systemRoleUsersInfraService;

    @Resource
    SystemRolesInfraService systemRolesInfraService;

    @Resource
    SystemRoleMenuInfraService systemRoleMenuInfraService;

    @Override
    public Set<String> getUserRoles(Long userId) {
        List<SystemRoleUsers> systemRoleUsers = systemRoleUsersInfraService.list(SystemRoleUsers::getUserId, userId);
        if (CollUtil.isEmpty(systemRoleUsers)) {
            return Sets.newHashSet();
        }
        return systemRolesInfraService.list(new LambdaQueryWrapper<SystemRoles>()
                .in(SystemRoles::getId, systemRoleUsers.stream().map(x -> x.getRoleId()).collect(Collectors.toSet()))
                .eq(SystemRoles::getStatus, CommonStatusEnum.ENABLE.getStatus())
        ).stream().map(x -> x.getRoleTag()).collect(Collectors.toSet());
    }

    @Override
    public Set<XGrantedAuthority> getUserPermission(Long userId) {
        log.info("获取用户权限,userId={}", userId);
        SystemUsers systemUsers = systemUsersInfraService.getById(userId);
        Assert.notNull(systemUsers);
        // 查询角色
        List<SystemRoleUsers> systemRoleUsers = systemRoleUsersInfraService.list(SystemRoleUsers::getUserId, userId);
        if (CollUtil.isEmpty(systemRoleUsers)) {
            log.info("暂未给用户分配角色,userId={}", userId);
            return Sets.newHashSet();
        }

        Set<Long> roleIds = systemRoleUsers.stream().map(SystemRoleUsers::getRoleId).collect(Collectors.toSet());
        return systemRoleMenuInfraService.getUserPermission(roleIds);
    }

    @Override
    public boolean hasAnyAuthority(String... permissions) {
        // 如果为空，说明已经有权限
        if (ArrayUtil.isEmpty(permissions)) {
            return true;
        }
        UserContext userContext = UserContext.get();
        Set<String> userRoles = userContext.getRoles();
        // 判断是否是超管。如果是，符合条件
        if (hasAnySuperAdmin(userRoles)) {
            return true;
        }
        Set<XGrantedAuthority> authorities = userContext.getAuthorities();
        return authorities.stream().map(x -> x.getAuthority()).anyMatch(auth -> StrUtil.equalsAny(auth, permissions));
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        // 如果为空，说明已经有权限
        if (ArrayUtil.isEmpty(roles)) {
            return true;
        }
        Set<String> userRoles = UserContext.get().getRoles();
        // 判断是否是超管。如果是，符合条件
        if (hasAnySuperAdmin(userRoles)) {
            return true;
        }
        return CollUtil.containsAny(userRoles, Sets.newHashSet(roles));
    }


    private boolean hasAnySuperAdmin(Set<String> roles) {
        if (CollectionUtil.isEmpty(roles)) {
            return false;
        }
        return roles.stream().anyMatch(role -> RoleCodeEnum.isSuperAdmin(role));
    }
}
