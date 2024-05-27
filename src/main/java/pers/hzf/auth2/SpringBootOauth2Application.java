package pers.hzf.auth2;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import pers.hzf.auth2.demos.common.enums.ClientEnum;
import pers.hzf.auth2.demos.security.core.annotations.EnablePlatformSecurity;

import java.util.UUID;

@SpringBootApplication
@ComponentScan(basePackages = {"pers.hzf"})
@EnablePlatformSecurity(client = ClientEnum.CV_BG)
public class SpringBootOauth2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootOauth2Application.class, args);
    }

}
