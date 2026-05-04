package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.common.JwtUtil;
import com.jwluo0719.deltatrade.domain.RentalOrder;
import com.jwluo0719.deltatrade.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> payload,
                                                   @RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            Long userId = extractUserId(auth);
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
            result.put("estimatedDelivery", "5分钟内由客服确认");
            return ApiResponse.success("订单已提交", result);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/my")
    public ApiResponse<List<Map<String, Object>>> myOrders(@RequestHeader(value = "Authorization", required = false) String auth,
                                                            @RequestParam(required = false) String status) {
        try {
            Long userId = extractUserId(auth);
            return ApiResponse.success(orderService.listByUserWithDetails(userId, status));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> listAll(@RequestHeader(value = "Authorization", required = false) String auth,
                                                          @RequestParam(required = false) String status) {
        if (!isAdmin(auth)) {
            return ApiResponse.fail("无权访问");
        }
        return ApiResponse.success(orderService.listAllWithDetails(status));
    }

    @GetMapping("/{orderNo}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable String orderNo,
                                                   @RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            RentalOrder order = orderService.getByOrderNo(orderNo);
            if (order == null) return ApiResponse.fail("订单不存在");
            if (!isAdmin(auth) && !Objects.equals(order.getUserId(), extractUserId(auth))) {
                return ApiResponse.fail("无权查看该订单");
            }

            Map<String, Object> detail = orderService.getDetailByOrderNo(orderNo);
            if (detail == null) return ApiResponse.fail("订单不存在");
            detail.put("events", buildEvents(order));
            return ApiResponse.success(detail);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

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

    @PutMapping("/{orderNo}/cancel")
    public ApiResponse<?> cancel(@PathVariable String orderNo,
                                 @RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            RentalOrder order = orderService.getByOrderNo(orderNo);
            if (order == null) return ApiResponse.fail("订单不存在");
            if (!Objects.equals(order.getUserId(), extractUserId(auth)) && !isAdmin(auth)) {
                return ApiResponse.fail("无权取消该订单");
            }
            orderService.transitionStatus(order.getId(), "CANCELLED");
            return ApiResponse.success("订单已取消", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    private Long extractUserId(String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            Long uid = JwtUtil.getUserId(auth.substring(7));
            if (uid != null) return uid;
        }
        throw new IllegalArgumentException("请先登录");
    }

    private boolean isAdmin(String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            String role = JwtUtil.getRole(auth.substring(7));
            return "ADMIN".equals(role) || "CS".equals(role);
        }
        return false;
    }

    private Long toLong(Object value, Long defaultVal) {
        if (value == null) return defaultVal;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(String.valueOf(value));
    }

    private Integer toInt(Object value, Integer defaultVal) {
        if (value == null) return defaultVal;
        if (value instanceof Number number) return number.intValue();
        return Integer.parseInt(String.valueOf(value));
    }

    private List<Map<String, Object>> buildEvents(RentalOrder order) {
        List<Map<String, Object>> events = new ArrayList<>();
        events.add(event(formatDateTime(order.getCreatedAt()), "订单已提交"));

        switch (order.getStatus()) {
            case "IN_PROGRESS", "COMPLETED", "AFTER_SALE" ->
                    events.add(event(formatDateTime(order.getUpdatedAt()), "客服已确认，订单进行中"));
            case "CANCELLED" ->
                    events.add(event(formatDateTime(order.getUpdatedAt()), "订单已取消"));
            default -> {
            }
        }

        if ("COMPLETED".equals(order.getStatus()) || "AFTER_SALE".equals(order.getStatus())) {
            events.add(event(formatDateTime(order.getUpdatedAt()), "订单已完成"));
        }
        if ("AFTER_SALE".equals(order.getStatus())) {
            events.add(event(formatDateTime(order.getUpdatedAt()), "售后已开启"));
        }
        return events;
    }

    private Map<String, Object> event(String time, String content) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("time", time);
        item.put("content", content);
        return item;
    }

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return "";
        }
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
