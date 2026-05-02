package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.common.JwtUtil;
import com.jwluo0719.deltatrade.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me(@RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            return ApiResponse.success(userService.getProfile(extractUserId(auth)));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/me")
    public ApiResponse<Map<String, Object>> updateProfile(@RequestHeader(value = "Authorization", required = false) String auth,
                                                          @RequestBody Map<String, Object> payload) {
        try {
            String nickname = String.valueOf(payload.getOrDefault("nickname", ""));
            return ApiResponse.success("璧勬枡宸蹭繚瀛?", userService.updateProfile(extractUserId(auth), nickname));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/me/password")
    public ApiResponse<Void> changePassword(@RequestHeader(value = "Authorization", required = false) String auth,
                                            @RequestBody Map<String, Object> payload) {
        try {
            String oldPassword = String.valueOf(payload.getOrDefault("oldPassword", ""));
            String newPassword = String.valueOf(payload.getOrDefault("newPassword", ""));
            userService.changePassword(extractUserId(auth), oldPassword, newPassword);
            return ApiResponse.success("瀵嗙爜淇敼鎴愬姛", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    private Long extractUserId(String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            Long userId = JwtUtil.getUserId(auth.substring(7));
            if (userId != null) {
                return userId;
            }
        }
        return 1L;
    }
}
