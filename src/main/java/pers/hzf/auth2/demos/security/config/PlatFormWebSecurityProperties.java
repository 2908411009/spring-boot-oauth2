package pers.hzf.auth2.demos.security.config;

import lombok.Data;
import pers.hzf.auth2.demos.common.enums.ClientEnum;

/**
 * @author houzhifang
 * @date 2024/5/15 10:17
 */
@Data
public class PlatFormWebSecurityProperties {

    private String loginUrl;

    private String logoutUrl;

    private ClientEnum client;
    
}
