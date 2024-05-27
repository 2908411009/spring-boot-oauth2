package pers.hzf.auth2.demos.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.hzf.auth2.demos.common.mybatis.BaseMapperX;
import pers.hzf.auth2.demos.common.web.XGrantedAuthority;
import pers.hzf.auth2.demos.infra.po.SystemRoleMenu;
import pers.hzf.auth2.demos.infra.po.SystemRoles;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper
public interface SystemRoleMenuMapper extends BaseMapperX<SystemRoleMenu> {
    Set<XGrantedAuthority> selectListByRoleIds(@Param("roleIds") Collection<Long> roleIds, @Param("roleStatus") Integer roleStatus);
}
