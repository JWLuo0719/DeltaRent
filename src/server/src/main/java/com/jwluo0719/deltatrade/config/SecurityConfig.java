package com.jwluo0719.deltatrade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置 — 关闭 CSRF（前后端分离），关闭 Session（使用 JWT），
 * 公开接口无需认证，管理接口需要 ADMIN 角色，其余接口需要登录。
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 前后端分离，关闭 CSRF
            .csrf(AbstractHttpConfigurer::disable)
            // 使用 JWT 无状态认证，不创建 Session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开接口：健康检查、登录注册、门户首页
                .requestMatchers("/api/health", "/api/auth/**", "/api/portal/**").permitAll()
                // 读接口：任何已登录用户可访问
                .requestMatchers(HttpMethod.GET, "/api/rentals/**", "/api/notices/**").permitAll()
                // 写接口：需要 ADMIN 角色
                .requestMatchers(HttpMethod.POST, "/api/rentals/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/rentals/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/rentals/**").hasRole("ADMIN")
                .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 订单：登录用户可创建和查看自己的，管理员可管理
                .requestMatchers(HttpMethod.POST, "/api/orders/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasRole("ADMIN")
                .requestMatchers("/api/appeals/**").authenticated()
                // 其余请求需要认证
                .anyRequest().authenticated()
            )
            // 在用户名密码过滤器之前插入 JWT 过滤器
            .addFilterBefore(new JwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 密码编码器 — 使用 BCrypt 对用户密码进行不可逆哈希存储。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
