package pers.hzf.auth2.demos.infra.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author houzhifang
 * @date 2024/5/15 10:17
 */
@Data
@TableName("t_system_users")
public class SystemUsers implements Serializable {
    private static final long serialVersionUID = 655442777574169366L;

    public static final long SUPER_ADMIN_USER_ID = 1L;  // 超级管理员用户ID

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户名
     */
    @TableField("`name`")
    private String name;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 修改时间
     */
    private Date updatedAt;
    /**
     * 删除时间
     */
    @TableLogic
    private LocalDateTime deletedAt;


    @Override
    public String toString() {
        return JacksonUtils.toJson(this);
    }
}
