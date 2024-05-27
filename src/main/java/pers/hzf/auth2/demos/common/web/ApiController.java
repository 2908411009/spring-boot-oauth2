package pers.hzf.auth2.demos.common.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pers.hzf.auth2.demos.common.constants.ErrorCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 公共控制层
 * @author xiangjun
 * @date 2020/11/13 10:15
 */
public class ApiController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    /**
     * 自定义输出
     * 示例: output("",500,"系统错误!")
     * 示例: output("{{'uname':'zhangsan','utype':'admin'}}",0,"登录成功!")
     */
    protected <T> R<T> output(T data, long code, String msg) {
        return R.output(code, msg, data);
    }

    /**
     * 成功(code=0)
     * 示例: success("登录成功")
     */
    protected <T> R<T> success(T data) {
        return R.ok(data);
    }

    /**
     * 失败(code=-1)
     * 示例: failed("token过期")
     */
    protected <T> R<T> failed(String msg) {
        return R.failed(msg);
    }

    /**
     * 失败(code=枚举)
     * 示例: failed(ResultEnum.AUTH_EXPIRED)
     */
    protected <T> R<T> failed(ErrorCode errorCode) {
        return R.failed(errorCode);
    }

    protected int getPage(HttpServletRequest request) {
        //默认第一页
        String page = request.getParameter("page");
        return StringUtils.isEmpty(page) ? 1 : Integer.parseInt(page);
    }

    protected int getPerPage(HttpServletRequest request) {
        //默认每一页10条
        String per_page = request.getParameter("per_page");
        return StringUtils.isEmpty(per_page) ? 10 : Integer.parseInt(per_page);
    }
}
