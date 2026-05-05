package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/roles")
public class AdminRoleController {

    private final RoleService roleService;

    public AdminRoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.success(roleService.listAll());
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> payload) {
        try {
            String roleCode = String.valueOf(payload.getOrDefault("roleCode", ""));
            String roleName = String.valueOf(payload.getOrDefault("roleName", ""));
            String description = String.valueOf(payload.getOrDefault("description", ""));
            return ApiResponse.success("角色创建成功", roleService.create(roleCode, roleName, description));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/{roleCode}")
    public ApiResponse<Void> update(@PathVariable String roleCode, @RequestBody Map<String, Object> payload) {
        try {
            String roleName = String.valueOf(payload.getOrDefault("roleName", ""));
            String description = String.valueOf(payload.getOrDefault("description", ""));
            roleService.update(roleCode, roleName, description);
            return ApiResponse.success("角色已更新", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
