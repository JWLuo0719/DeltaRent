package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.RentalOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface RentalOrderMapper {

    @Insert("""
            insert into rental_order(order_no, user_id, product_id, unit_price, service_fee, rent_days, order_amount, deposit_amount, contact_info, delivery_note, status)
            values(#{orderNo}, #{userId}, #{productId}, #{unitPrice}, #{serviceFee}, #{rentDays}, #{orderAmount}, #{depositAmount}, #{contactInfo}, #{deliveryNote}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RentalOrder order);

    @Select("select count(*) from rental_order")
    long countAll();

    @Select("select count(*) from rental_order where status = #{status}")
    long countByStatus(String status);

    @Select("""
            select count(*)
            from rental_order
            where user_id = #{userId}
              and product_id = #{productId}
              and status in ('WAITING_CONFIRM', 'IN_PROGRESS')
            """)
    long countActiveByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("""
            select o.order_no as orderNo,
                   coalesce(u.nickname, u.username) as buyer,
                   p.name as item,
                   o.status as status
            from rental_order o
            left join sys_user u on o.user_id = u.id
            left join rental_product p on o.product_id = p.id
            order by o.id desc
            limit 5
            """)
    List<Map<String, Object>> findRecent();

    @Select("""
            select id, order_no, user_id, product_id, unit_price, service_fee, rent_days, order_amount, deposit_amount, contact_info, delivery_note, status, start_time, end_time, created_at, updated_at
            from rental_order
            where user_id = #{userId}
            order by id desc
            """)
    List<RentalOrder> findByUserId(Long userId);

    @Select("""
            select o.id,
                   o.order_no as orderNo,
                   o.user_id as userId,
                   o.product_id as productId,
                   o.unit_price as unitPrice,
                   o.deposit_amount as depositAmount,
                   o.service_fee as serviceFee,
                   o.rent_days as rentDays,
                   o.order_amount as amount,
                   o.contact_info as contactInfo,
                   o.delivery_note as remark,
                   o.status as status,
                   o.start_time as startTime,
                   o.end_time as endTime,
                   o.created_at as createdAt,
                   o.updated_at as updatedAt,
                   coalesce(u.nickname, u.username) as buyer,
                   p.name as item
            from rental_order o
            left join sys_user u on o.user_id = u.id
            left join rental_product p on o.product_id = p.id
            where o.user_id = #{userId}
            order by o.id desc
            """)
    List<Map<String, Object>> findByUserIdWithDetails(Long userId);

    @Select("""
            select o.id,
                   o.order_no as orderNo,
                   o.user_id as userId,
                   o.product_id as productId,
                   o.unit_price as unitPrice,
                   o.deposit_amount as depositAmount,
                   o.service_fee as serviceFee,
                   o.rent_days as rentDays,
                   o.order_amount as amount,
                   o.contact_info as contactInfo,
                   o.delivery_note as remark,
                   o.status as status,
                   o.start_time as startTime,
                   o.end_time as endTime,
                   o.created_at as createdAt,
                   o.updated_at as updatedAt,
                   coalesce(u.nickname, u.username) as buyer,
                   p.name as item
            from rental_order o
            left join sys_user u on o.user_id = u.id
            left join rental_product p on o.product_id = p.id
            order by o.id desc
            """)
    List<Map<String, Object>> findAllWithDetails();

    @Select("""
            select id, order_no, user_id, product_id, unit_price, service_fee, rent_days, order_amount, deposit_amount, contact_info, delivery_note, status, start_time, end_time, created_at, updated_at
            from rental_order
            where order_no = #{orderNo}
            """)
    RentalOrder findByOrderNo(String orderNo);

    @Select("""
            select id, order_no, user_id, product_id, unit_price, service_fee, rent_days, order_amount, deposit_amount, contact_info, delivery_note, status, start_time, end_time, created_at, updated_at
            from rental_order
            where id = #{id}
            """)
    RentalOrder findById(Long id);

    @Select("""
            select o.id,
                   o.order_no as orderNo,
                   o.user_id as userId,
                   o.product_id as productId,
                   o.unit_price as unitPrice,
                   o.deposit_amount as depositAmount,
                   o.service_fee as serviceFee,
                   o.rent_days as rentDays,
                   o.order_amount as amount,
                   o.contact_info as contactInfo,
                   o.delivery_note as remark,
                   o.status as status,
                   o.start_time as startTime,
                   o.end_time as endTime,
                   o.created_at as createdAt,
                   o.updated_at as updatedAt,
                   coalesce(u.nickname, u.username) as buyer,
                   p.name as item
            from rental_order o
            left join sys_user u on o.user_id = u.id
            left join rental_product p on o.product_id = p.id
            where o.order_no = #{orderNo}
            limit 1
            """)
    Map<String, Object> findDetailByOrderNo(String orderNo);

    @Update("""
            update rental_order
            set status = #{status},
                start_time = #{startTime},
                end_time = #{endTime}
            where id = #{id}
            """)
    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("startTime") java.time.LocalDateTime startTime,
                     @Param("endTime") java.time.LocalDateTime endTime);
}
