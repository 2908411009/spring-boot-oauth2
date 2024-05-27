package pers.hzf.auth2.demos.security.core.annotations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pers.hzf.auth2.demos.common.enums.ClientEnum;
import pers.hzf.auth2.demos.security.config.PlatFormWebSecurityProperties;

/**
 * @author houzhifang
 * @date 2024/5/13 16:56
 */
@Slf4j
public class SecurityBeanRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

        ClientEnum client = (ClientEnum) metadata.getAnnotationAttributes(EnablePlatformSecurity.class.getName())
                .get("client");
        beanDefinition.setBeanClass(PlatFormWebSecurityProperties.class);
        beanDefinition.getPropertyValues().add("loginUrl", client.getLoginUrl());
        beanDefinition.getPropertyValues().add("logoutUrl", client.getLogoutUrl());
        beanDefinition.getPropertyValues().add("client", client);
        registry.registerBeanDefinition("platFormWebSecurityProperties", beanDefinition);
        log.info("registry PlatFormWebSecurityProperties BeanDefinition");

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(AntPathRequestMatcher.class);
        beanDefinitionBuilder.addConstructorArgValue(client.getLoginUrl());
        log.info("registry AntPathRequestMatcher BeanDefinition");
        registry.registerBeanDefinition("antPathRequestMatcher", beanDefinitionBuilder.getBeanDefinition());
    }
}
