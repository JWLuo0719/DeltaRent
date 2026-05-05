package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.AppealRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 申诉记录数据访问层 — 负责 appeal_record 表的增删改查。
 */
@Mapper
public interface AppealRecordMapper {

    /** 按用户 ID 查询其所有申诉 */
    @Select("select id, order_type, order_id, user_id, content, status, handler_id, handler_remark, handled_at, updated_at from appeal_record where user_id = #{userId} order by id desc")
    List<AppealRecord> findByUserId(Long userId);

    /** 查询全部申诉（管理员用），带用户名关联 */
    @Select("select a.id, a.order_type, a.order_id, a.user_id, a.content, a.status, a.handler_id, a.handler_remark, a.handled_at, a.updated_at from appeal_record a order by a.id desc")
    List<AppealRecord> findAll();

    /** 按 ID 查询单条申诉 */
    @Select("select id, order_type, order_id, user_id, content, status, handler_id, handler_remark, handled_at, updated_at from appeal_record where id = #{id}")
    AppealRecord findById(Long id);

    /** 新增申诉 */
    @Insert("insert into appeal_record(order_type, order_id, user_id, content, status) values(#{orderType}, #{orderId}, #{userId}, #{content}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AppealRecord record);

    /** 更新申诉状态（管理员处理） */
    @Update("update appeal_record set status = #{status}, handler_id = #{handlerId}, handler_remark = #{handlerRemark}, handled_at = #{handledAt}, updated_at = current_timestamp where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("handlerId") Long handlerId, @Param("handlerRemark") String handlerRemark, @Param("handledAt") java.time.LocalDateTime handledAt);
}
