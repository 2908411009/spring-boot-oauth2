package pers.hzf.auth2.demos.security.core.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import pers.hzf.auth2.demos.common.dto.UserDto;

/**
 * @author houzhifang
 * @date 2024/5/13 18:19
 */
public interface UserAuthService {

    UserDetails authenticate(Authentication authentication);

    UserDetails getUserByName(String username);

    UserDto.LoginRes getLoginRes(String username, String token);
    
}
