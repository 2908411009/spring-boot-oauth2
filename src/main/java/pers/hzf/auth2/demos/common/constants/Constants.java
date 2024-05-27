package pers.hzf.auth2.demos.common.constants;

/**
 * @author houzhifang
 * @date 2024/4/24 17:40
 */
public interface Constants {

    String TRACE_ID = "X-B3-TraceId";

    public final static String USERNAME = "username";

    public final static String PASSWORD = "password";

    //权限验证-白名单,支持正则
    public final static String[] AuthWhiteList = {"/error.*", ".*/callback.*", ".*/check.*", ".*/analysis/.*", ".*/export.*", ".*/import.*", "/doc.html"
            ,".*/api/ft/auth.*", ".*/ft/basic-data.*",".*/fc.*",".*/skiping.*",".*/bbs.*",".*/api/ft/open.*",".*/probe.*",".*/api/bg/auth.*",".*/pay/notify/.*",".*/.*socket.*"
    };
}
