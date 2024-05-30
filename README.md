# spring-boot-oauth2
SpringBoot集成SpringSecurity实现账户密码登录、及多种OAuth2登录

# oauth2登录页面
```
模板：${baseUrl}/oauth2/authorization/${registrationId}
例如：http://127.0.0.1:19001/oauth2/authorization/gitee

使用Spring Security OAuth2的内置的登录功能
自定义拦截器 拦截登录成功回调
```