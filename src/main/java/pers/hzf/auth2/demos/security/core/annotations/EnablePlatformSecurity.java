package pers.hzf.auth2.demos.security.core.annotations;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import pers.hzf.auth2.demos.common.enums.ClientEnum;

import java.lang.annotation.*;

/**
 * 开启后台登录
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Configurable
@Import(SecurityBeanRegister.class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public @interface EnablePlatformSecurity {

    ClientEnum client() default ClientEnum.CV_BG;
}
