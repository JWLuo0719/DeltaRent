package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.RentalOrder;
import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalOrderMapper;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final RentalOrderMapper orderMapper;
    private final RentalProductMapper productMapper;

    public OrderService(RentalOrderMapper orderMapper, RentalProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    public RentalOrder create(Long userId, Long productId, Integer rentHours, String contactInfo, String deliveryNote) {
        if (rentHours == null || rentHours < 1) {
            throw new IllegalArgumentException("绉熻祦鏃堕暱蹇呴』澶т簬 0");
        }

        RentalProduct product = productMapper.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("绉熻祦鍟嗗搧涓嶅瓨鍦?");
        }
        if (!"AVAILABLE".equals(product.getStatus())) {
            throw new IllegalArgumentException("璇ュ晢鍝佸綋鍓嶄笉鍙");
        }

        BigDecimal amount = product.getHourPrice().multiply(BigDecimal.valueOf(rentHours));
        RentalOrder order = new RentalOrder();
        order.setOrderNo("DR" + System.currentTimeMillis());
        order.setUserId(userId);
        order.setProductId(productId);
        order.setRentHours(rentHours);
        order.setOrderAmount(amount);
        order.setContactInfo(contactInfo != null ? contactInfo : "");
        order.setDeliveryNote(deliveryNote != null ? deliveryNote : "");
        order.setStatus("WAITING_CONFIRM");
        orderMapper.insert(order);
        return order;
    }

    public List<RentalOrder> listByUser(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    public List<Map<String, Object>> listByUserWithDetails(Long userId, String status) {
        List<Map<String, Object>> orders = orderMapper.findByUserIdWithDetails(userId);
        if (status == null || status.isBlank()) {
            return orders;
        }
        return orders.stream()
                .filter(order -> status.equals(order.get("status")))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> listAllWithDetails() {
        return orderMapper.findAllWithDetails();
    }

    public List<Map<String, Object>> listAllWithDetails(String status) {
        if (status == null || status.isBlank()) {
            return orderMapper.findAllWithDetails();
        }
        return orderMapper.findAllWithDetails().stream()
                .filter(order -> status.equals(order.get("status")))
                .collect(Collectors.toList());
    }

    public RentalOrder getById(Long id) {
        return orderMapper.findById(id);
    }

    public RentalOrder getByOrderNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }

    public Map<String, Object> getDetailByOrderNo(String orderNo) {
        return orderMapper.findDetailByOrderNo(orderNo);
    }

    public void transitionStatus(Long orderId, String targetStatus) {
        RentalOrder order = orderMapper.findById(orderId);
        if (order == null) throw new IllegalArgumentException("璁㈠崟涓嶅瓨鍦?");

        String current = order.getStatus();
        if (!isValidTransition(current, targetStatus)) {
            throw new IllegalArgumentException("涓嶅厑璁镐粠 " + current + " 鍙樻洿涓? " + targetStatus);
        }
        orderMapper.updateStatus(orderId, targetStatus);
    }

    public long countAll() {
        return orderMapper.countAll();
    }

    public long countByStatus(String status) {
        return orderMapper.countByStatus(status);
    }

    public List<Map<String, Object>> listRecent() {
        return orderMapper.findRecent();
    }

    private boolean isValidTransition(String from, String to) {
        if ("CANCELLED".equals(to) && !"COMPLETED".equals(from) && !"CANCELLED".equals(from)) {
            return true;
        }
        return switch (from) {
            case "WAITING_CONFIRM" -> "IN_PROGRESS".equals(to) || "CANCELLED".equals(to);
            case "IN_PROGRESS" -> "COMPLETED".equals(to) || "CANCELLED".equals(to);
            case "COMPLETED" -> "AFTER_SALE".equals(to);
            default -> false;
        };
    }
}
