package pers.hzf.auth2.demos.common.enums;

public enum ClientEnum {
    CV_BG("/api/bg/auth/login", "/api/bg/auth/logout"),//后台管理系统
    CV_FT("/api/ft/auth/login", "/api/ft/auth/logout");//前台系统
    private String loginUrl;
    private String logoutUrl;

    private ClientEnum(String loginUrl, String logoutUrl) {
        this.loginUrl = loginUrl;
        this.logoutUrl = logoutUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }
}
