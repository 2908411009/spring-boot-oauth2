package pers.hzf.auth2.demos.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 全局用户类型枚举
 */
@AllArgsConstructor
@Getter
public enum UserTypeEnum {

    MEMBER(1, "USER_TYPE_MEMBER","用户"), // 面向 c 端，普通用户
    ADMIN(2, "USER_TYPE_ADMIN","管理员"); // 面向 b 端，管理后台

    /**
     * 类型
     */
    private final Integer value;
    /**
     * 用于区分前后台用户账号
     */
    private final String code;
    /**
     * 类型名
     */
    private final String name;

}
