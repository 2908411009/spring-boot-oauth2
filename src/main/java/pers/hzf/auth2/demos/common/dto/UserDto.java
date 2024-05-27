package pers.hzf.auth2.demos.common.dto;

import lombok.Data;

/**
 * @author houzhifang
 * @date 2024/5/15 16:35
 */
public class UserDto {

    @Data
    public static class LoginRes {

//        @ApiModelProperty(value = "用户名")
        private String username;

//        @ApiModelProperty(value = "token")
        private String accessToken;

//        @ApiModelProperty(value = "tokenType")
        private String tokenType = "bearer";

//        @ApiModelProperty(value = "有效期时长")
        private long expiresIn;
        
//        @ApiModelProperty(value = "角色类型，默认普通角色。0普通角色 1 管理员 2 超级管理员，")
        private Integer roleType = 0;
    }
    
}
