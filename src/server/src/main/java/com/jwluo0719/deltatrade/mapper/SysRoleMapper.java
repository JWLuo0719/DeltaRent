package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.SysRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    @Select("""
            select r.id,
                   r.role_code,
                   r.role_name,
                   r.description,
                   count(ur.user_id) as user_count
            from sys_role r
            left join sys_user_role ur on ur.role_id = r.id
            group by r.id, r.role_code, r.role_name, r.description
            order by r.id asc
            """)
    List<SysRole> findAllWithUserCount();

    @Select("select id, role_code, role_name, description from sys_role where role_code = #{roleCode} limit 1")
    SysRole findByCode(String roleCode);

    @Select("select id, role_code, role_name, description from sys_role where id = #{id} limit 1")
    SysRole findById(Long id);

    @Insert("insert into sys_role(role_code, role_name, description) values(#{roleCode}, #{roleName}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysRole role);

    @Update("update sys_role set role_name = #{roleName}, description = #{description} where role_code = #{roleCode}")
    int update(SysRole role);
}
