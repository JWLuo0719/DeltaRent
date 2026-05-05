package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.RentalProduct;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 租赁商品数据访问层 — 负责 rental_product 表的增删改查，
 * 支撑前台账号展示和后台商品管理。
 */
@Mapper
public interface RentalProductMapper {

    /** 查询全部商品，按 ID 倒序 */
    @Select("select id, name, category, tag_text, hour_price, coin_amount_text, equipment_level_text, warehouse_value_text, status, description from rental_product order by id desc")
    List<RentalProduct> findAll();

    /** 按 ID 查询单个商品 */
    @Select("select id, name, category, tag_text, hour_price, coin_amount_text, equipment_level_text, warehouse_value_text, status, description from rental_product where id = #{id}")
    RentalProduct findById(Long id);

    /** 统计可租商品数量 */
    @Select("select count(*) from rental_product where status = 'AVAILABLE'")
    long countAvailable();

    /** 新增商品（管理员） */
    @Insert("insert into rental_product(name, category, tag_text, hour_price, coin_amount_text, equipment_level_text, warehouse_value_text, status, description) values(#{name}, #{category}, #{tagText}, #{hourPrice}, #{coinAmountText}, #{equipmentLevelText}, #{warehouseValueText}, #{status}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RentalProduct product);

    /** 更新商品信息（管理员） */
    @Update("update rental_product set name = #{name}, category = #{category}, tag_text = #{tagText}, hour_price = #{hourPrice}, coin_amount_text = #{coinAmountText}, equipment_level_text = #{equipmentLevelText}, warehouse_value_text = #{warehouseValueText}, status = #{status}, description = #{description} where id = #{id}")
    int update(RentalProduct product);

    /** 删除商品（管理员） */
    @Delete("delete from rental_product where id = #{id}")
    int deleteById(Long id);

    /** 更新商品状态（上架/下架/维护） */
    @Update("update rental_product set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
