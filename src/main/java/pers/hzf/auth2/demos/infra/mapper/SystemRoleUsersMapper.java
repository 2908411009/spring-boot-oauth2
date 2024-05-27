package pers.hzf.auth2.demos.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.hzf.auth2.demos.common.mybatis.BaseMapperX;
import pers.hzf.auth2.demos.infra.po.SystemRoleUsers;
import pers.hzf.auth2.demos.infra.po.SystemRoles;

@Mapper
public interface SystemRoleUsersMapper extends BaseMapperX<SystemRoleUsers> {
}
