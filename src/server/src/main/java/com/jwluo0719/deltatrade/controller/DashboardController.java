package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("metrics", Arrays.asList(
            metric("待确认订单", "6"),
            metric("进行中订单", "9"),
            metric("今日新增用户", "4")
        ));
        result.put("menus", Arrays.asList("用户管理", "账号管理", "租赁订单管理", "售后申诉管理", "公告管理", "价格规则管理"));
        result.put("recentOrders", Arrays.asList(
            order("DR20260423001", "jwluo", "高战账号 A01", "待确认"),
            order("DR20260423002", "test_user", "活动账号 B02", "进行中")
        ));
        return ApiResponse.success(result);
    }

    private Map<String, Object> metric(String label, String value) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("label", label);
        item.put("value", value);
        return item;
    }

    private Map<String, Object> order(String orderNo, String user, String itemName, String status) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("orderNo", orderNo);
        item.put("user", user);
        item.put("item", itemName);
        item.put("status", status);
        return item;
    }
}
