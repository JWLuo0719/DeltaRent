package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.SysUser;
import com.jwluo0719.deltatrade.domain.SysRole;
import com.jwluo0719.deltatrade.mapper.SysRoleMapper;
import com.jwluo0719.deltatrade.mapper.SysUserMapper;
import com.jwluo0719.deltatrade.mapper.SysUserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;
    @Mock
    private SysRoleMapper sysRoleMapper;
    @Mock
    private SysUserRoleMapper sysUserRoleMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(sysUserMapper, sysRoleMapper, sysUserRoleMapper, passwordEncoder);
    }

    @Test
    void login_shouldSucceed_withCorrectPlainPassword() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPasswordHash("123456");
        user.setNickname("Admin");
        user.setStatus(1);
        user.setRoleCode("ADMIN");
        when(sysUserMapper.findByLoginKey("admin")).thenReturn(user);

        var result = userService.login("admin", "123456");

        assertNotNull(result.get("token"));
        @SuppressWarnings("unchecked")
        var userInfo = (java.util.Map<String, Object>) result.get("user");
        assertEquals("admin", userInfo.get("username"));
        assertEquals("ADMIN", userInfo.get("role"));
    }

    @Test
    void login_shouldSucceed_withBcryptPassword() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPasswordHash("$2a$10$hashedpassword");
        user.setNickname("Admin");
        user.setStatus(1);
        when(sysUserMapper.findByLoginKey("admin")).thenReturn(user);
        when(passwordEncoder.matches("123456", "$2a$10$hashedpassword")).thenReturn(true);

        var result = userService.login("admin", "123456");

        assertNotNull(result.get("token"));
    }

    @Test
    void login_shouldFail_whenPasswordWrong() {
        SysUser user = new SysUser();
        user.setId(1L);
        user.setUsername("admin");
        user.setPasswordHash("wrongpassword");
        user.setStatus(1);
        when(sysUserMapper.findByLoginKey("admin")).thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> userService.login("admin", "123456"));
    }

    @Test
    void login_shouldFail_whenUserNotFound() {
        when(sysUserMapper.findByLoginKey("nobody")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> userService.login("nobody", "123456"));
    }

    @Test
    void login_shouldFail_whenUserDisabled() {
        SysUser user = new SysUser();
        user.setStatus(0);
        when(sysUserMapper.findByLoginKey("admin")).thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> userService.login("admin", "123456"));
    }

    @Test
    void login_shouldFail_whenUsernameBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.login("", "123456"));
    }

    @Test
    void register_shouldSucceed_whenValidInput() {
        when(sysUserMapper.findByUsername("13800000001")).thenReturn(null);
        when(sysUserMapper.findByPhone("13800000001")).thenReturn(null);
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$encoded");
        SysRole role = new SysRole();
        role.setId(2L);
        role.setRoleCode("USER");
        when(sysRoleMapper.findByCode("USER")).thenReturn(role);
        doAnswer(invocation -> {
            SysUser inserted = invocation.getArgument(0);
            inserted.setId(3L);
            return 1;
        }).when(sysUserMapper).insert(any(SysUser.class));

        SysUser user = userService.register("", "123456", "New", "13800000001");

        assertNotNull(user);
        assertEquals("13800000001", user.getUsername());
        assertEquals("$2a$10$encoded", user.getPasswordHash());
        assertEquals(1, user.getStatus());
        verify(sysUserMapper).insert(user);
        verify(sysUserRoleMapper).insert(3L, 2L);
    }

    @Test
    void register_shouldFail_whenUsernameExists() {
        SysUser exist = new SysUser();
        exist.setUsername("admin");
        when(sysUserMapper.findByUsername("admin")).thenReturn(exist);

        assertThrows(IllegalArgumentException.class,
                () -> userService.register("admin", "123456", "", "13800000000"));
    }

    @Test
    void register_shouldFail_whenPasswordTooShort() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.register("newuser", "12345", "", "13800000001"));
    }
}
