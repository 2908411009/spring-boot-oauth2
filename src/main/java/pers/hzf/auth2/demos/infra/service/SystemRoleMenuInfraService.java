package pers.hzf.auth2.demos.infra.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.hzf.auth2.demos.common.mybatis.IServiceX;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;
import pers.hzf.auth2.demos.infra.po.SystemRoleMenu;
import pers.hzf.auth2.demos.infra.po.SystemUsers;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SystemRoleMenuInfraService extends IServiceX<SystemRoleMenu> {
    Set<XGrantedAuthority> getUserPermission(Collection<Long> roleIds);
}
