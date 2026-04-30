package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.RentalOrder;
import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalOrderMapper;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单业务服务 — 负责订单创建、查询和状态流转。
 * 订单状态机：WAITING_CONFIRM → IN_PROGRESS → COMPLETED → AFTER_SALE
 * 任意非终态可 → CANCELLED
 */
@Service
public class OrderService {

    private final RentalOrderMapper orderMapper;
    private final RentalProductMapper productMapper;

    public OrderService(RentalOrderMapper orderMapper, RentalProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    /** 用户 — 创建租赁订单 */
    public RentalOrder create(Long userId, Long productId, Integer rentHours, String contactInfo, String deliveryNote) {
        if (rentHours == null || rentHours < 1) {
            throw new IllegalArgumentException("租赁时长必须大于 0");
        }

        RentalProduct product = productMapper.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("租赁商品不存在");
        }
        if (!"AVAILABLE".equals(product.getStatus())) {
            throw new IllegalArgumentException("该商品当前不可租");
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
        order.setStatus("WAITING_CONFIRM"); // 新订单等待管理员确认
        orderMapper.insert(order);
        return order;
    }

    /** 用户 — 查看自己的订单 */
    public List<RentalOrder> listByUser(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    /** 管理员 — 查看全部订单（含用户名和商品名） */
    public List<Map<String, Object>> listAllWithDetails() {
        return orderMapper.findAllWithDetails();
    }

    /** 按 ID 查订单 */
    public RentalOrder getById(Long id) {
        return orderMapper.findById(id);
    }

    /**
     * 订单状态流转 — 校验状态转移是否合法后更新。
     * WAITING_CONFIRM → IN_PROGRESS → COMPLETED → AFTER_SALE
     * 任意态 → CANCELLED
     */
    public void transitionStatus(Long orderId, String targetStatus) {
        RentalOrder order = orderMapper.findById(orderId);
        if (order == null) throw new IllegalArgumentException("订单不存在");

        String current = order.getStatus();
        if (!isValidTransition(current, targetStatus)) {
            throw new IllegalArgumentException(
                    "不允许从 " + current + " 变更为 " + targetStatus);
        }
        orderMapper.updateStatus(orderId, targetStatus);
    }

    /** 统计全部订单数 */
    public long countAll() { return orderMapper.countAll(); }

    /** 按状态统计订单数 */
    public long countByStatus(String status) { return orderMapper.countByStatus(status); }

    /** 最近订单列表（看板用） */
    public List<Map<String, Object>> listRecent() { return orderMapper.findRecent(); }

    /** 判断状态流转是否合法 */
    private boolean isValidTransition(String from, String to) {
        if ("CANCELLED".equals(to) && !"COMPLETED".equals(from) && !"CANCELLED".equals(from)) {
            return true; // 任意非终态可取消
        }
        return switch (from) {
            case "WAITING_CONFIRM" -> "IN_PROGRESS".equals(to) || "CANCELLED".equals(to);
            case "IN_PROGRESS" -> "COMPLETED".equals(to) || "CANCELLED".equals(to);
            case "COMPLETED" -> "AFTER_SALE".equals(to);
            default -> false;
        };
    }
}
