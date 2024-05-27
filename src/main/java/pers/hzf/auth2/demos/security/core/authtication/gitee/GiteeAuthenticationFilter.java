package pers.hzf.auth2.demos.security.core.authtication.gitee;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import pers.hzf.auth2.demos.common.config.OAuth2Config;
import pers.hzf.auth2.demos.common.constants.OAuth2ClientConstants;
import pers.hzf.auth2.demos.common.dto.UserDto;
import pers.hzf.auth2.demos.common.web.R;
import pers.hzf.auth2.demos.security.core.authtication.AbstractLoginFilter;
import pers.hzf.auth2.demos.security.core.authtication.AuthEntryPoint;
import pers.hzf.auth2.demos.security.core.service.JWTAuthService;
import pers.hzf.auth2.demos.security.core.service.UserAuthService;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pers.hzf.auth2.demos.common.constants.OAuth2ClientConstants.*;

/**
 * @author houzhifang
 * @date 2024/5/23 18:51
 */
@Component
@Slf4j
public class GiteeAuthenticationFilter extends AbstractLoginFilter {

    @Resource
    private AuthEntryPoint authEntryPoint;

    protected GiteeAuthenticationFilter(AuthenticationManager authenticationManager,OAuth2Config oAuth2Config) {
        super(new AntPathRequestMatcher(URLUtil.getPath(oAuth2Config.getProviderByPlatform(GITEE).getRedirectUri())));
        this.setAuthenticationManager(authenticationManager);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            log.info("[gitee]登录开始认证");
            if (!HttpMethod.GET.name().equals(request.getMethod())) {
                // 仅支持Get请求
                throw new AuthenticationServiceException(StrUtil.format("【Gitee登录】仅支持get请求,当前为{}请求", request.getMethod()));
            }
//        ?code=c0681f76cb6d0bd72fc54485c8af7e9897726a46ad581641a54b5ffcc2c601f9
            String queryString = request.getQueryString();
            if (StrUtil.isBlank(queryString) || !queryString.contains("code")) {
                throw new AuthenticationServiceException("【Gitee登录】参数错误");
            }
            String code = queryString.split("=")[1];
            log.info("[gitee]code={}", code);
            GiteeAuthenticationToken token = new GiteeAuthenticationToken(code);
            Authentication authResult = getAuthenticationManager().authenticate(token);
            log.debug("【Gitee登录】Authentication success: {}", authResult);
            if (authResult != null) {
                SecurityContextHolder.getContext().setAuthentication(authResult);
            }
            return authResult;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            try {
                authEntryPoint.commence(request, response, new BadCredentialsException(e.getMessage()));
            } catch (Exception ex) {
                log.error("authenticationEntryPoint handler error:{}", ex);
            }
        }
        return null;
    }

}
