package pers.hzf.auth2.demos.infra.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.infra.mapper.SystemMenuMapper;
import pers.hzf.auth2.demos.infra.po.SystemMenu;
import pers.hzf.auth2.demos.infra.service.SystemMenuInfraService;

@Service
public class SystemMenuInfraServiceImpl extends ServiceImpl<SystemMenuMapper, SystemMenu> implements SystemMenuInfraService {
}
