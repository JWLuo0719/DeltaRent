package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        Map<String, Object> user = new LinkedHashMap<String, Object>();
        user.put("id", 1);
        user.put("username", payload.get("username"));
        user.put("displayName", "课程演示账号");
        user.put("role", "ADMIN");

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("token", "java-backend-demo-token");
        result.put("user", user);
        return ApiResponse.success("登录成功", result);
    }
}
