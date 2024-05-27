package pers.hzf.auth2.demos.common.servlet;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pers.hzf.auth2.demos.common.tools.MyRequestWrapper;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.*;

/**
 * @author houzhifang
 * @date 2024/5/13 15:02
 */
@Slf4j
public class ServletUtils {

    private static final List<String> JSON_CONTENT_TYPE = Lists.newArrayList(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE);

    /**
     * 返回 JSON 字符串
     *
     * @param response 响应
     * @param object   对象，会序列化成 JSON 字符串
     */
    @SuppressWarnings("deprecation") // 必须使用 APPLICATION_JSON_UTF8_VALUE，否则会乱码
    public static void writeJSON(HttpServletResponse response, Object object) {
        String content = JSONUtil.toJsonStr(object);
        ServletUtil.write(response, content, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    /**
     * 返回附件
     *
     * @param response 响应
     * @param filename 文件名
     * @param content  附件内容
     */
    public static void writeAttachment(HttpServletResponse response, String filename, byte[] content) throws IOException {
        // 设置 header 和 contentType
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 输出附件
        IoUtil.write(response.getOutputStream(), false, content);
    }

    /**
     * @param request 请求
     * @return ua
     */
    public static String getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "";
    }

    /**
     * 获得请求
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    public static String getUserAgent() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return getUserAgent(request);
    }

    public static String getClientIP() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return ServletUtil.getClientIP(request);
    }

    public static boolean isJsonRequest(ServletRequest request) {
        return StrUtil.startWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    }

    public static String getBody(HttpServletRequest request) {
        return ServletUtil.getBody(request);
    }

    /**
     * 获取 body 但不关闭流
     *
     * @param request
     * @return
     */
    public static String getBodyNotCloseStream(HttpServletRequest request) {
        try {
            String contentType = request.getContentType();
            Map<String, String> paramMap = getParamMap(request);
            if (MapUtil.isNotEmpty(paramMap)) {
                return JSONUtil.toJsonStr(paramMap);
            } else if (JSON_CONTENT_TYPE.contains(contentType)) {
                ServletInputStream inputStream = new MyRequestWrapper(request).getInputStream();
                return IOUtils.toString(inputStream, UTF_8).replaceAll("\\s+", "");
            } else {
                return StrUtil.EMPTY;
            }
        } catch (IOException e) {
            log.error("[getBodyNotCloseStream]获取body异常", e);
            throw new RuntimeException(e);
        }
    }

    public static byte[] getBodyBytes(HttpServletRequest request) {
        return ServletUtil.getBodyBytes(request);
    }

    public static String getClientIP(HttpServletRequest request) {
        return ServletUtil.getClientIP(request);
    }

    public static Map<String, String> getParamMap(HttpServletRequest request) {
        return ServletUtil.getParamMap(request);
    }

    public static String getRequestHost(HttpServletRequest request) {
        String port = ":" + request.getServerPort();
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
            port = "";
        }
        return request.getScheme() + "://" + request.getServerName() + port;
    }

    public static String obtainBasicAuthorization(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isBlank(token)) {
            token = request.getHeader("token");
        }
        return Optional.ofNullable(token).orElse(StrUtil.EMPTY);
    }

}
