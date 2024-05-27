package pers.hzf.auth2.demos.common.utils;


import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.MDC;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author houzhifang
 * @date 2024/4/25 16:33
 */
@Slf4j
@Data
public class HttpUtils {

    private String traceId = "traceId";
    private String keyStorePath;
    private String keyStorepass;
    private RequestConfig requestConfig;
    private CredentialsProvider provider;
    private HttpClientBuilder clientBuilder;
    //重试策略: 所有请求都重试(特定场景如docker push api需要设置为true),默认false,只有连接异常或GET请求才会重试
    private boolean unsafeRetry = false;
    //重试策略: 所有请求都不重试(特定场景如中间件监控需要设置为true),如果unsafeRetry和noRetry都为true冲突时,以unsafeRetry的值为准
    private boolean noRetry = false;
    private PoolingHttpClientConnectionManager connPool;
    private CloseableHttpClient httpClient;

    public static HttpUtils getInstance() {
        return new HttpUtils();
    }

    /**
     * 从线程池中获取连接对象
     */
    public CloseableHttpClient init() {
        if (this.requestConfig == null) {
            this.requestConfig = getRequestConfig();
        }
        this.connPool = getConnPool(keyStorePath, keyStorepass);
        this.traceId = traceId;
        HttpRequestRetryHandler retry = getRetryHandler();
        if (this.clientBuilder == null) {
            this.clientBuilder = this.getClientBuilder();
        }
        httpClient = clientBuilder
                //连接池
                .setConnectionManager(connPool)
                //连接重用策略,默认60s
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                //连接最大存活时间,默认-1无限存活,从创建连接开始计时,在网络中断而server无法感知的情形非常有用.
                .setConnectionTimeToLive(60, TimeUnit.SECONDS)
                //定时清理空闲连接的间隔时间,默认间隔10秒
                .evictIdleConnections(10, TimeUnit.SECONDS)
                //开启过期连接清理,默认间隔5秒
                .evictExpiredConnections()
                //默认请求配置(超时/重定向次数)
                .setDefaultRequestConfig(requestConfig)
                //共享模式
                .setConnectionManagerShared(false)
                //重试策略
                .setRetryHandler(retry)
                //身份验证
                .setDefaultCredentialsProvider(provider)
                .build();
        this.shutdownHook();
        return httpClient;
    }

