package pers.hzf.auth2.demos.infra.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.hzf.auth2.demos.common.mybatis.IServiceX;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;
import pers.hzf.auth2.demos.infra.po.SystemRoleUsers;
import pers.hzf.auth2.demos.infra.po.SystemUsers;

import java.util.Collection;
import java.util.List;

public interface SystemRoleUsersInfraService extends IServiceX<SystemRoleUsers> {
}
