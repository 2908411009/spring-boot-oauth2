package pers.hzf.auth2.demos.app.service;

import pers.hzf.auth2.demos.common.web.XGrantedAuthority;

import java.util.List;
import java.util.Set;

/**
 * @author houzhifang
 * @date 2024/5/21 10:36
 */
public interface PermissionService {

    Set<String> getUserRoles(Long userId);
    
    Set<XGrantedAuthority> getUserPermission(Long userId);

    /**
     * 判断是否有权限，任一一个即可
     *
     * @param userId 用户编号
     * @param permissions 权限
     * @return 是否
     */
    boolean hasAnyAuthority(String... permissions);

    /**
     * 判断是否有角色，任一一个即可
     *
     * @param roles 角色数组
     * @return 是否
     */
    boolean hasAnyRoles(String... roles);
    
}
