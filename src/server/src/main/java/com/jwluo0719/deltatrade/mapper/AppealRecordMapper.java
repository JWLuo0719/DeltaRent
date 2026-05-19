package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.AppealRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 申诉记录数据访问层 — 负责 appeal_record 表的增删改查。
 */
@Mapper
public interface AppealRecordMapper {

    /** 按用户 ID 查询其所有申诉 */
    @Select("select id, order_type, order_id, user_id, content, reason, status, handler_id, handler_remark, refund_amount, compensation, handled_at, updated_at from appeal_record where user_id = #{userId} order by id desc")
    List<AppealRecord> findByUserId(Long userId);

    /** 查询全部申诉（管理员用），带用户名和订单关联 */
    @Select("""
            select a.id, a.order_type, a.order_id, a.user_id, a.content, a.reason,
                   a.status, a.handler_id, a.handler_remark, a.refund_amount, a.compensation,
                   a.handled_at, a.updated_at
            from appeal_record a
            order by a.id desc
            """)
    List<AppealRecord> findAll();

    /** 查询全部申诉带详情（管理员用），含用户名和账号名 */
    @Select("""
            select a.id, a.order_type, a.order_id, a.user_id, a.content, a.reason,
                   a.status, a.handler_id, a.handler_remark, a.refund_amount, a.compensation,
                   a.handled_at, a.updated_at, a.created_at,
                   coalesce(u.nickname, u.username) as userName,
                   coalesce(h.nickname, h.username) as handlerName,
                   p.name as productName,
                   o.order_no as orderNo,
                   o.order_amount as orderAmount,
                   o.status as orderStatus
            from appeal_record a
            left join sys_user u on a.user_id = u.id
            left join sys_user h on a.handler_id = h.id
            left join rental_order o on a.order_id = o.id and a.order_type = 'RENTAL'
            left join rental_product p on o.product_id = p.id
            order by a.id desc
            """)
    List<Map<String, Object>> findAllWithDetails();

    /** 按 ID 查询单条申诉 */
    @Select("select id, order_type, order_id, user_id, content, reason, status, handler_id, handler_remark, refund_amount, compensation, handled_at, updated_at from appeal_record where id = #{id}")
    AppealRecord findById(Long id);

    /** 新增申诉 */
    @Insert("insert into appeal_record(order_type, order_id, user_id, content, reason, status) values(#{orderType}, #{orderId}, #{userId}, #{content}, #{reason}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AppealRecord record);

    /** 更新申诉状态（管理员处理），含退款金额和赔偿说明 */
    @Update("update appeal_record set status = #{status}, handler_id = #{handlerId}, handler_remark = #{handlerRemark}, refund_amount = #{refundAmount}, compensation = #{compensation}, handled_at = #{handledAt}, updated_at = current_timestamp where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status,
                     @Param("handlerId") Long handlerId, @Param("handlerRemark") String handlerRemark,
                     @Param("refundAmount") java.math.BigDecimal refundAmount,
                     @Param("compensation") String compensation,
                     @Param("handledAt") java.time.LocalDateTime handledAt);
}
