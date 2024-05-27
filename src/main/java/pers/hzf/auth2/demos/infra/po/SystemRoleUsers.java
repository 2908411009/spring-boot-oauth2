package pers.hzf.auth2.demos.infra.po;

import com.baomidou.mybatisplus.annotation.*;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author houzhifang
 * @date 2024/5/15 10:17
 */
@Data
@TableName("t_system_role_users")
public class SystemRoleUsers extends BasePO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 用户id
     */
    private Long userId;
}
