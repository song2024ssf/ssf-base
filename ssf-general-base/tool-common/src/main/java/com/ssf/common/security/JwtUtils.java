package com.ssf.common.security;

import com.ssf.common.exception.BusinessException;
import com.ssf.common.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;

    /**
     * 生成 Token
     */
    public String generateToken(LoginUser loginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loginUser.getUserId());
        claims.put("username", loginUser.getUsername());
        claims.put("permissions", loginUser.getPermissions());

        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtProperties.getExpiration() * 1000);

        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(loginUser.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从 Token 中解析用户信息
     */
    public LoginUser getLoginUserFromToken(String token) {
        Claims claims = parseClaims(token);
        LoginUser user = new LoginUser();
        user.setUserId(claims.get("userId", Long.class));
        user.setUsername(claims.get("username", String.class));
        // 权限字段，注意类型转换
        try {
            List<String> permissions = claims.get("permissions", List.class);
            user.setPermissions(permissions);
        } catch (Exception e) {
            user.setPermissions(null);
        }
        return user;
    }

    /**
     * 校验 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token 已过期: {}", e.getMessage());
            throw new BusinessException(401, "Token 已过期，请重新登录");
        } catch (MalformedJwtException | SignatureException e) {
            log.warn("JWT Token 格式非法或签名无效: {}", e.getMessage());
            throw new BusinessException(401, "Token 非法");
        } catch (Exception e) {
            log.error("JWT Token 校验异常", e);
            throw new BusinessException(401, "Token 校验失败");
        }
    }

    /**
     * 解析 Token 获取 Claims
     */
    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}