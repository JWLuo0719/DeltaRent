package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.RentalProduct;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RentalProductMapper {

    @Select("""
            select id, name, category, tag_text, hour_price, coin_amount,
                   equipment_level_text, warehouse_value_text, login_method,
                   ratio_text, insurance_box_text, stamina_text, weight_text,
                   rank_text, kd_text, diving_level_text, login_region,
                   trade_time_text, rental_days, deposit, knife_skin_text,
                   weapon_skin_text, character_skin_text, cover_image_url,
                   status, description
            from rental_product
            order by id desc
            """)
    List<RentalProduct> findAll();

    @Select("""
            select id, name, category, tag_text, hour_price, coin_amount,
                   equipment_level_text, warehouse_value_text, login_method,
                   ratio_text, insurance_box_text, stamina_text, weight_text,
                   rank_text, kd_text, diving_level_text, login_region,
                   trade_time_text, rental_days, deposit, knife_skin_text,
                   weapon_skin_text, character_skin_text, cover_image_url,
                   status, description
            from rental_product
            where id = #{id}
            """)
    RentalProduct findById(Long id);

    @Select("select count(*) from rental_product where status = 'AVAILABLE'")
    long countAvailable();

    @Insert("""
            insert into rental_product(
                name, category, tag_text, hour_price, coin_amount,
                equipment_level_text, warehouse_value_text, login_method,
                ratio_text, insurance_box_text, stamina_text, weight_text,
                rank_text, kd_text, diving_level_text, login_region,
                trade_time_text, rental_days, deposit, knife_skin_text,
                weapon_skin_text, character_skin_text, cover_image_url,
                status, description
            ) values(
                #{name}, #{category}, #{tagText}, #{hourPrice}, #{coinAmount},
                #{equipmentLevelText}, #{warehouseValueText}, #{loginMethod},
                #{ratioText}, #{insuranceBoxText}, #{staminaText}, #{weightText},
                #{rankText}, #{kdText}, #{divingLevelText}, #{loginRegion},
                #{tradeTimeText}, #{rentalDays}, #{deposit}, #{knifeSkinText},
                #{weaponSkinText}, #{characterSkinText}, #{coverImageUrl},
                #{status}, #{description}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RentalProduct product);

    @Update("""
            update rental_product
            set name = #{name},
                category = #{category},
                tag_text = #{tagText},
                hour_price = #{hourPrice},
                coin_amount = #{coinAmount},
                equipment_level_text = #{equipmentLevelText},
                warehouse_value_text = #{warehouseValueText},
                login_method = #{loginMethod},
                ratio_text = #{ratioText},
                insurance_box_text = #{insuranceBoxText},
                stamina_text = #{staminaText},
                weight_text = #{weightText},
                rank_text = #{rankText},
                kd_text = #{kdText},
                diving_level_text = #{divingLevelText},
                login_region = #{loginRegion},
                trade_time_text = #{tradeTimeText},
                rental_days = #{rentalDays},
                deposit = #{deposit},
                knife_skin_text = #{knifeSkinText},
                weapon_skin_text = #{weaponSkinText},
                character_skin_text = #{characterSkinText},
                cover_image_url = #{coverImageUrl},
                status = #{status},
                description = #{description}
            where id = #{id}
            """)
    int update(RentalProduct product);

    @Delete("delete from rental_product where id = #{id}")
    int deleteById(Long id);

    @Update("update rental_product set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
