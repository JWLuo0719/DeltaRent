package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> list(@RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer pageSize,
                                                 @RequestParam(required = false) String phone,
                                                 @RequestParam(required = false) String role,
                                                 @RequestParam(required = false) Integer status) {
        return ApiResponse.success(userService.listAdminUsers(page, pageSize, phone, role, status));
    }

    @PutMapping("/{id}/role")
    public ApiResponse<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            String role = String.valueOf(payload.getOrDefault("role", ""));
            userService.updateRole(id, role);
            return ApiResponse.success("瑙掕壊宸叉洿鏂?", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Integer status = payload.get("status") instanceof Number number ? number.intValue() : Integer.parseInt(String.valueOf(payload.getOrDefault("status", "1")));
            userService.updateStatus(id, status);
            return ApiResponse.success("鐢ㄦ埛鐘舵€佸凡鏇存柊", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
