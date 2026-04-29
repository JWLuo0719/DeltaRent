package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.RentalOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RentalOrderMapper {

    @Insert("insert into rental_order(order_no, user_id, product_id, rent_hours, order_amount, contact_info, delivery_note, status) values(#{orderNo}, #{userId}, #{productId}, #{rentHours}, #{orderAmount}, #{contactInfo}, #{deliveryNote}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RentalOrder order);

    @Select("select count(*) from rental_order")
    long countAll();

    @Select("select count(*) from rental_order where status = #{status}")
    long countByStatus(String status);

    @Select("select o.order_no as orderNo, u.username as username, p.name as productName, o.status as status from rental_order o left join sys_user u on o.user_id = u.id left join rental_product p on o.product_id = p.id order by o.id desc limit 5")
    List<Map<String, Object>> findRecent();
}
