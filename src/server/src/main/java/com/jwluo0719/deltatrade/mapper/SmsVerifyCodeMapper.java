package com.jwluo0719.deltatrade.mapper;

import com.jwluo0719.deltatrade.domain.SmsVerifyCode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SmsVerifyCodeMapper {

    @Insert("""
            insert into sms_verify_code(phone, code, type, expire_time, created_at)
            values(#{phone}, #{code}, #{type}, #{expireTime}, #{createdAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SmsVerifyCode verifyCode);

    @Select("""
            select id, phone, code, type, expire_time, created_at, used_at
            from sms_verify_code
            where phone = #{phone}
            order by created_at desc, id desc
            limit 1
            """)
    SmsVerifyCode findLatestByPhone(String phone);

    @Select("""
            select id, phone, code, type, expire_time, created_at, used_at
            from sms_verify_code
            where phone = #{phone}
              and type = #{type}
            order by created_at desc, id desc
            limit 1
            """)
    SmsVerifyCode findLatestByPhoneAndType(@Param("phone") String phone, @Param("type") String type);

    @Update("""
            update sms_verify_code
            set used_at = #{usedAt}
            where id = #{id}
              and used_at is null
            """)
    int markUsed(@Param("id") Long id, @Param("usedAt") java.time.LocalDateTime usedAt);
}
