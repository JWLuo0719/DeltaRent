package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        try {
            String loginKey = firstNonBlank(payload.get("phone"), payload.get("username"));
            String password = String.valueOf(payload.getOrDefault("password", ""));
            Map<String, Object> result = userService.login(loginKey, password);
            return ApiResponse.success("鐧诲綍鎴愬姛", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, Object> payload) {
        try {
            String username = firstNonBlank(payload.get("username"), payload.get("phone"));
            String password = String.valueOf(payload.getOrDefault("password", ""));
            String nickname = String.valueOf(payload.getOrDefault("nickname", ""));
            String phone = String.valueOf(payload.getOrDefault("phone", ""));
            userService.register(username, password, nickname, phone);
            return ApiResponse.success("娉ㄥ唽鎴愬姛", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/send-verify-code")
    public ApiResponse<Void> sendVerifyCode(@RequestBody Map<String, Object> payload) {
        try {
            String phone = String.valueOf(payload.getOrDefault("phone", ""));
            String type = String.valueOf(payload.getOrDefault("type", "reset_password"));
            userService.sendVerifyCode(phone, type);
            return ApiResponse.success("楠岃瘉鐮佸凡鍙戦€侊紙娴嬭瘯鐮?123456锛?", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody Map<String, Object> payload) {
        try {
            String phone = String.valueOf(payload.getOrDefault("phone", ""));
            String verifyCode = String.valueOf(payload.getOrDefault("verifyCode", ""));
            String newPassword = String.valueOf(payload.getOrDefault("newPassword", ""));
            userService.resetPassword(phone, verifyCode, newPassword);
            return ApiResponse.success("瀵嗙爜宸查噸缃?", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    private String firstNonBlank(Object first, Object second) {
        String firstValue = first == null ? "" : String.valueOf(first).trim();
        if (!firstValue.isBlank()) {
            return firstValue;
        }
        return second == null ? "" : String.valueOf(second).trim();
    }
}
