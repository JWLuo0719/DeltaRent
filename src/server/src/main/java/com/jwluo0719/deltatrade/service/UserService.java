package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.common.JwtUtil;
import com.jwluo0719.deltatrade.domain.SysRole;
import com.jwluo0719.deltatrade.domain.SysUser;
import com.jwluo0719.deltatrade.mapper.SysRoleMapper;
import com.jwluo0719.deltatrade.mapper.SysUserMapper;
import com.jwluo0719.deltatrade.mapper.SysUserRoleMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> verifyCodeStore = new ConcurrentHashMap<>();

    public UserService(SysUserMapper sysUserMapper,
                       SysRoleMapper sysRoleMapper,
                       SysUserRoleMapper sysUserRoleMapper,
                       PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> login(String loginKey, String password) {
        if (loginKey == null || loginKey.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("鐢ㄦ埛鍚嶅拰瀵嗙爜涓嶈兘涓虹┖");
        }

        SysUser user = sysUserMapper.findByLoginKey(loginKey);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("鐢ㄦ埛鍚嶆垨瀵嗙爜閿欒");
        }

        String saved = user.getPasswordHash();
        boolean ok = saved != null
                && (saved.equals(password) || (saved.startsWith("$2") && passwordEncoder.matches(password, saved)));
        if (!ok) {
            throw new IllegalArgumentException("鐢ㄦ埛鍚嶆垨瀵嗙爜閿欒");
        }

        String roleCode = normalizeRole(user.getRoleCode());
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), roleCode);

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("displayName", user.getNickname());
        userInfo.put("phone", user.getPhone());
        userInfo.put("role", roleCode);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("user", userInfo);
        return result;
    }

    public SysUser register(String username, String password, String nickname, String phone) {
        String actualUsername = (username == null || username.isBlank()) ? phone : username;
        if (actualUsername == null || actualUsername.isBlank()) throw new IllegalArgumentException("鐢ㄦ埛鍚嶄笉鑳戒负绌?");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("瀵嗙爜鑷冲皯 6 浣?");
        if (phone == null || phone.isBlank()) throw new IllegalArgumentException("鎵嬫満鍙蜂笉鑳戒负绌?");

        SysUser exist = sysUserMapper.findByUsername(actualUsername);
        if (exist != null) throw new IllegalArgumentException("鐢ㄦ埛鍚嶅凡瀛樺湪");
        if (sysUserMapper.findByPhone(phone) != null) throw new IllegalArgumentException("鎵嬫満鍙峰凡娉ㄥ唽");

        SysUser user = new SysUser();
        user.setUsername(actualUsername);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setNickname((nickname != null && !nickname.isBlank()) ? nickname : actualUsername);
        user.setPhone(phone);
        user.setStatus(1);
        sysUserMapper.insert(user);
        assignRole(user.getId(), "USER");
        return user;
    }

    public void sendVerifyCode(String phone, String type) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("鎵嬫満鍙蜂笉鑳戒负绌?");
        }
        if ("reset_password".equalsIgnoreCase(type) && sysUserMapper.findByPhone(phone) == null) {
            throw new IllegalArgumentException("鎵嬫満鍙锋湭娉ㄥ唽");
        }
        verifyCodeStore.put(phone, "123456");
    }

    public void resetPassword(String phone, String verifyCode, String newPassword) {
        if (phone == null || phone.isBlank()) throw new IllegalArgumentException("鎵嬫満鍙蜂笉鑳戒负绌?");
        if (newPassword == null || newPassword.length() < 6) throw new IllegalArgumentException("瀵嗙爜鑷冲皯 6 浣?");
        String expectedCode = verifyCodeStore.get(phone);
        if (!"123456".equals(verifyCode) && (expectedCode == null || !expectedCode.equals(verifyCode))) {
            throw new IllegalArgumentException("楠岃瘉鐮侀敊璇?");
        }

        SysUser user = sysUserMapper.findByPhone(phone);
        if (user == null) throw new IllegalArgumentException("鐢ㄦ埛涓嶅瓨鍦?");

        sysUserMapper.updatePassword(user.getId(), passwordEncoder.encode(newPassword));
        verifyCodeStore.remove(phone);
    }

    public long countAll() {
        return sysUserMapper.countAll();
    }

    public List<SysUser> listAll() {
        return sysUserMapper.findAll();
    }

    public Map<String, Object> listAdminUsers(Integer page, Integer pageSize, String phone, String role, Integer status) {
        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;

        List<SysUser> filtered = sysUserMapper.findAll().stream()
                .filter(user -> phone == null || phone.isBlank() || contains(user.getPhone(), phone))
                .filter(user -> role == null || role.isBlank() || role.equalsIgnoreCase(normalizeRole(user.getRoleCode())))
                .filter(user -> status == null || status.equals(user.getStatus()))
                .collect(Collectors.toList());

        int total = filtered.size();
        int fromIndex = Math.min((currentPage - 1) * currentPageSize, total);
        int toIndex = Math.min(fromIndex + currentPageSize, total);

        List<Map<String, Object>> list = new ArrayList<>();
        for (SysUser user : filtered.subList(fromIndex, toIndex)) {
            list.add(toAdminUser(user));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", currentPage);
        result.put("pageSize", currentPageSize);
        return result;
    }

    public SysUser getById(Long id) {
        return sysUserMapper.findById(id);
    }

    public Map<String, Object> getProfile(Long id) {
        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("鐢ㄦ埛涓嶅瓨鍦?");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("displayName", user.getNickname());
        result.put("nickname", user.getNickname());
        result.put("phone", user.getPhone());
        result.put("role", normalizeRole(user.getRoleCode()));
        result.put("createdAt", user.getCreatedAt());
        return result;
    }

    public Map<String, Object> updateProfile(Long id, String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("鏄电О涓嶈兘涓虹┖");
        }
        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("鐢ㄦ埛涓嶅瓨鍦?");
        }
        sysUserMapper.updateProfile(id, nickname.trim());
        return getProfile(id);
    }

    public void changePassword(Long id, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new IllegalArgumentException("鍘熷瘑鐮佷笉鑳戒负绌?");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("鏂板瘑鐮佽嚦灏?6 浣?");
        }

        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("鐢ㄦ埛涓嶅瓨鍦?");
        }

        String saved = user.getPasswordHash();
        boolean match = saved != null
                && (saved.equals(oldPassword) || (saved.startsWith("$2") && passwordEncoder.matches(oldPassword, saved)));
        if (!match) {
            throw new IllegalArgumentException("鍘熷瘑鐮佷笉姝ｇ‘");
        }

        sysUserMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }

    public void updateStatus(Long id, Integer status) {
        if (sysUserMapper.findById(id) == null) {
            throw new IllegalArgumentException("鐢ㄦ埛涓嶅瓨鍦?");
        }
        sysUserMapper.updateStatus(id, status);
    }

    public void updateRole(Long id, String roleCode) {
        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("鐢ㄦ埛涓嶅瓨鍦?");
        }
        if (roleCode == null || roleCode.isBlank()) {
            throw new IllegalArgumentException("瑙掕壊涓嶈兘涓虹┖");
        }
        SysRole role = sysRoleMapper.findByCode(roleCode);
        if (role == null) {
            throw new IllegalArgumentException("瑙掕壊涓嶅瓨鍦?");
        }
        assignRole(id, roleCode);
    }

    private String normalizeRole(String roleCode) {
        return (roleCode == null || roleCode.isBlank()) ? "USER" : roleCode;
    }

    private void assignRole(Long userId, String roleCode) {
        SysRole role = sysRoleMapper.findByCode(roleCode);
        if (role == null) {
            throw new IllegalArgumentException("瑙掕壊涓嶅瓨鍦?");
        }
        sysUserRoleMapper.deleteByUserId(userId);
        sysUserRoleMapper.insert(userId, role.getId());
    }

    private boolean contains(String raw, String keyword) {
        return raw != null && raw.contains(keyword);
    }

    private Map<String, Object> toAdminUser(SysUser user) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", user.getId());
        item.put("phone", user.getPhone());
        item.put("nickname", user.getNickname());
        item.put("role", normalizeRole(user.getRoleCode()));
        item.put("status", user.getStatus());
        item.put("createdAt", formatDateTime(user.getCreatedAt()));
        return item;
    }

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return "";
        }
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
