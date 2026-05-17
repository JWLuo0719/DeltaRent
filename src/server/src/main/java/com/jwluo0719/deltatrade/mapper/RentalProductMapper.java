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
            select id, name, owner_id, category, tag_text, price, deposit,
                   rental_days, coin_amount, login_method, insurance_box,
                   stamina_level, weight_level, rank_text, kd, diving_level,
                   ratio_text, login_region, trade_time_text, knife_skin_text,
                   weapon_skin_text, character_skin_text, cover_image_url,
                   level, helmet_count, armor_count, awm_ammo_count,
                   nine_grid_trial_card_count, recent_ban_record,
                   warehouse_value_text, description, status, created_at, updated_at
            from rental_product where deleted_at is null order by id desc
            """)
    List<RentalProduct> findAll();

    @Select("""
            select id, name, owner_id, category, tag_text, price, deposit,
                   rental_days, coin_amount, login_method, insurance_box,
                   stamina_level, weight_level, rank_text, kd, diving_level,
                   ratio_text, login_region, trade_time_text, knife_skin_text,
                   weapon_skin_text, character_skin_text, cover_image_url,
                   level, helmet_count, armor_count, awm_ammo_count,
                   nine_grid_trial_card_count, recent_ban_record,
                   warehouse_value_text, description, status, created_at, updated_at
            from rental_product where id = #{id} and deleted_at is null
            """)
    RentalProduct findById(Long id);

    @Select("select count(*) from rental_product where status = 'AVAILABLE' and deleted_at is null")
    long countAvailable();

    @Insert("""
            insert into rental_product(
                name, owner_id, category, tag_text, price, deposit,
                rental_days, coin_amount, login_method, insurance_box,
                stamina_level, weight_level, rank_text, kd, diving_level,
                ratio_text, login_region, trade_time_text, knife_skin_text,
                weapon_skin_text, character_skin_text, cover_image_url,
                level, helmet_count, armor_count, awm_ammo_count,
                nine_grid_trial_card_count, recent_ban_record,
                warehouse_value_text, description, status
            ) values(
                #{name}, #{ownerId}, #{category}, #{tagText}, #{price}, #{deposit},
                #{rentalDays}, #{coinAmount}, #{loginMethod}, #{insuranceBox},
                #{staminaLevel}, #{weightLevel}, #{rankText}, #{kd}, #{divingLevel},
                #{ratioText}, #{loginRegion}, #{tradeTimeText}, #{knifeSkinText},
                #{weaponSkinText}, #{characterSkinText}, #{coverImageUrl},
                #{level}, #{helmetCount}, #{armorCount}, #{awmAmmoCount},
                #{nineGridTrialCardCount}, #{recentBanRecord},
                #{warehouseValueText}, #{description}, #{status}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RentalProduct product);

    @Update("""
            update rental_product set
                name = #{name}, owner_id = #{ownerId}, category = #{category},
                tag_text = #{tagText}, price = #{price}, deposit = #{deposit},
                rental_days = #{rentalDays}, coin_amount = #{coinAmount},
                login_method = #{loginMethod}, insurance_box = #{insuranceBox},
                stamina_level = #{staminaLevel}, weight_level = #{weightLevel},
                rank_text = #{rankText}, kd = #{kd}, diving_level = #{divingLevel},
                ratio_text = #{ratioText}, login_region = #{loginRegion},
                trade_time_text = #{tradeTimeText}, knife_skin_text = #{knifeSkinText},
                weapon_skin_text = #{weaponSkinText}, character_skin_text = #{characterSkinText},
                level = #{level}, helmet_count = #{helmetCount}, armor_count = #{armorCount},
                awm_ammo_count = #{awmAmmoCount}, nine_grid_trial_card_count = #{nineGridTrialCardCount},
                recent_ban_record = #{recentBanRecord},
                cover_image_url = #{coverImageUrl}, warehouse_value_text = #{warehouseValueText},
                description = #{description}, status = #{status}
            where id = #{id}
            """)
    int update(RentalProduct product);

    @Delete("delete from rental_product where id = #{id}")
    int deleteById(Long id);

    @Update("update rental_product set status = #{status} where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