    /**
     * 连接池
     */
    private PoolingHttpClientConnectionManager getConnPool(String keyStorePath, String keyStorepass) {
        try {
            ConnectionSocketFactory connectionSocketFactory = null;
            if (!StrUtil.isEmpty(keyStorePath)) {
                connectionSocketFactory = createSSLConnSocketFactory(keyStorePath, keyStorepass);
            } else {
                connectionSocketFactory = createSSLConnSocketFactory();
            }
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", connectionSocketFactory)
                    .build();
            connPool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            //线程池配置:全局最大连接数,默认10,高于这个值时,新连接请求,需要阻塞排队等待
            connPool.setMaxTotal(200);
            //线程池配置:单路由(相同ip:port)最大连接数,默认5
            connPool.setDefaultMaxPerRoute(20);
            //在从连接池获取连接时,连接不活跃多长时间后需要进行一次验证,默认为2s
            connPool.setValidateAfterInactivity(1000);
            //关闭已经空闲超过10s的连接
            connPool.closeIdleConnections(10, TimeUnit.SECONDS);
            //关闭超过ConnectionTimeToLive/KeepAliveStrategy的连接
            connPool.closeExpiredConnections();
            //设置到某个路由的最大连接数
            //connManager.setMaxPerRoute(new HttpRoute(new HttpHost("www.baidu.com", 80)), 150);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return connPool;
    }

    /**
     * HttpClientBuilder默认配置
     */
    public HttpClientBuilder getClientBuilder() {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create().addInterceptorFirst((new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                if (!StrUtil.isEmpty(traceId)) {
                    String traceIdX = MDC.get(traceId);
                    if (traceIdX != null) {
                        httpRequest.addHeader(traceId, traceIdX);
                    }
                }
            }
        }));
        return clientBuilder;
    }

    /**
     * request默认配置
     */
    public RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                //从连接池中获取连接的超时时间(ms),超过该时间未拿到可用连接，会抛出异常:Timeout waiting for connection from pool
                .setConnectionRequestTimeout(1000)
                //连接超时(ms),建议3000
                .setConnectTimeout(3 * 1000)
                //响应超时(ms),建议15000,阻塞等待永不超时,请填写0
                .setSocketTimeout(5 * 1000)
                //最大的redirect次数,默认50
                .setMaxRedirects(10)
                .build();
    }

    /**
     * 重试策略
     */
    private HttpRequestRetryHandler getRetryHandler() {
        return new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (unsafeRetry == false && noRetry == true) {
                    //所有请求都不重试
                    return false;
                }
                if (executionCount > 3) {
                    //如果已经重试了3次,就放弃
                    return false;
                }
                log.info("重试第 {} 次, 上一次异常信息:\n {}", executionCount, ExceptionUtil.stacktraceToString(exception));
                if (exception instanceof UnknownHostException) {
                    //Unknown host 不重试
                    return false;
                } else if (exception instanceof SSLException || exception instanceof SSLHandshakeException) {
                    //SSL handshake exception  不重试
                    return false;
                } else if (exception instanceof SocketTimeoutException) {
                    //Socket timeout 响应超时不重试,避免造成业务数据不一致
                    return false;
                } else {
                    if (unsafeRetry == true) {
                        //可以重试
                        return true;
                    } else {
                        if (exception instanceof ConnectTimeoutException) {
                            //Connection timeout 连接可以重试
                            return true;
                        } else if (exception instanceof NoHttpResponseException) {
                            //NoHttpResponse 可以重试
                            return true;
                        } else {
                            //判断请求方式
                            HttpClientContext clientContext = HttpClientContext.adapt(context);
                            HttpRequest request = clientContext.getRequest();
                            //HttpEntityEnclosingRequest指的是有请求体的request,如:PUT/POST/DELETE
                            if (request instanceof HttpEntityEnclosingRequest == false) {
                                //如果是幂等的(GET/HEAD/OPTIONS), 可以重试
                                return true;
                            }
                        }
                    }
                }
                //默认:不重试
                return false;
            }
        };
    }

    private void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (httpClient != null) {
                try {
                    httpClient.close();
                    log.info(">>>>httpClient closed<<<<");
                } catch (Exception e) {
                    log.warn("httpClient close exception", e);
                }
            }
        }, "srsynchronize-httpInvoker-shutdown-thread"));
    }

    public String doGet(String url, Map<String, String> param, Map<String, String> head) {
        String resultString = null;
        try {
            try (CloseableHttpResponse response = doGetAndGetResponse(url, param, head);) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return resultString;
    }

    public CloseableHttpResponse doGetAndGetResponse(String url, Map<String, String> param, Map<String, String> head) {
        try {
            log.debug("url: {}, param: {}, head: {}", url, param, head);
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();
            log.info("uri:{}", uri);
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setProtocolVersion(HttpVersion.HTTP_1_0);
            if (head != null) {
                for (String key : head.keySet()) {
                    httpGet.addHeader(key, head.get(key));
                }
            }
            //实际执行的是InternalHttpClient.execute方法
            CloseableHttpResponse response = httpClient.execute(httpGet);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String doUpload(String url, HttpEntity httpEntity, Map<String, String> head) {
        String resultString = null;
        try {
            try (CloseableHttpResponse response = doUploadAndGetResponse(url, httpEntity, head);) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return resultString;
    }

    public CloseableHttpResponse doUploadAndGetResponse(String url, HttpEntity httpEntity, Map<String, String> head) {
        try {
            log.debug("url: {}, param: {}, head: {}", url, httpEntity, head);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
            if (head != null) {
                for (String key : head.keySet()) {
                    httpPost.addHeader(key, head.get(key));
                }
            }
            httpPost.setEntity(httpEntity);
            //实际执行的是InternalHttpClient.execute方法
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String doPost(String url, Map<String, String> param, Map<String, String> head) {
        String resultString = null;
        try {
            try (CloseableHttpResponse response = doPostAndGetResponse(url, param, head);) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return resultString;
    }

    public CloseableHttpResponse doPostAndGetResponse(String url, Map<String, String> param, Map<String, String> head) {
        try {
            log.debug("url: {}, param: {}, head: {}", url, param, head);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                if (head != null) {
                    for (String key : head.keySet()) {
                        httpPost.addHeader(key, head.get(key));
                    }
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            //实际执行的是InternalHttpClient.execute方法
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String doPostJson(String url, String json, Map<String, String> head) {
        String resultString = null;
        try {
            try (CloseableHttpResponse response = doPostJsonAndGetResponse(url, json, head);) {
                resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return resultString;
    }

    public CloseableHttpResponse doPostJsonAndGetResponse(String url, String json, Map<String, String> head) {
        try {
            log.debug("url: {}, param: {}, head: {}", url, json, head);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setProtocolVersion(HttpVersion.HTTP_1_0);
            if (head != null) {
                for (String key : head.keySet()) {
                    httpPost.addHeader(key, head.get(key));
                }
            }
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            //实际执行的是InternalHttpClient.execute方法
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return response;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public String getBody(CloseableHttpResponse response) {
        String resultString = null;
        try {
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
            response.close();
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return resultString;
    }

    /**
     * 忽略SSL验证
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        //信任所有
                        return true;
                    }
                }).build();
        // ALLOW_ALL_HOSTNAME_VERIFIER:这个主机名验证器基本上是关闭主机名验证的,实现的是一个空操作，并且不会抛出javax.net.ssl.SSLException异常。
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1.2","TLSv1.1"}, null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return socketFactory;
    }

    /**
     * 创建SSL安全连接
     */
    private SSLConnectionSocketFactory createSSLConnSocketFactory(String keyStorePath, String keyStorepass) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream instream = new FileInputStream(new File(keyStorePath));
        trustStore.load(instream, keyStorepass.toCharArray());
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        return socketFactory;
    }

    public String doGet(String url) {
        return doGet(url, null, null);
    }

    public String doPost(String url) {
        return doPost(url, null, null);
    }

    public String doPostJson(String url) {
        return doPostJson(url, null, null);
    }

    public String doGet(String url, Map<String, String> param) {
        return doGet(url, param, null);
    }

    public String doPost(String url, Map<String, String> param) {
        return doPost(url, param, null);
    }

    public String doPostJson(String url, String json) {
        return doPostJson(url, json, null);
    }

    public static long ip2Long(String strIP) {
        long[] ip = new long[4];
        //先找到IP地址字符串中.的位置
        int position1 = strIP.indexOf(".");
        int position2 = strIP.indexOf(".", position1 + 1);
        int position3 = strIP.indexOf(".", position2 + 1);
        //将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIP.substring(0, position1));
        ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIP.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    public static String long2IP(long longIP) {
        StringBuffer sb = new StringBuffer("");
        //直接右移24位
        sb.append(String.valueOf(longIP >>> 24));
        sb.append(".");
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(longIP & 0x000000FF));
        return sb.toString();
    }
}
