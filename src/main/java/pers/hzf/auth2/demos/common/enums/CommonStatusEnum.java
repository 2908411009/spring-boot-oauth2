package pers.hzf.auth2.demos.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author houzhifang
 * @date 2024/5/20 18:21
 */
@AllArgsConstructor
@Getter
public enum CommonStatusEnum {


    ENABLE(1, "开启"),
    DISABLE(0, "关闭");

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;
}
