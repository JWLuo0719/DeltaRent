package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysUserMapper {

    @Select("""
            select u.id, u.username, u.password_hash, u.nickname, u.phone, u.status,
                   u.created_at, u.updated_at, u.password_updated_at, r.role_code
            from sys_user u
            left join sys_user_role ur on ur.user_id = u.id
            left join sys_role r on r.id = ur.role_id
            where u.username = #{username}
            limit 1
            """)
    SysUser findByUsername(String username);

    @Select("""
            select u.id, u.username, u.password_hash, u.nickname, u.phone, u.status,
                   u.created_at, u.updated_at, u.password_updated_at, r.role_code
            from sys_user u
            left join sys_user_role ur on ur.user_id = u.id
            left join sys_role r on r.id = ur.role_id
            where u.username = #{loginKey} or u.phone = #{loginKey}
            limit 1
            """)
    SysUser findByLoginKey(String loginKey);

    @Select("""
            select u.id, u.username, u.password_hash, u.nickname, u.phone, u.status,
                   u.created_at, u.updated_at, u.password_updated_at, r.role_code
            from sys_user u
            left join sys_user_role ur on ur.user_id = u.id
            left join sys_role r on r.id = ur.role_id
            where u.phone = #{phone}
            limit 1
            """)
    SysUser findByPhone(String phone);

    @Select("select count(*) from sys_user")
    long countAll();

    @Select("""
            select u.id, u.username, u.password_hash, u.nickname, u.phone, u.status,
                   u.created_at, u.updated_at, u.password_updated_at, r.role_code
            from sys_user u
            left join sys_user_role ur on ur.user_id = u.id
            left join sys_role r on r.id = ur.role_id
            order by u.id
            """)
    List<SysUser> findAll();

    @Select("""
            select u.id, u.username, u.password_hash, u.nickname, u.phone, u.status,
                   u.created_at, u.updated_at, u.password_updated_at, r.role_code
            from sys_user u
            left join sys_user_role ur on ur.user_id = u.id
            left join sys_role r on r.id = ur.role_id
            where u.id = #{id}
            limit 1
            """)
    SysUser findById(Long id);

    @Insert("""
            insert into sys_user(username, password_hash, nickname, phone, status)
            values(#{username}, #{passwordHash}, #{nickname}, #{phone}, 1)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser user);

    @Update("update sys_user set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("update sys_user set nickname = #{nickname}, updated_at = current_timestamp where id = #{id}")
    int updateProfile(@Param("id") Long id, @Param("nickname") String nickname);

    @Update("""
            update sys_user
            set password_hash = #{passwordHash},
                password_updated_at = #{passwordUpdatedAt},
                updated_at = current_timestamp
            where id = #{id}
            """)
    int updatePassword(@Param("id") Long id,
                       @Param("passwordHash") String passwordHash,
                       @Param("passwordUpdatedAt") java.time.LocalDateTime passwordUpdatedAt);
}
