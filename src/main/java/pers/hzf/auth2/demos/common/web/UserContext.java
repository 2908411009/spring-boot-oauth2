package pers.hzf.auth2.demos.common.web;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author houzhifang
 * @date 2024/5/13 16:12
 */
@Data
@NoArgsConstructor
public class UserContext extends User {
    
    private String token;

    /**
     * token过期时间-格式化:用于调试
     */
    private String expire;

    /**
     * token过期时间-时间戳:精确到秒
     */
    private Long expireTime;

    /**
     * 角色列表
     */
    private Set<String> roles;

    /**
     * 权限列表
     */
    private Set<XGrantedAuthority> authorities;

    //存储临时key-value
    private Map<String, Object> customMap = new ConcurrentHashMap<>();

    //当前线程(包括子线程以及线程池的中的子线程)的上下文
    private static ThreadLocal<UserContext> content = new TransmittableThreadLocal<>();

    public static UserContext get() {
        return content.get();
    }

    public static Long getUserId() {
        return content.get().getId();
    }

    public static void set(UserContext context) {
        content.set(context);
    }

    public static void remove() {
        content.remove();
    }

    public static void put(String key, Object value) {
        UserContext.get().getCustomMap().put(key, value);
    }

    public static Object get(String key) {
        return UserContext.get().getCustomMap().get(key);
    }
    
}
