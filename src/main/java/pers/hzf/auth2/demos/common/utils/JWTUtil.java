package pers.hzf.auth2.demos.common.utils;

import cn.hutool.core.map.MapUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具
 *
 * @author xiangjun
 * @date 2021/6/2 12:45
 */
@Slf4j
public class JWTUtil {

    private static final String JWT_SECERT = "P@ssw02d,utg6k803n(8hg}ziuy73ks";
    private static JwtBuilder jwtBuilder = Jwts.builder();
    private static JWTUtil intance;

    public static SecretKey generalKey() {
        try {
            byte[] encodedKey = JWT_SECERT.getBytes("UTF-8");
            SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            return key;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 签发JWT
     */
    public static String createJWT(String id, String subject, long valid_second) {
        return createJWT(id, subject, valid_second, null);
    }

    /**
     * 签发JWT
     */
    public static String createJWT(String id, String name, long valid_second, Map<String, Object> dataMap) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        SecretKey secretKey = generalKey();
        jwtBuilder = jwtBuilder
                //JWT_ID
                .setId(id)
                //主题或用户名
                .setSubject(name)
                //签发时间
                .setIssuedAt(new Date());
        //自定义属性
        if (!MapUtil.isEmpty(dataMap)) {
            jwtBuilder.setClaims(dataMap);
        }
        if (valid_second >= 0) {
            long expMillis = nowMillis + valid_second * 1000;
            Date expDate = new Date(expMillis);
            //过期时间
            jwtBuilder.setExpiration(expDate);
        }
        //签名算法
        jwtBuilder.signWith(signatureAlgorithm, secretKey);
        return jwtBuilder.compact();
    }

    /**
     * 解析JWT
     * 可能会抛异常: MalformedJwtException(格式错误); IllegalStateException(payload和claims只能二选一);
     * UnsupportedJwtException/SignatureException(签名错误); ExpiredJwtException(已过期);
     */
    public static Claims parseJWT(String jwt) {
        SecretKey secretKey = generalKey();
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).getBody();
    }

    /**
     * 检查token是否过期
     */
    public Boolean isTokenExpired(String token) {
        Date expiration = parseJWT(token).getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 获取用户名从token中
     */
    public String getUsernameFromToken(String token) {
        return parseJWT(token).getSubject();
    }

    /**
     * 获取jwt发布时间
     */
    public Date getIssuedAtDateFromToken(String token) {
        return parseJWT(token).getIssuedAt();
    }
}
