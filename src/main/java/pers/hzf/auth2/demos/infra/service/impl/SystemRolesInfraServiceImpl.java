package pers.hzf.auth2.demos.infra.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.common.enums.CommonStatusEnum;
import pers.hzf.auth2.demos.infra.mapper.SystemRolesMapper;
import pers.hzf.auth2.demos.infra.po.SystemRoles;
import pers.hzf.auth2.demos.infra.service.SystemRolesInfraService;

import java.util.Collection;
import java.util.List;

@Service
public class SystemRolesInfraServiceImpl extends ServiceImpl<SystemRolesMapper, SystemRoles> implements SystemRolesInfraService {
    @Override
    public List<SystemRoles> listByEnable(Collection<Long> ids) {
        return baseMapper.selectList(new LambdaQueryWrapper<SystemRoles>()
                .in(SystemRoles::getId, ids)
                .eq(SystemRoles::getStatus, CommonStatusEnum.ENABLE.getStatus()));
    }
}
