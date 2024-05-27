package pers.hzf.auth2.demos.infra.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.common.enums.CommonStatusEnum;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;
import pers.hzf.auth2.demos.infra.mapper.SystemRoleMenuMapper;
import pers.hzf.auth2.demos.infra.po.SystemRoleMenu;
import pers.hzf.auth2.demos.infra.service.SystemRoleMenuInfraService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class SystemRoleMenuInfraServiceImpl extends ServiceImpl<SystemRoleMenuMapper, SystemRoleMenu> implements SystemRoleMenuInfraService {
    
    @Override
    public Set<XGrantedAuthority> getUserPermission(Collection<Long> roleIds) {
        return baseMapper.selectListByRoleIds(roleIds, CommonStatusEnum.ENABLE.getStatus());
    }
}
