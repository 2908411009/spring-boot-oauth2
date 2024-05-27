package pers.hzf.auth2.demos.infra.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;
import pers.hzf.auth2.demos.infra.mapper.SystemRoleUsersMapper;
import pers.hzf.auth2.demos.infra.po.SystemRoleUsers;
import pers.hzf.auth2.demos.infra.service.SystemRoleUsersInfraService;

import java.util.Collection;
import java.util.List;

@Service
public class SystemRoleUsersInfraServiceImpl extends ServiceImpl<SystemRoleUsersMapper, SystemRoleUsers> implements SystemRoleUsersInfraService {

}
