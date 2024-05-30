package pers.hzf.auth2.demos.security.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.*;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import pers.hzf.auth2.demos.common.constants.Constants;
import pers.hzf.auth2.demos.common.enums.UserTypeEnum;
import pers.hzf.auth2.demos.security.core.authtication.oauth2.CommonOAuth2LoginAuthenticationFilter;
import pers.hzf.auth2.demos.security.core.filter.AuthTokenFilter;
import pers.hzf.auth2.demos.security.core.authtication.credentials.CredentialsAuthFilter;
import pers.hzf.auth2.demos.security.core.handler.MyLogoutHandler;
import pers.hzf.auth2.demos.security.core.handler.MyLogoutSuccessHandler;
import pers.hzf.auth2.demos.security.core.authtication.AuthEntryPoint;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author houzhifang
 * @date 2024/5/13 16:10
 * SpringSecurity 配置类
 */
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class PlatFormWebSecurityConfigurerAdapter {

    @Resource
    PlatFormWebSecurityProperties platFormWebSecurityProperties;

    @Lazy
    @Resource
    CredentialsAuthFilter credentialsAuthFilter;

    @Lazy
    @Resource
    CommonOAuth2LoginAuthenticationFilter commonOAuth2LoginAuthenticationFilter;

    @Resource
    AuthTokenFilter authTokenFilter;

    @Resource
    AuthEntryPoint authEntryPoint;

    @Resource
    AccessDeniedHandler accessDeniedHandler;

    @Resource
    MyLogoutHandler myLogoutHandler;

    @Resource
    MyLogoutSuccessHandler myLogoutSuccessHandler;


//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
    /**
     * 务必自行创建 如果让 Spring Security创建 使用多个 AuthenticationProvider bean 时 会出现StackOverFlow异常
     * @param authenticationProviders
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) throws Exception {
        return new ProviderManager(authenticationProviders);
    }

    /**
     * 通过 AuthorizationFilter 授权HttpServletRequest
     *
     * @param introspector
     * @return
     * @document https://docs.spring.io/spring-security/reference/5.7/servlet/authorization/authorize-http-requests.html
     */
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> requestMatcherAuthorizationManager(HandlerMappingIntrospector introspector) {
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        // 静态资源直接放行
        requestMatchers.addAll(Lists.newArrayList("/*.html", "/**/*.html", "/**/*.css", "/**/*.js")
                .stream().map(x -> new AntPathRequestMatcher(x, HttpMethod.GET.name()))
                .collect(Collectors.toList()));
        // 一些白名单规则
        requestMatchers.addAll(Arrays.stream(Constants.AuthWhiteList).map(pattern -> new RegexRequestMatcher(pattern, null)).collect(Collectors.toList()));
        // Option请求直接放行
        requestMatchers.add(new AntPathRequestMatcher("/**", HttpMethod.OPTIONS.name()));
        // oauth2 放行
        requestMatchers.add(new AntPathRequestMatcher("/oauth2/authorization/**"));
        
        // 免登录规则中 匹配一个就放行
        RequestMatcher permitAll = new OrRequestMatcher(requestMatchers);
        // 前台接口匹配规则
        RequestMatcher front = new MvcRequestMatcher(introspector, "/api/ft/**");
        // 后台接口匹配规则
        RequestMatcher admin = new MvcRequestMatcher(introspector, "/api/bg/**");
        // 除去上面几个规则意外 其他所有的请求
        RequestMatcher any = AnyRequestMatcher.INSTANCE;

        AuthorizationManager<HttpServletRequest> manager = RequestMatcherDelegatingAuthorizationManager.builder()
                // 所有请求直接返回true 免登录访问
                .add(permitAll, (authentication, object) -> new AuthorizationDecision(true))
                // 限制前台接口 只能普通用户访问 UserType在认证时以硬编码的方式添加
                .add(front, AuthorityAuthorizationManager.hasAuthority(UserTypeEnum.MEMBER.getCode()))
                // 限制后台接口 只能管理员用户访问
                .add(admin, AuthorityAuthorizationManager.hasAuthority(UserTypeEnum.ADMIN.getCode()))
                // 其他接口 校验登录
                .add(any, new AuthenticatedAuthorizationManager())
                .build();
        return (authentication, context) -> manager.check(authentication, context.getRequest());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthorizationManager<RequestAuthorizationContext> access) throws Exception {
        // 登出
        httpSecurity
                // 开启跨域
                .cors().and()
                // CSRF禁用 因为不使用 Session
                .csrf().disable()
                .logout()
                .logoutUrl(platFormWebSecurityProperties.getLogoutUrl())
                .addLogoutHandler(myLogoutHandler)
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .and()
                .oauth2Login()
                .and()
                // 基于 token 机制，所以不需要 Session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .headers().frameOptions().disable().and()
                // 一堆自定义的 Spring Security 处理器
                // 认证失败处理
                .exceptionHandling().authenticationEntryPoint(authEntryPoint)
                // 这里配置的 accessDeniedHandler不会捕获 @PreAuthorize 注解抛出的异常
                // 只会捕获 RequestMatcherDelegatingAuthorizationManager 中使用 hasRole之类的配置的权限抛出的异常
                // 因为使用 @PreAuthorize 注解 实际是 AOP 校验权限 根据就近原则 异常就会进入 @ExceptionHandler 捕获的全局异常
                // 而在 RequestMatcherDelegatingAuthorizationManager 中配置 是使用 AuthorizationFilter 进行拦截授权  
                // 抛出的异常会进入 ExceptionTranslationFilter 拦截器 在这里会调用 accessDeniedHandler 处理异常
                .accessDeniedHandler(accessDeniedHandler);
        // 权限校验
        httpSecurity.authorizeHttpRequests()
                .anyRequest()
                .access(access);
        // 后台用户认证过滤器 如果需要进行前台用户认证 在微服务环境下 重新实现JWTAuthService接口即可
        // 如果是单体应用 则需要再添加一个 Filter 进行前台账户认证
        httpSecurity.addFilterBefore(credentialsAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // gitee认证过滤器 
        httpSecurity.addFilterBefore(commonOAuth2LoginAuthenticationFilter, OAuth2LoginAuthenticationFilter.class);
        // token校验过滤器
        httpSecurity.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
