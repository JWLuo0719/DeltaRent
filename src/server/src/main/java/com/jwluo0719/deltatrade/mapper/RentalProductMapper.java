package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.RentalProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RentalProductMapper {

    @Select("select id, name, category, tag_text, hour_price, coin_amount_text, equipment_level_text, warehouse_value_text, status, description from rental_product order by id desc")
    List<RentalProduct> findAll();

    @Select("select id, name, category, tag_text, hour_price, coin_amount_text, equipment_level_text, warehouse_value_text, status, description from rental_product where id = #{id}")
    RentalProduct findById(Long id);

    @Select("select count(*) from rental_product where status = 'AVAILABLE'")
    long countAvailable();
}
