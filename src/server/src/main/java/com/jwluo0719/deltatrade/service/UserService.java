package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.common.JwtUtil;
import com.jwluo0719.deltatrade.domain.SmsVerifyCode;
import com.jwluo0719.deltatrade.domain.SysRole;
import com.jwluo0719.deltatrade.domain.SysUser;
import com.jwluo0719.deltatrade.mapper.SmsVerifyCodeMapper;
import com.jwluo0719.deltatrade.mapper.SysRoleMapper;
import com.jwluo0719.deltatrade.mapper.SysUserMapper;
import com.jwluo0719.deltatrade.mapper.SysUserRoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Random RANDOM = new Random();
    private static final long VERIFY_CODE_EXPIRE_MINUTES = 10L;
    private static final long VERIFY_CODE_COOLDOWN_SECONDS = 60L;

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SmsVerifyCodeMapper smsVerifyCodeMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(SysUserMapper sysUserMapper,
                       SysRoleMapper sysRoleMapper,
                       SysUserRoleMapper sysUserRoleMapper,
                       SmsVerifyCodeMapper smsVerifyCodeMapper,
                       PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.smsVerifyCodeMapper = smsVerifyCodeMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> login(String loginKey, String password) {
        if (loginKey == null || loginKey.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("手机号或用户名和密码不能为空");
        }

        SysUser user = sysUserMapper.findByLoginKey(loginKey);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("手机号或密码错误");
        }

        String saved = user.getPasswordHash();
        boolean ok = saved != null
                && (saved.equals(password) || (saved.startsWith("$2") && passwordEncoder.matches(password, saved)));
        if (!ok) {
            throw new IllegalArgumentException("手机号或密码错误");
        }

        String roleCode = normalizeRole(user.getRoleCode());
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), roleCode, resolvePasswordUpdatedAt(user));

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
        String normalizedPhone = normalizePhone(phone);
        String actualUsername = (username == null || username.isBlank()) ? normalizedPhone : username.trim();
        if (actualUsername.isBlank()) throw new IllegalArgumentException("用户名不能为空");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("密码长度不能少于 6 位");

        SysUser exist = sysUserMapper.findByUsername(actualUsername);
        if (exist != null) throw new IllegalArgumentException("用户名已存在");
        if (sysUserMapper.findByPhone(normalizedPhone) != null) throw new IllegalArgumentException("手机号已注册");

        SysUser user = new SysUser();
        user.setUsername(actualUsername);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setNickname((nickname != null && !nickname.isBlank()) ? nickname.trim() : normalizedPhone);
        user.setPhone(normalizedPhone);
        user.setStatus(1);
        sysUserMapper.insert(user);
        assignRole(user.getId(), "USER");
        return user;
    }

    public void sendVerifyCode(String phone, String type) {
        String normalizedPhone = normalizePhone(phone);
        String normalizedType = normalizeVerifyType(type);
        validateVerifyCodeTarget(normalizedPhone, normalizedType);

        SmsVerifyCode latest = smsVerifyCodeMapper.findLatestByPhone(normalizedPhone);
        LocalDateTime now = LocalDateTime.now();
        if (latest != null && latest.getCreatedAt() != null
                && latest.getCreatedAt().plusSeconds(VERIFY_CODE_COOLDOWN_SECONDS).isAfter(now)) {
            throw new IllegalArgumentException("验证码发送过于频繁，请 60 秒后再试");
        }

        SmsVerifyCode verifyCode = new SmsVerifyCode();
        verifyCode.setPhone(normalizedPhone);
        verifyCode.setType(normalizedType);
        verifyCode.setCode(generateVerifyCode());
        verifyCode.setCreatedAt(now);
        verifyCode.setExpireTime(now.plusMinutes(VERIFY_CODE_EXPIRE_MINUTES));
        smsVerifyCodeMapper.insert(verifyCode);

        log.info("模拟发送验证码成功：phone={}, type={}", normalizedPhone, normalizedType);
    }

    @Transactional
    public void resetPassword(String phone, String verifyCode, String newPassword) {
        String normalizedPhone = normalizePhone(phone);
        if (verifyCode == null || verifyCode.isBlank()) {
            throw new IllegalArgumentException("验证码不能为空");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于 6 位");
        }

        SmsVerifyCode latest = smsVerifyCodeMapper.findLatestByPhoneAndType(normalizedPhone, "reset_password");
        LocalDateTime now = LocalDateTime.now();
        if (latest == null
                || latest.getUsedAt() != null
                || latest.getExpireTime() == null
                || latest.getExpireTime().isBefore(now)
                || !verifyCode.trim().equals(latest.getCode())) {
            throw new IllegalArgumentException("验证码错误或已失效");
        }

        SysUser user = sysUserMapper.findByPhone(normalizedPhone);
        if (user == null) throw new IllegalArgumentException("用户不存在");

        LocalDateTime passwordUpdatedAt = LocalDateTime.now();
        sysUserMapper.updatePassword(user.getId(), passwordEncoder.encode(newPassword), passwordUpdatedAt);
        smsVerifyCodeMapper.markUsed(latest.getId(), now);
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
            throw new IllegalArgumentException("用户不存在");
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
            throw new IllegalArgumentException("昵称不能为空");
        }
        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        sysUserMapper.updateProfile(id, nickname.trim());
        return getProfile(id);
    }

    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new IllegalArgumentException("原密码不能为空");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码长度不能少于 6 位");
        }

        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        String saved = user.getPasswordHash();
        boolean match = saved != null
                && (saved.equals(oldPassword) || (saved.startsWith("$2") && passwordEncoder.matches(oldPassword, saved)));
        if (!match) {
            throw new IllegalArgumentException("原密码不正确");
        }

        sysUserMapper.updatePassword(id, passwordEncoder.encode(newPassword), LocalDateTime.now());
    }

    public void updateStatus(Long id, Integer status) {
        if (sysUserMapper.findById(id) == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        sysUserMapper.updateStatus(id, status);
    }

    public void updateRole(Long id, String roleCode) {
        SysUser user = sysUserMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (roleCode == null || roleCode.isBlank()) {
            throw new IllegalArgumentException("角色不能为空");
        }
        SysRole role = sysRoleMapper.findByCode(roleCode);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        assignRole(id, roleCode);
    }

    private String normalizeRole(String roleCode) {
        return (roleCode == null || roleCode.isBlank()) ? "USER" : roleCode;
    }

    private void assignRole(Long userId, String roleCode) {
        SysRole role = sysRoleMapper.findByCode(roleCode);
        if (role == null) {
            throw new IllegalArgumentException("角色不存在");
        }
        sysUserRoleMapper.deleteByUserId(userId);
        sysUserRoleMapper.insert(userId, role.getId());
    }

    private String normalizePhone(String phone) {
        String normalizedPhone = phone == null ? "" : phone.trim();
        if (!PHONE_PATTERN.matcher(normalizedPhone).matches()) {
            throw new IllegalArgumentException("请输入正确的手机号");
        }
        return normalizedPhone;
    }

    private String normalizeVerifyType(String type) {
        String normalizedType = type == null ? "" : type.trim().toLowerCase(Locale.ROOT);
        return switch (normalizedType) {
            case "reset_password", "register", "login" -> normalizedType;
            default -> throw new IllegalArgumentException("验证码类型不支持");
        };
    }

    private void validateVerifyCodeTarget(String phone, String type) {
        SysUser user = sysUserMapper.findByPhone(phone);
        switch (type) {
            case "reset_password", "login" -> {
                if (user == null) {
                    throw new IllegalArgumentException("该手机号尚未注册");
                }
            }
            case "register" -> {
                if (user != null) {
                    throw new IllegalArgumentException("该手机号已注册");
                }
            }
            default -> throw new IllegalArgumentException("验证码类型不支持");
        }
    }

    private String generateVerifyCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private LocalDateTime resolvePasswordUpdatedAt(SysUser user) {
        if (user.getPasswordUpdatedAt() != null) {
            return user.getPasswordUpdatedAt();
        }
        if (user.getUpdatedAt() != null) {
            return user.getUpdatedAt();
        }
        if (user.getCreatedAt() != null) {
            return user.getCreatedAt();
        }
        return LocalDateTime.now();
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
