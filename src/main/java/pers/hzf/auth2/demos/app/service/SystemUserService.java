package pers.hzf.auth2.demos.app.service;

import pers.hzf.auth2.demos.common.dto.GiteeDto;
import pers.hzf.auth2.demos.infra.po.SystemUsers;

/**
 * @author houzhifang
 * @date 2024/5/24 15:48
 */
public interface SystemUserService {

    SystemUsers getGiteeUserIfAbsent(GiteeDto.UserInfo userInfo);

    SystemUsers getUserByIdentity(String identity);
    
}
