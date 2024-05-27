package pers.hzf.auth2.demos.common.web;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pers.hzf.auth2.demos.common.constants.ErrorCode;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * 重写API接口的基础返回类
 * @author houzhifang
 * @since 2024/05/26
 */
@Slf4j
@Data
@NoArgsConstructor
//@ApiModel
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "错误代码(0成功;-1失败;401登录失败;402会话过期;403无访问权限;)", example = "402", required = true)
    private long code;

//    @ApiModelProperty(value = "提示信息", example = "会话已过期,请重新登录.")
    private String message;

//    @ApiModelProperty(value = "数据信息")
    private T data;

    public R(long code, String msg, T data) {
        this.code = code;
        this.data = data;
        this.message = msg;
    }

    /**
     * 自定义输出
     * 示例: R.output("{{'uname':'zhangsan','utype':'admin'}}",0,"登录成功!")
     */
    public static R output(long code, String msg) {
        return output(code,msg, StrUtil.EMPTY);
    }
    
    /**
     * 自定义输出
     * 示例: R.output("{{'uname':'zhangsan','utype':'admin'}}",0,"登录成功!")
     */
    public static <T> R<T> output(long code, String msg, T data) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(msg);
        return apiResult;
    }

    /**
     * 成功(code=0)
     * 示例: success("登录成功")
     */
    public static <T> R<T> ok(T data) {
        ErrorCode aec = ErrorCode.SUCCESS;
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            aec = ErrorCode.FAILED;
        }
        return restResult(data, aec);
    }

    /**
     * 成功(code=0)
     * 示例: success("登录成功")
     */
    public static <T> R<T> ok(String msg,T data) {
        return output(ErrorCode.SUCCESS.getCode(),msg, data);
    }

    /**
     * 失败(code=-1)
     * 示例: R.failed("token过期")
     */
    public static <T> R<T> failed(String msg) {
        return output(ErrorCode.FAILED.getCode(), msg, null);
    }

    /**
     * 失败(code=枚举)
     * 示例: R.failed(ResultEnum.AUTH_EXPIRED)
     */
    public static <T> R<T> failed(ErrorCode errorCode) {
        return restResult(null, errorCode);
    }

    private static <T> R<T> restResult(T data, ErrorCode errorCode) {
        return output(errorCode.getCode(), errorCode.getMsg(), data);
    }

    private boolean ok() {
        return ErrorCode.SUCCESS.getCode() == this.code;
    }

    /**
     * 手动编码输出json
     */
    public void to(ServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String message = JacksonUtils.getInstance().toJson(this);
            response.setBufferSize(message.getBytes("utf-8").length);
            out.println(message);
            response.flushBuffer();
            out.flush();
        } catch (Exception e) {
            log.error(e + "输出JSON出错");
        }
    }
}
