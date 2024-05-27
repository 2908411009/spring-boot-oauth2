package pers.hzf.auth2.demos.common.constants;

import lombok.Getter;

/**
 * 接口返回信息枚举
 * (兼容前端历史版本)
 */
@Getter
public enum ErrorCode {

    FAILED(99999, "系统错误"),

    SUCCESS(20000, "执行成功"),

    PARAM(40000, "参数异常"),

    FORBIDDEN(403, "无权限访问"),

    AUTH_FAILED(401, "登录失败"),

    AUTH_INVALID(407, "token无效"),

    AUTH_EXPIRED(407, "token过期"),

    PAGE_NOT_FOUND(404, "not found resource 404 error"),
    USER_NOT_EXIST(41000, "用户名不存在"),
    USER_MOBILE_EXIST(41002, "用户名或者手机账号已被注册"),
    REGISTER_FAILD(41003, "用户注册失败"),
    VERIFACTION_CODE_EXPIRED(41004, "验证码已过期"),
    INCORRECT_FILE_UPLOAD_CONFIGURATION(55002, "文件上传配置有误"),
    UEER_UN_LOGIN(55002, "用户未登录"),
    WECOM_LOGIN_FAILED(56566, "签名验证失败"),
    USER_PATIS_EXIST(41005, "用户名已报名"),
    INSTANCE_NOT_EXISTS(51005, "instance.not.exists"),
    INSTANCE_NOT_CAN_TEST(51027, "instance.can.not.test"),
	TASKS_ALREADY_IN_TESTING(51008, "tasks.already.in.testing"),
	TASKS_CAN_NOT_REVOKED(51009, "任务无法撤销"),
    RESOURCE_NOT_SATISFY(3201, "resource.not_satisfy"),
	TASKS_CAN_NOT_ABORTED(51009, "任务无法中止"),
    TASK_NUM_LIMIT_EXCEED(52000, "任务数量超过限制"),
    MODEL_RECORD_LIMIT_EXCEED(52001, "模型记录数量超过限制"),
    MODEL_STORAGE_SPACE_LIMIT_EXCEED(52002, "模型存储空间超过限制"),
    SIGNATURE_VALIDATE_FAILED(53000, "签名验证失败"),

    QR_CODE_GENERATE_FAILED(54000, "二维码生成失败"),
    QR_CODE_EXPIRE(54001, "二维码过期"),
    ORDER_GENERATE_FAILED(54002, "订单创建失败"),
    PAY_NOTIFY_FAILED(54003, "支付回调失败"),
    ;

    private long code;
    private String msg;

    ErrorCode(long code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
