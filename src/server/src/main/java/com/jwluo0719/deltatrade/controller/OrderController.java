package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.domain.RentalOrder;
import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalOrderMapper;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final RentalOrderMapper rentalOrderMapper;
    private final RentalProductMapper rentalProductMapper;

    public OrderController(RentalOrderMapper rentalOrderMapper, RentalProductMapper rentalProductMapper) {
        this.rentalOrderMapper = rentalOrderMapper;
        this.rentalProductMapper = rentalProductMapper;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> payload) {
        Long productId = readLong(payload, "accountId", 1001L);
        Integer rentHours = readInteger(payload, "rentHours", 1);
        if (rentHours < 1) {
            return ApiResponse.fail("Rent hours must be greater than 0");
        }

        RentalProduct product = rentalProductMapper.findById(productId);
        if (product == null) {
            return ApiResponse.fail("Rental product not found");
        }

        BigDecimal amount = product.getHourPrice().multiply(BigDecimal.valueOf(rentHours));
        RentalOrder order = new RentalOrder();
        order.setOrderNo("DR" + System.currentTimeMillis());
        order.setUserId(readLong(payload, "userId", 1L));
        order.setProductId(productId);
        order.setRentHours(rentHours);
        order.setOrderAmount(amount);
        order.setContactInfo(String.valueOf(payload.getOrDefault("contactInfo", "")));
        order.setDeliveryNote(String.valueOf(payload.getOrDefault("deliveryNote", "")));
        order.setStatus("WAITING_CONFIRM");
        rentalOrderMapper.insert(order);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("orderNo", order.getOrderNo());
        result.put("accountId", order.getProductId());
        result.put("rentHours", order.getRentHours());
        result.put("amount", order.getOrderAmount());
        result.put("status", order.getStatus());
        result.put("estimatedDelivery", "Waiting for admin confirmation");
        return ApiResponse.success("Order submitted", result);
    }

    private Long readLong(Map<String, Object> payload, String key, Long defaultValue) {
        Object value = payload.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private Integer readInteger(Map<String, Object> payload, String key, Integer defaultValue) {
        Object value = payload.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
