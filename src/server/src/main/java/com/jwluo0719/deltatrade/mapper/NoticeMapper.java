package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.Notice;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 公告数据访问层 — 负责 notice 表的增删改查。
 */
@Mapper
public interface NoticeMapper {

    /** 查询所有已发布的公告（status=1），按 ID 倒序 */
    @Select("select id, title, content, author_id, status, created_at from notice where status = 1 order by id desc")
    List<Notice> findPublished();

    /** 查询全部公告（管理员用） */
    @Select("select id, title, content, author_id, status, created_at from notice order by id desc")
    List<Notice> findAll();

    /** 按 ID 查询单条公告 */
    @Select("select id, title, content, author_id, status, created_at from notice where id = #{id}")
    Notice findById(Long id);

    /** 新增公告 */
    @Insert("insert into notice(title, content, author_id, status) values(#{title}, #{content}, #{authorId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    /** 更新公告 */
    @Update("update notice set title = #{title}, content = #{content}, status = #{status} where id = #{id}")
    int update(Notice notice);

    /** 删除公告 */
    @Delete("delete from notice where id = #{id}")
    int deleteById(Long id);
}
