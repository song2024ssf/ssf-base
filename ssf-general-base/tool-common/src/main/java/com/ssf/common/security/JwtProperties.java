package com.ssf.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "campus.security.jwt")
public class JwtProperties {

    /** JWT 签名密钥，必须配置，默认值仅供开发使用 */
    private String secret = "DefaultSecretKeyForJwtPleaseChangeInProduction";

    /** Token 过期时间（秒），默认 7 天 */
    private Long expiration = 604800L;

    /** 请求头中 Token 的字段名 */
    private String header = "Authorization";

    /** Token 前缀，通常为 "Bearer " */
    private String tokenPrefix = "Bearer ";
}