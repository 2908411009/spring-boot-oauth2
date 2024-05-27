package pers.hzf.auth2.demos.infra.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import pers.hzf.auth2.demos.common.enums.CommonStatusEnum;
import pers.hzf.auth2.demos.common.enums.permission.MenuTypeEnum;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author houzhifang
 * @date 2024/5/15 10:17
 */
@Data
@TableName("t_system_menu")
public class SystemMenu extends BasePO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 菜单名称
     */
    private String name;
    /**
     * 权限标识
     * 一般格式为：${系统}:${模块}:${操作}
     * 例如说：system:admin:add，即 system 服务的添加管理员。
     * 当我们把该 MenuDO 赋予给角色后，意味着该角色有该资源：
     * - 对于后端，配合 @PreAuthorize 注解，配置 API 接口需要该权限，从而对 API 接口进行权限控制。
     * - 对于前端，配合前端标签，配置按钮是否展示，避免用户没有该权限时，结果可以看到该操作。
     */
    private String permission;
    /**
     * 菜单类型
     * 枚举 {@link MenuTypeEnum}
     */
    private Integer type;

    /**
     * 显示顺序
     */
    private Integer sort;
    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 路由地址
     * 如果 path 为 http(s) 时，则它是外链
     */
    private String path;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 组件路径
     */
    private String component;
    /**
     * 组件名
     */
    private String componentName;
    /**
     * 状态
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 是否可见
     * 只有菜单、目录使用
     * 当设置为 true 时，该菜单不会展示在侧边栏，但是路由还是存在。例如说，一些独立的编辑页面 /edit/1024 等等
     */
    private Boolean visible;
    /**
     * 是否缓存
     *
     * 只有菜单、目录使用，否使用 Vue 路由的 keep-alive 特性
     * 注意：如果开启缓存，则必须填写 {@link #componentName} 属性，否则无法缓存
     */
    private Boolean keepAlive;
    /**
     * 是否总是显示
     *
     * 如果为 false 时，当该菜单只有一个子菜单时，不展示自己，直接展示子菜单
     */
    private Boolean alwaysShow;

}
