package pers.hzf.auth2.demos.common.exception;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import pers.hzf.auth2.demos.common.constants.ErrorCode;
import pers.hzf.auth2.demos.common.servlet.ServletUtils;
import pers.hzf.auth2.demos.common.thread.ThreadMdcWrapper;
import pers.hzf.auth2.demos.common.web.R;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static pers.hzf.auth2.demos.common.constants.ErrorCode.*;
import static pers.hzf.auth2.demos.common.web.R.*;

/**
 * @author houzhifang
 * @date 2024/5/13 14:52
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Resource
    AccessDeniedHandler accessDeniedHandler;

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public Object businessExceptionHandler(HttpServletRequest request, BusinessException exception) {
        if (!exception.isNeedWrap()) {
            return BusinessException.ApiResult.build(exception);
        } else {
            return output(exception.getCode(), exception.getMsg(), exception.getData());
        }
    }

    /**
     * @PreAuthorize 注解抛出的异常不会直接进入 accessDeniedHandler
     * 因为他是AOP处理的 所以这里通过全局异常处理的方式捕获 并调用 accessDeniedHandler
     * @param request
     * @param response
     * @param exception
     * @return
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public Object accessDeniedExceptionHandler(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) {
        try {
            accessDeniedHandler.handle(request, response, exception);
            return null;
        } catch (Exception e) {
            return this.errorHandler(request, e);
        }
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object errorHandler(HttpServletRequest request, Exception exception) {
        String url = request.getServletPath();
        String params = ServletUtils.getBodyNotCloseStream(request);
        String token = ServletUtils.obtainBasicAuthorization(request);
        String method = request.getMethod();
        log.info(StrUtil.format("\nmethod: {};\nurl: {};\ntoken: {};\ncontentType: {};\nparams: {}", method, url, token, request.getContentType(), params));
        log.error("系统异常", exception);
        String message = exception.getMessage();
        Throwable throwable = exception.getCause();
        if (StrUtil.isBlank(message)) {
            message = Objects.nonNull(throwable) ? throwable.toString() : StrUtil.EMPTY;
        }
        try {
            if (exception instanceof MethodArgumentTypeMismatchException) {
                return output(PARAM.getCode(), message + ", this args is:" + ((MethodArgumentTypeMismatchException) exception).getName(), "");
            } else if (exception instanceof DataAccessException) {
                message = throwable.toString();
            } else if (throwable instanceof DataAccessException) {
                message = throwable.getCause().toString();
            } else if (exception instanceof AccessDeniedException) {
                // 无权限
                return failed(FORBIDDEN);
            } else if (exception instanceof HttpMediaTypeException || exception instanceof NoHandlerFoundException) {
                // 404
                return failed(PAGE_NOT_FOUND);
            } else if (exception instanceof ArithmeticException || exception instanceof IndexOutOfBoundsException || throwable instanceof ArithmeticException || throwable instanceof IndexOutOfBoundsException) {
                //系统错误
                return failed(ErrorCode.FAILED);
            } else {
                R r = paramValidExceptionHandler(exception);
                if (r != null) {
                    return r;
                }
            }
            return failed(FAILED);
        } finally {
            ThreadMdcWrapper.clear();
        }
    }


    private R paramValidExceptionHandler(Exception exception) {
        String errorMsg = "";
        if (exception instanceof BindException) {
            BindException ex = (BindException) exception;
            errorMsg = getErrorDetails(ex.getBindingResult().getAllErrors());
            return R.output(PARAM.getCode(), errorMsg);
        } else if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) exception;
            errorMsg = getErrorDetails(ex.getAllErrors());
            return R.output(PARAM.getCode(), errorMsg);
        } else if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) exception;
            Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
            errorMsg = constraintViolations.stream().map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("; "));
            return R.output(PARAM.getCode(), errorMsg);
        }
        return null;
    }

    private String getErrorDetails(List<ObjectError> allErrors) {
        List<String> errors = new ArrayList<>();
        for (ObjectError allError : allErrors) {
            Object[] args = allError.getArguments();
            String msg = "";
            String exceptionField = "";
            for (Object arg : args) {
                if (arg instanceof DefaultMessageSourceResolvable) {
                    DefaultMessageSourceResolvable resolvable = (DefaultMessageSourceResolvable) arg;
                    exceptionField += resolvable.getDefaultMessage() + "";
                }
            }
            log.warn("param valid exception field: {}", exceptionField);
            msg += allError.getDefaultMessage();
            errors.add(msg);
        }
        return CollUtil.join(errors, "; ");
    }

}


@Slf4j
@RestController
class ErrorController extends BasicErrorController {

    public ErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        log.info("[ErrorController]处理Filter异常,uri={}", request.getRequestURI());
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        log.debug("[ErrorController]处理Filter异常,body={}", JSONUtil.toJsonStr(body));
        HttpStatus status = getStatus(request);
        HashMap<String, Object> map = MapUtil.of("code", FAILED.getCode());
        map.put("msg", body.get("message"));
        ThreadMdcWrapper.clear();
        return new ResponseEntity(map, status);
    }
}
