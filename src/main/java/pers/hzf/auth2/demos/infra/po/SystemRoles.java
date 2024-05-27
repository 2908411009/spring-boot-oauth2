package pers.hzf.auth2.demos.infra.po;

import com.baomidou.mybatisplus.annotation.*;
import pers.hzf.auth2.demos.common.enums.CommonStatusEnum;
import pers.hzf.auth2.demos.common.enums.permission.RoleTypeEnum;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * @author houzhifang
 * @date 2024/5/15 10:17
 */
@Data
@TableName("t_system_roles")
public class SystemRoles  extends BasePO {
    public static final long SUPER_ADMIN_ROLE_ID = 1L;  // 超级管理员角色ID
    public static final int STATUS_AVAILABLE = 1;
    public static final int STATUS_UNAVAILABLE = 0;

    /**
     * 角色ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 角色标识
     */
    private String roleTag;
    /**
     * 角色排序
     */
    private Integer sort;
    /**
     * 角色说明
     */
    private String description;
    /**
     * 角色状态 1 开启 0关闭
     * 枚举{@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 角色类型
     *
     * 枚举 {@link RoleTypeEnum}
     */
    private Integer type;
}
