package pers.hzf.auth2.demos.infra.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.hzf.auth2.demos.common.mybatis.IServiceX;
import pers.hzf.auth2.demos.infra.po.SystemRoles;
import pers.hzf.auth2.demos.infra.po.SystemUsers;

import java.util.Collection;
import java.util.List;

public interface SystemRolesInfraService extends IServiceX<SystemRoles> {

    /**
     * 获取启用的角色列表
     * @param ids
     * @return
     */
    List<SystemRoles> listByEnable(Collection<Long> ids);
    
}
