package pers.hzf.auth2.demos.infra.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.infra.mapper.SystemUsersMapper;
import pers.hzf.auth2.demos.infra.po.SystemUsers;
import pers.hzf.auth2.demos.infra.service.SystemUsersInfraService;

import java.util.Objects;

@Service
public class SystemUsersInfraServiceImpl extends ServiceImpl<SystemUsersMapper, SystemUsers> implements SystemUsersInfraService {

}
