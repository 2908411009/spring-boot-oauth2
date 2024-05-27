package pers.hzf.auth2.demos.security.core.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 匹配是否静态资源
 */
@Component
public class ResourcesService {
    //注意:exclusions不能定义为static
    @Value("${demo.resources.exclusions}")
    private String exclusions;

    public boolean check_resources(String url) {
        List<String> exclusionsList = Arrays.asList(this.exclusions.split(","));
        String extstr = StringUtils.substringAfterLast(url, ".");
        for (String element : exclusionsList) {
            if (element.indexOf(".") == 0 && StringUtils.equalsIgnoreCase(element, "." + extstr)) {
                return true;
            } else if (element.indexOf("/") == 0 && StringUtils.indexOfIgnoreCase(url, element) != -1) {
                return true;
            }
        }
        return false;
    }

    public String getExclusions() {
        return this.exclusions;
    }

    public void setExclusions(String exclusions) {
        this.exclusions = exclusions;
    }
}
