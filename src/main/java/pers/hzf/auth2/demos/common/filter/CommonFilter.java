package pers.hzf.auth2.demos.common.filter;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartResolver;
import pers.hzf.auth2.demos.security.core.service.ResourcesService;
import pers.hzf.auth2.demos.common.thread.ThreadMdcWrapper;
import pers.hzf.auth2.demos.common.tools.MyRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 公共过滤器
 *
 * @author houzhifang
 * @date 2024/4/25 16:43
 */
@Slf4j
@Component
public class CommonFilter extends OncePerRequestFilter {
    @Autowired
    private ResourcesService resourcesService;
    @Autowired
    private MultipartResolver multipartResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //1.静态资源放行
        String url = request.getRequestURL().toString();
        if (resourcesService.check_resources(url)) {
            filterChain.doFilter(request, response);
            return;
        }
        // 加入mdc traceId
        ThreadMdcWrapper.putIfAbsentByThreadPool();

        // 2. 自定义业务逻辑
        String method = request.getMethod();
        String queryString = request.getQueryString();
        url = url + StrUtil.blankToDefault(queryString, StrUtil.EMPTY);
        log.info("请求url {} {}", method, url);

        String token = StrUtil.blankToDefault(request.getHeader("token"), request.getHeader(HttpHeaders.AUTHORIZATION));
        String contentType = request.getContentType();
        log.info(StrUtil.format("请求头\n method: {};\n token: {}; \n contentType: {};", method, token, contentType));

        String params;
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (!MapUtil.isEmpty(parameterMap)) {
            params = JSONUtil.toJsonStr(parameterMap);
            log.info("请求参数\n params: {}", params);
        }
        //设置跨域
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

        // 3. 转发给后续请求
        if (multipartResolver.isMultipart(request)) {
            filterChain.doFilter(multipartResolver.resolveMultipart(request), response);
        } else if (!StrUtil.isBlank(request.getHeader("tus-resumable")) || contentType != null && contentType.toLowerCase().startsWith("application/x-www-form-urlencoded")) {
            filterChain.doFilter(request, response);
        } else {
            MyRequestWrapper requestWrapper = new MyRequestWrapper(request);
            if (contentType != null && StringUtils.startsWithAny(contentType, new String[]{MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})) {
                params = IOUtils.toString(requestWrapper.getInputStream(), StandardCharsets.UTF_8).replace("\\s+", "");
                log.info("请求参数\n params: {}", params);
            }
            filterChain.doFilter(request, response);
        }
        ThreadMdcWrapper.clear();
    }
}
