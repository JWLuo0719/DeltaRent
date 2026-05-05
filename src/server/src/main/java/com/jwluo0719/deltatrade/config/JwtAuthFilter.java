package com.jwluo0719.deltatrade.config;

import com.jwluo0719.deltatrade.common.JwtUtil;
import com.jwluo0719.deltatrade.domain.SysUser;
import com.jwluo0719.deltatrade.mapper.SysUserMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器 — 每个请求到达时，从 Authorization 头中提取令牌，
 * 校验后将用户信息写入 Spring Security 上下文，后续控制器可通过
 * SecurityContextHolder 获取当前登录用户。
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final SysUserMapper sysUserMapper;

    public JwtAuthFilter(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    // 不需要认证即可访问的接口路径前缀（仅 GET 对 rentals/notices 公开）
    private static final String[] PUBLIC_PATH_PREFIXES = {
            "/api/health",
            "/api/auth/",
            "/api/portal/"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println("[DEBUG doFilter] method=" + request.getMethod() + " path=" + path);
        System.out.println("[DEBUG] isPublic=" + isPublicRequest(request, path));

        // 公开接口直接放行，不校验令牌
        if (isPublicRequest(request, path)) {
            chain.doFilter(request, response);
            return;
        }

        // 从请求头提取 Bearer Token
        String token = extractToken(request);
        Claims claims = token == null ? null : JwtUtil.parseToken(token);
        if (claims == null) {
            writeUnauthorized(response, "请先登录后再访问");
            return;
        }

        Long userId = JwtUtil.getUserId(token);
        SysUser user = userId == null ? null : sysUserMapper.findById(userId);
        String tokenPasswordVersion = JwtUtil.getPasswordVersion(token);
        String currentPasswordVersion = user == null ? null : JwtUtil.formatPasswordVersion(user.getPasswordUpdatedAt());
        if (user == null || !safeEquals(tokenPasswordVersion, currentPasswordVersion)) {
            writeUnauthorized(response, "登录状态已失效，请重新登录");
            return;
        }

        // 令牌有效，将用户信息设置到安全上下文
        String role = claims.get("role", String.class);
        String username = claims.get("username", String.class);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    /** 公开接口直接放行，GET 请求的 rentals/notices 也公开 */
    private boolean isPublicRequest(HttpServletRequest request, String path) {
        for (String prefix : PUBLIC_PATH_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        // GET 方式的商品和公告读接口公开，但 /api/notices/all 是管理员接口除外
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            if (path.startsWith("/api/rentals")) {
                return true;
            }
            if (path.startsWith("/api/notices") && !path.equals("/api/notices/all")) {
                return true;
            }
        }
        return false;
    }

    /** 从 Authorization 头中提取 Bearer Token */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\",\"data\":null}");
    }

    private boolean safeEquals(String left, String right) {
        String normalizedLeft = left == null ? "" : left;
        String normalizedRight = right == null ? "" : right;
        return normalizedLeft.equals(normalizedRight);
    }
}
