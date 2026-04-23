package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.success(Arrays.asList(
            rental(1001, "高战账号 A01", "满配仓库", "￥28 / 小时", "可租", "1200万哈夫币", "六套毕业装", "高价值仓库"),
            rental(1002, "活动账号 B02", "稀有外观", "￥18 / 小时", "可租", "340万哈夫币", "中高配作战装", "活动收藏资源"),
            rental(1003, "新手体验号 C03", "新手试用", "￥9 / 小时", "维护中", "80万哈夫币", "基础装备", "入门资源")
        ));
    }

    private Map<String, Object> rental(int id, String name, String tag, String price, String status,
                                       String coinAmount, String equipmentLevel, String warehouseValue) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("id", id);
        item.put("name", name);
        item.put("tag", tag);
        item.put("price", price);
        item.put("status", status);
        item.put("coinAmount", coinAmount);
        item.put("equipmentLevel", equipmentLevel);
        item.put("warehouseValue", warehouseValue);
        return item;
    }
}
