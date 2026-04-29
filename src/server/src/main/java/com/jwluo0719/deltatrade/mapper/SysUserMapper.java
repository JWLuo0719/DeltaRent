package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper {

    @Select("select id, username, password_hash, nickname, phone, status from sys_user where username = #{username} limit 1")
    SysUser findByUsername(String username);

    @Select("select count(*) from sys_user")
    long countAll();
}
