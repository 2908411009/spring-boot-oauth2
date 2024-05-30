package pers.hzf.auth2.demos.app.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pers.hzf.auth2.demos.app.service.SystemUserService;
import pers.hzf.auth2.demos.common.dto.GiteeDto;
import pers.hzf.auth2.demos.infra.po.SystemUsers;
import pers.hzf.auth2.demos.infra.service.SystemUsersInfraService;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author houzhifang
 * @date 2024/5/24 15:49
 */
@Service
@Slf4j
public class SystemUserServiceImpl implements SystemUserService {

    @Resource
    SystemUsersInfraService systemUsersInfraService;

    @Override
    public SystemUsers getGiteeUserIfAbsent(GiteeDto.UserInfo userInfo) {
        String email = userInfo.getEmail();
        SystemUsers systemUsers = systemUsersInfraService.getOne(SystemUsers::getEmail, email);
        if (Objects.isNull(systemUsers)) {
            systemUsers = new SystemUsers();
            // 此处应当调用注册方法 这里为了方便直接创建用户 生产环境不应该这么写
            systemUsers.setEmail(email);
            systemUsers.setAvatar(userInfo.getAvatar_url());
            systemUsers.setPassword(DigestUtil.bcrypt(RandomUtil.randomString(6)));
            systemUsers.setName(userInfo.getName());
            systemUsers.setUsername(userInfo.getLogin());
            systemUsersInfraService.save(systemUsers);
        }
        return systemUsers;
    }

    @Override
    public SystemUsers getUserByIdentity(String identity) {
        LambdaQueryWrapper<SystemUsers> queryWrapper = new LambdaQueryWrapper<SystemUsers>()
                .eq(SystemUsers::getUsername, identity)
                .or()
                .eq(SystemUsers::getPhone, identity)
                .or()
                .eq(SystemUsers::getEmail, identity)
                .last("limit 1");
        return systemUsersInfraService.getOne(queryWrapper);
    }

}
