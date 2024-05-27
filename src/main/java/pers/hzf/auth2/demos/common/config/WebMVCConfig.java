package pers.hzf.auth2.demos.common.config;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import pers.hzf.auth2.demos.common.utils.JacksonUtils;

import java.util.List;

/**
 * @author houzhifang
 * @date 2024/4/25 16:11
 */
@Slf4j
@Configuration
public class WebMVCConfig extends WebMvcConfigurationSupport {

    @Value("${demo.resources.exclusions}")
    public String exclusions;

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/", "classpath:/META-INF/resources");
        super.addResourceHandlers(registry);
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(JacksonUtils.getInstance());
        converters.add(jsonConverter);
        super.addDefaultHttpMessageConverters(converters);
    }

    public static void main(String[] args) {
        User1 user1 = new User1();
        user1.setName("张三");

        User2 user2 = BeanUtil.toBean(user1, User2.class);
        System.out.println(user2.getName());

    }
    
    @Data
    public static class User1 {
        private String name;
    }


    @Data
    public static class User2 {
        private String name;
    }
    
}
