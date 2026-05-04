package com.jwluo0719.deltatrade.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserRoleMapper {

    @Delete("delete from sys_user_role where user_id = #{userId}")
    int deleteByUserId(Long userId);

    @Insert("insert into sys_user_role(user_id, role_id) values(#{userId}, #{roleId})")
    int insert(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
