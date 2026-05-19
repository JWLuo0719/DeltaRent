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
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final SysUserMapper sysUserMapper;

    private static final String[] PUBLIC_PATH_PREFIXES = {
            "/api/health",
            "/api/auth/",
            "/api/portal/"
    };

    public JwtAuthFilter(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (isPublicRequest(request, path)) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (token == null) {
            writeUnauthorized(response, "请先登录后再访问");
            return;
        }

        Claims claims;
        try {
            claims = JwtUtil.parseToken(token);
        } catch (Exception e) {
            writeUnauthorized(response, "登录状态已失效，请重新登录");
            return;
        }
        if (claims == null) {
            writeUnauthorized(response, "登录状态已失效，请重新登录");
            return;
        }

        try {
            Long userId = Long.parseLong(claims.getSubject());
            SysUser user = sysUserMapper.findById(userId);
            String tokenPasswordVersion = claims.get("pwdUpdatedAt", String.class);
            String currentPasswordVersion = user == null ? null : JwtUtil.formatPasswordVersion(user.getPasswordUpdatedAt());
            if (user == null || !safeEquals(tokenPasswordVersion, currentPasswordVersion)) {
                writeUnauthorized(response, "登录状态已失效，请重新登录");
                return;
            }

            String role = claims.get("role", String.class);
            String username = claims.get("username", String.class);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            writeUnauthorized(response, "登录状态已失效，请重新登录");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicRequest(HttpServletRequest request, String path) {
        for (String prefix : PUBLIC_PATH_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            if ("/api/rentals".equals(path) || path.startsWith("/api/rentals/")) {
                return true;
            }
            if ("/api/notices".equals(path) || (path.startsWith("/api/notices/") && !"/api/notices/all".equals(path))) {
                return true;
            }
        }

        return false;
    }

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
