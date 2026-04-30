package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.common.JwtUtil;
import com.jwluo0719.deltatrade.domain.RentalOrder;
import com.jwluo0719.deltatrade.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 订单控制器 — 负责下单、订单查询和状态管理。
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 用户 — 创建租赁订单 */
    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> payload,
                                                    @RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            Long userId = extractUserId(auth); // 从 JWT 中提取当前登录用户
            Long productId = toLong(payload.get("accountId"), 0L);
            Integer rentHours = toInt(payload.get("rentHours"), 1);
            String contactInfo = String.valueOf(payload.getOrDefault("contactInfo", ""));
            String deliveryNote = String.valueOf(payload.getOrDefault("remark", ""));
            RentalOrder order = orderService.create(userId, productId, rentHours, contactInfo, deliveryNote);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("orderNo", order.getOrderNo());
            result.put("accountId", order.getProductId());
            result.put("rentHours", order.getRentHours());
            result.put("amount", order.getOrderAmount());
            result.put("status", order.getStatus());
            result.put("estimatedDelivery", "等待管理员确认");
            return ApiResponse.success("订单已提交", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 用户 — 查看自己的订单 */
    @GetMapping("/my")
    public ApiResponse<List<Map<String, Object>>> myOrders(@RequestHeader(value = "Authorization", required = false) String auth) {
        Long userId = extractUserId(auth);
        List<Map<String, Object>> result = new ArrayList<>();
        for (RentalOrder o : orderService.listByUser(userId)) {
            result.add(toView(o));
        }
        return ApiResponse.success(result);
    }

    /** 管理员 — 查看全部订单（含用户名和商品名） */
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> listAll() {
        return ApiResponse.success(orderService.listAllWithDetails());
    }

    /** 查看单个订单详情 */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        RentalOrder o = orderService.getById(id);
        if (o == null) return ApiResponse.fail("订单不存在");
        return ApiResponse.success(toView(o));
    }

    /** 管理员 — 变更订单状态 */
    @PutMapping("/{id}/status")
    public ApiResponse<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String status = payload.getOrDefault("status", "");
            orderService.transitionStatus(id, status);
            return ApiResponse.success("状态更新成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 从 Authorization 头提取用户 ID */
    private Long extractUserId(String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            Long uid = JwtUtil.getUserId(auth.substring(7));
            if (uid != null) return uid;
        }
        return 1L; // 兼容 Mock 模式，默认用 admin
    }

    private Long toLong(Object value, Long defaultVal) {
        if (value == null) return defaultVal;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(String.valueOf(value));
    }

    private Integer toInt(Object value, Integer defaultVal) {
        if (value == null) return defaultVal;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(String.valueOf(value));
    }

    private Map<String, Object> toView(RentalOrder o) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", o.getId());
        item.put("orderNo", o.getOrderNo());
        item.put("userId", o.getUserId());
        item.put("productId", o.getProductId());
        item.put("rentHours", o.getRentHours());
        item.put("amount", o.getOrderAmount());
        item.put("contactInfo", o.getContactInfo());
        item.put("deliveryNote", o.getDeliveryNote());
        item.put("status", o.getStatus());
        return item;
    }
}
