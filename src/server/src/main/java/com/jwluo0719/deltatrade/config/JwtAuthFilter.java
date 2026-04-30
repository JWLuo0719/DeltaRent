package com.jwluo0719.deltatrade.config;

import com.jwluo0719.deltatrade.common.JwtUtil;
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

    // 不需要认证即可访问的接口路径前缀
    private static final String[] PUBLIC_PATHS = {
            "/api/health",
            "/api/auth/",
            "/api/portal/"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 公开接口直接放行，不校验令牌
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 从请求头提取 Bearer Token
        String token = extractToken(request);
        if (token == null || !JwtUtil.isTokenValid(token)) {
            // 令牌缺失或无效，返回 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized — please login first\",\"data\":null}");
            return;
        }

        // 令牌有效，将用户信息设置到安全上下文
        String role = JwtUtil.getRole(token);
        String username = JwtUtil.getUsername(token);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    /** 判断请求路径是否为公开接口 */
    private boolean isPublicPath(String path) {
        for (String prefix : PUBLIC_PATHS) {
            if (path.startsWith(prefix)) {
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
}
