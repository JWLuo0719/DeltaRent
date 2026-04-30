package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.common.JwtUtil;
import com.jwluo0719.deltatrade.domain.SysUser;
import com.jwluo0719.deltatrade.mapper.SysUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户业务服务 — 负责登录认证和用户管理。
 */
@Service
public class UserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 登录 — 校验用户名和密码，成功后返回 JWT 令牌和用户信息。
     */
    public Map<String, Object> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        SysUser user = sysUserMapper.findByUsername(username);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        // 密码兼容明文（种子数据）和 BCrypt 哈希两种存储方式
        String saved = user.getPasswordHash();
        boolean ok = saved != null
                && (saved.equals(password) || (saved.startsWith("$2") && passwordEncoder.matches(password, saved)));
        if (!ok) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), "ADMIN");

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("displayName", user.getNickname());
        userInfo.put("role", "ADMIN");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", token);
        result.put("user", userInfo);
        return result;
    }

    /**
     * 注册 — 创建新用户，密码用 BCrypt 加密存储。
     */
    public SysUser register(String username, String password, String nickname, String phone) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("用户名不能为空");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("密码至少 6 位");

        SysUser exist = sysUserMapper.findByUsername(username);
        if (exist != null) throw new IllegalArgumentException("用户名已存在");

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setPhone(phone != null ? phone : "");
        user.setStatus(1);
        sysUserMapper.insert(user);
        return user;
    }

    /** 统计用户总数 */
    public long countAll() { return sysUserMapper.countAll(); }

    /** 管理员 — 查看全部用户 */
    public List<SysUser> listAll() {
        return sysUserMapper.findAll();
    }

    /** 管理员 — 按 ID 查用户 */
    public SysUser getById(Long id) {
        return sysUserMapper.findById(id);
    }

    /** 管理员 — 启用或禁用用户 */
    public void updateStatus(Long id, Integer status) {
        sysUserMapper.updateStatus(id, status);
    }
}
