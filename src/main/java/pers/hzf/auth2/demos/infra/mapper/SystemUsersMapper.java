package pers.hzf.auth2.demos.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.hzf.auth2.demos.common.mybatis.BaseMapperX;
import pers.hzf.auth2.demos.infra.po.SystemUsers;

@Mapper
public interface SystemUsersMapper extends BaseMapperX<SystemUsers> {
}
