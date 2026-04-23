package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> payload) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("orderNo", "DR" + System.currentTimeMillis());
        result.put("accountId", payload.get("accountId"));
        result.put("rentHours", payload.get("rentHours"));
        result.put("status", "WAITING_CONFIRM");
        result.put("estimatedDelivery", "5分钟内由客服确认");
        return ApiResponse.success("订单已提交", result);
    }
}
