package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器 — 负责用户登录和注册。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /** 登录 — 校验用户名密码，返回 JWT 令牌 */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        try {
            String username = String.valueOf(payload.getOrDefault("username", ""));
            String password = String.valueOf(payload.getOrDefault("password", ""));
            Map<String, Object> result = userService.login(username, password);
            return ApiResponse.success("登录成功", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 注册 — 创建新用户 */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, Object> payload) {
        try {
            String username = String.valueOf(payload.getOrDefault("username", ""));
            String password = String.valueOf(payload.getOrDefault("password", ""));
            String nickname = String.valueOf(payload.getOrDefault("nickname", ""));
            String phone = String.valueOf(payload.getOrDefault("phone", ""));
            userService.register(username, password, nickname, phone);
            return ApiResponse.success("注册成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
