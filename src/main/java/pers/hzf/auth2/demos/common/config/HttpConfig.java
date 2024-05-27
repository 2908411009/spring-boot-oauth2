package pers.hzf.auth2.demos.common.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import pers.hzf.auth2.demos.common.utils.HttpUtils;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author houzhifang
 * @date 2024/4/25 16:25
 */
@Configuration
public class HttpConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate(ClientHttpRequestFactory httpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        restTemplate.setInterceptors(interceptors);
        restTemplate.getMessageConverters().add(new Jackson2HttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory(@Qualifier("httpClient") HttpClient httpClient) throws MalformedURLException {
        return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    
    @Bean
    public HttpClient httpClient() throws MalformedURLException {
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(1000)
                .setConnectTimeout(3 * 1000)
                .setSocketTimeout(3 * 1000)
                .setMaxRedirects(10).build();
        HttpUtils httpUtils = HttpUtils.getInstance();
        httpUtils.setRequestConfig(config);
        return httpUtils.init();
    }

}

class Jackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    Jackson2HttpMessageConverter() {
        MediaType[] mediaTypes = new MediaType[]{
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM,
                MediaType.TEXT_HTML,
                MediaType.TEXT_PLAIN,
                MediaType.TEXT_XML,
                MediaType.APPLICATION_ATOM_XML,
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.MULTIPART_FORM_DATA
        };
        this.setSupportedMediaTypes(Arrays.asList(mediaTypes));
    }
}