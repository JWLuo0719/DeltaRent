package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.SysRole;
import com.jwluo0719.deltatrade.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    private final SysRoleMapper sysRoleMapper;

    public RoleService(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    public List<Map<String, Object>> listAll() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysRole role : sysRoleMapper.findAllWithUserCount()) {
            result.add(toView(role));
        }
        return result;
    }

    public Map<String, Object> create(String roleCode, String roleName, String description) {
        validate(roleCode, roleName);
        if (sysRoleMapper.findByCode(roleCode) != null) {
            throw new IllegalArgumentException("瑙掕壊浠ｇ爜宸插瓨鍦?");
        }

        SysRole role = new SysRole();
        role.setRoleCode(roleCode.trim().toUpperCase());
        role.setRoleName(roleName.trim());
        role.setDescription(description == null ? "" : description.trim());
        sysRoleMapper.insert(role);
        return toView(sysRoleMapper.findByCode(role.getRoleCode()));
    }

    public void update(String roleCode, String roleName, String description) {
        validate(roleCode, roleName);
        SysRole existing = sysRoleMapper.findByCode(roleCode);
        if (existing == null) {
            throw new IllegalArgumentException("瑙掕壊涓嶅瓨鍦?");
        }

        SysRole role = new SysRole();
        role.setRoleCode(roleCode.trim().toUpperCase());
        role.setRoleName(roleName.trim());
        role.setDescription(description == null ? "" : description.trim());
        sysRoleMapper.update(role);
    }

    private void validate(String roleCode, String roleName) {
        if (roleCode == null || roleCode.isBlank()) {
            throw new IllegalArgumentException("瑙掕壊浠ｇ爜涓嶈兘涓虹┖");
        }
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("瑙掕壊鍚嶇О涓嶈兘涓虹┖");
        }
    }

    private Map<String, Object> toView(SysRole role) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("roleCode", role.getRoleCode());
        item.put("roleName", role.getRoleName());
        item.put("description", role.getDescription());
        item.put("userCount", role.getUserCount() == null ? 0 : role.getUserCount());
        return item;
    }
}
