package pers.hzf.auth2.demos.common.exception;
import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import pers.hzf.auth2.demos.common.constants.ErrorCode;

/**
 * 自定义异常类
 */
@Data
public class BusinessException extends RuntimeException {
    private long code;
    private String msg;
    private Object data;
    //是否需要被R类包装,默认true
    private boolean needWrap = true;

    public BusinessException(long code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.data = "";
    }

    public BusinessException(long code, String msg, Object data) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.data = data != null ? data : "";
    }

    public BusinessException(long code, String msg, Object data, boolean needWrap) {
        super(msg);
        this.code = code;
        this.msg = msg;
        this.needWrap = needWrap;
        this.data = data != null ? data : "";
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public BusinessException(ErrorCode errorCode, String msg) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = msg;
    }

    public BusinessException(String msg) {
        super(msg);
        this.code = ErrorCode.FAILED.getCode();
        this.msg = msg;
    }

    public BusinessException(ErrorCode errorCode, Object data) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
        this.data = data != null ? data : "";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

    /**
     * 不需要被R.ApiResult封装时, 可用以下MyException.ApiResult对象来响应
     */
    @Data
    public static class ApiResult {
        private long code;
        private String msg;
        private Object data;
        
        public static ApiResult build(BusinessException exception){
            ApiResult apiResult = new ApiResult();
            BeanUtil.copyProperties(exception,apiResult);
            return apiResult;
        }
        
    }
}
