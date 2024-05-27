package pers.hzf.auth2.demos.common.web;
import lombok.Data;

/**
 * xxx
 * @author houzhifang
 * @date 2024/05/26
 */
//@ApiModel
@Data
public class User {

//    @ApiModelProperty("用户id")
    private Long id;

//    @ApiModelProperty("用户名")
    private String username;

//    @ApiModelProperty("密码")
    private String password;

//    @ApiModelProperty("手机")
    private String mobile;

//    @ApiModelProperty("邮箱")
    private String email;

//    @ApiModelProperty("头像")
    private String avatar;

//    @ApiModelProperty("昵称")
    private String nickname;

//    @ApiModelProperty("真实姓名")
    private String actualName;
}
