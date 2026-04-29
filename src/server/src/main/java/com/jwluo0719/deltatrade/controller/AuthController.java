package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.domain.SysUser;
import com.jwluo0719.deltatrade.mapper.SysUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthController(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, Object> payload) {
        String username = String.valueOf(payload.getOrDefault("username", ""));
        String password = String.valueOf(payload.getOrDefault("password", ""));

        if (username.isBlank() || password.isBlank()) {
            return ApiResponse.fail("Username and password are required");
        }

        SysUser userRecord = sysUserMapper.findByUsername(username);
        if (userRecord == null || userRecord.getStatus() == null || userRecord.getStatus() != 1) {
            return ApiResponse.fail("Invalid username or password");
        }

        String savedPassword = userRecord.getPasswordHash();
        boolean passwordOk = savedPassword != null
            && (savedPassword.equals(password) || (savedPassword.startsWith("$2") && passwordEncoder.matches(password, savedPassword)));
        if (!passwordOk) {
            return ApiResponse.fail("Invalid username or password");
        }

        Map<String, Object> user = new LinkedHashMap<String, Object>();
        user.put("id", userRecord.getId());
        user.put("username", userRecord.getUsername());
        user.put("displayName", userRecord.getNickname());
        user.put("role", "ADMIN");

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("token", "java-backend-demo-token-" + userRecord.getId());
        result.put("user", user);
        return ApiResponse.success("Login success", result);
    }
}
