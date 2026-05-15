package com.jwluo0719.deltatrade.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * JWT 令牌工具类 — 负责生成、解析和校验登录令牌。
 * 使用 HMAC-SHA256 签名，令牌有效期 24 小时。
 */
public final class JwtUtil {

    // 签名密钥（课程项目使用固定密钥，生产环境应从配置中心读取）给token上密钥
    private static final String SECRET = "DeltaRent-JWT-Secret-Key-2026-Course-Project-Must-Be-Long-Enough-For-HS256";
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24 小时
    private static final DateTimeFormatter PASSWORD_VERSION_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private JwtUtil() {
    }

    /**
     * 为用户生成 JWT 令牌，载荷中包含用户 ID、用户名和角色。
     */
    public static String generateToken(Long userId, String username, String role, LocalDateTime passwordUpdatedAt) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .claim("pwdUpdatedAt", formatPasswordVersion(passwordUpdatedAt))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    /**
     * 解析并校验令牌，返回其中的声明信息；校验失败则返回 null。
     */
    public static Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断令牌是否有效（未过期且签名正确）。
     */
    public static boolean isTokenValid(String token) {
        return parseToken(token) != null;
    }

    /**
     * 从有效令牌中提取用户 ID。
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从有效令牌中提取用户名。
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("username", String.class);
    }

    /**
     * 从有效令牌中提取角色。
     */
    public static String getRole(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("role", String.class);
    }

    public static String getPasswordVersion(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return null;
        return claims.get("pwdUpdatedAt", String.class);
    }

    public static String formatPasswordVersion(LocalDateTime passwordUpdatedAt) {
        return passwordUpdatedAt == null ? "" : PASSWORD_VERSION_FORMATTER.format(passwordUpdatedAt);
    }
}
