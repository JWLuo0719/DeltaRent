package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户数据访问层 — 负责 sys_user 表的增删改查，支撑登录认证和用户管理。
 */
@Mapper
public interface SysUserMapper {

    /** 按用户名精确查找，用于登录校验 */
    @Select("select id, username, password_hash, nickname, phone, status from sys_user where username = #{username} limit 1")
    SysUser findByUsername(String username);

    /** 统计用户总数 */
    @Select("select count(*) from sys_user")
    long countAll();

    /** 查询全部用户（管理员用） */
    @Select("select id, username, password_hash, nickname, phone, status from sys_user order by id")
    List<SysUser> findAll();

    /** 按 ID 查询用户 */
    @Select("select id, username, password_hash, nickname, phone, status from sys_user where id = #{id}")
    SysUser findById(Long id);

    /** 新增用户（注册），密码存储 BCrypt 哈希 */
    @Insert("insert into sys_user(username, password_hash, nickname, phone, status) values(#{username}, #{passwordHash}, #{nickname}, #{phone}, 1)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    /** 更新用户状态（启用/禁用） */
    @Update("update sys_user set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
