package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.RentalOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 租赁订单数据访问层 — 负责 rental_order 表的增删改查，
 * 支撑用户下单、订单查询和后台订单管理。
 */
@Mapper
public interface RentalOrderMapper {

    /** 新增订单 */
    @Insert("insert into rental_order(order_no, user_id, product_id, rent_hours, order_amount, contact_info, delivery_note, status) values(#{orderNo}, #{userId}, #{productId}, #{rentHours}, #{orderAmount}, #{contactInfo}, #{deliveryNote}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RentalOrder order);

    /** 统计全部订单数 */
    @Select("select count(*) from rental_order")
    long countAll();

    /** 按状态统计订单数 */
    @Select("select count(*) from rental_order where status = #{status}")
    long countByStatus(String status);

    /** 查询最近 5 条订单（关联用户名和商品名），用于后台看板 */
    @Select("select o.order_no as orderNo, u.username as username, p.name as productName, o.status as status from rental_order o left join sys_user u on o.user_id = u.id left join rental_product p on o.product_id = p.id order by o.id desc limit 5")
    List<Map<String, Object>> findRecent();

    /** 按用户 ID 查询其全部订单 */
    @Select("select id, order_no, user_id, product_id, rent_hours, order_amount, contact_info, delivery_note, status from rental_order where user_id = #{userId} order by id desc")
    List<RentalOrder> findByUserId(Long userId);

    /** 查询全部订单（管理员用），关联用户名和商品名 */
    @Select("select o.id, o.order_no, o.user_id, o.product_id, o.rent_hours, o.order_amount, o.contact_info, o.delivery_note, o.status, u.username, p.name as productName from rental_order o left join sys_user u on o.user_id = u.id left join rental_product p on o.product_id = p.id order by o.id desc")
    List<Map<String, Object>> findAllWithDetails();

    /** 按订单号查询 */
    @Select("select id, order_no, user_id, product_id, rent_hours, order_amount, contact_info, delivery_note, status from rental_order where order_no = #{orderNo}")
    RentalOrder findByOrderNo(String orderNo);

    /** 按 ID 查询订单 */
    @Select("select id, order_no, user_id, product_id, rent_hours, order_amount, contact_info, delivery_note, status from rental_order where id = #{id}")
    RentalOrder findById(Long id);

    /**
     * 更新订单状态 — 核心业务流程：
     * WAITING_CONFIRM → IN_PROGRESS → COMPLETED
     * 任意状态 → CANCELLED
     * COMPLETED → AFTER_SALE
     */
    @Update("update rental_order set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
