package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.RentalOrder;
import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalOrderMapper;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderServiceTest {

    private InMemoryRentalOrderMapper orderMapper;
    private InMemoryRentalProductMapper productMapper;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderMapper = new InMemoryRentalOrderMapper();
        productMapper = new InMemoryRentalProductMapper();
        productMapper.products.add(product(1001L, "High Rank Account A01", "AVAILABLE", "18.00"));
        productMapper.products.add(product(1002L, "Maintenance Account B02", "MAINTENANCE", "8.00"));
        orderService = new OrderService(orderMapper, productMapper);
    }

    @Test
    void createCalculatesAmountAndInitialStatus() {
        RentalOrder order = orderService.create(1L, 1001L, 6, "13800000000", "demo");

        assertEquals(1L, order.getId());
        assertEquals(new BigDecimal("18.00"), order.getUnitPrice());
        assertEquals(new BigDecimal("108.00"), order.getOrderAmount());
        assertEquals("WAITING_CONFIRM", order.getStatus());
        assertTrue(order.getOrderNo().startsWith("DR"));
    }

    @Test
    void createRejectsUnavailableProduct() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.create(1L, 1002L, 1, "13800000000", "")
        );

        assertEquals("该账号当前不可租", ex.getMessage());
    }

    @Test
    void createRejectsDuplicateActiveOrderForSameUserAndProduct() {
        orderService.create(1L, 1001L, 1, "13800000000", "");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.create(1L, 1001L, 1, "13800000000", "")
        );

        assertEquals("该账号已有进行中的订单，请勿重复提交", ex.getMessage());
    }

    @Test
    void transitionStatusAllowsExpectedFlow() {
        RentalOrder order = orderService.create(1L, 1001L, 1, "13800000000", "");

        orderService.transitionStatus(order.getId(), "IN_PROGRESS");
        assertEquals("IN_PROGRESS", orderMapper.findById(order.getId()).getStatus());

        orderService.transitionStatus(order.getId(), "COMPLETED");
        assertEquals("COMPLETED", orderMapper.findById(order.getId()).getStatus());

        orderService.transitionStatus(order.getId(), "AFTER_SALE");
        assertEquals("AFTER_SALE", orderMapper.findById(order.getId()).getStatus());
    }

    @Test
    void transitionStatusRejectsInvalidJump() {
        RentalOrder order = orderService.create(1L, 1001L, 1, "13800000000", "");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.transitionStatus(order.getId(), "COMPLETED")
        );

        assertEquals("不允许从 WAITING_CONFIRM 变更为 COMPLETED", ex.getMessage());
    }

    private RentalProduct product(Long id, String name, String status, String price) {
        RentalProduct product = new RentalProduct();
        product.setId(id);
        product.setName(name);
        product.setCategory("demo");
        product.setTagText("Full warehouse");
        product.setHourPrice(new BigDecimal(price));
        product.setCoinAmount(1_000_000L);
        product.setEquipmentLevelText("Advanced equipment set");
        product.setWarehouseValueText("High-value warehouse");
        product.setStatus(status);
        return product;
    }

    private static class InMemoryRentalOrderMapper implements RentalOrderMapper {

        private final List<RentalOrder> orders = new ArrayList<>();
        private long nextId = 1L;

        @Override
        public int insert(RentalOrder order) {
            order.setId(nextId++);
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(order.getCreatedAt());
            orders.add(order);
            return 1;
        }

        @Override
        public long countAll() {
            return orders.size();
        }

        @Override
        public long countByStatus(String status) {
            return orders.stream().filter(order -> status.equals(order.getStatus())).count();
        }

        @Override
        public long countActiveByUserAndProduct(Long userId, Long productId) {
            return orders.stream()
                    .filter(order -> userId.equals(order.getUserId()))
                    .filter(order -> productId.equals(order.getProductId()))
                    .filter(order -> "WAITING_CONFIRM".equals(order.getStatus()) || "IN_PROGRESS".equals(order.getStatus()))
                    .count();
        }

        @Override
        public List<Map<String, Object>> findRecent() {
            return List.of();
        }

        @Override
        public List<RentalOrder> findByUserId(Long userId) {
            return orders.stream().filter(order -> userId.equals(order.getUserId())).toList();
        }

        @Override
        public List<Map<String, Object>> findByUserIdWithDetails(Long userId) {
            return orders.stream()
                    .filter(order -> userId.equals(order.getUserId()))
                    .map(this::toDetail)
                    .toList();
        }

        @Override
        public List<Map<String, Object>> findAllWithDetails() {
            return orders.stream().map(this::toDetail).toList();
        }

        @Override
        public RentalOrder findByOrderNo(String orderNo) {
            return orders.stream().filter(order -> orderNo.equals(order.getOrderNo())).findFirst().orElse(null);
        }

        @Override
        public RentalOrder findById(Long id) {
            return orders.stream().filter(order -> id.equals(order.getId())).findFirst().orElse(null);
        }

        @Override
        public Map<String, Object> findDetailByOrderNo(String orderNo) {
            RentalOrder order = findByOrderNo(orderNo);
            return order == null ? null : toDetail(order);
        }

        @Override
        public int updateStatus(Long id, String status) {
            RentalOrder order = findById(id);
            if (order == null) {
                return 0;
            }
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            return 1;
        }

        private Map<String, Object> toDetail(RentalOrder order) {
            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("id", order.getId());
            detail.put("orderNo", order.getOrderNo());
            detail.put("userId", order.getUserId());
            detail.put("productId", order.getProductId());
            detail.put("rentHours", order.getRentHours());
            detail.put("amount", order.getOrderAmount());
            detail.put("status", order.getStatus());
            return detail;
        }
    }

    private static class InMemoryRentalProductMapper implements RentalProductMapper {

        private final List<RentalProduct> products = new ArrayList<>();

        @Override
        public List<RentalProduct> findAll() {
            return products;
        }

        @Override
        public RentalProduct findById(Long id) {
            return products.stream().filter(product -> id.equals(product.getId())).findFirst().orElse(null);
        }

        @Override
        public long countAvailable() {
            return products.stream().filter(product -> "AVAILABLE".equals(product.getStatus())).count();
        }

        @Override
        public int insert(RentalProduct product) {
            products.add(product);
            return 1;
        }

        @Override
        public int update(RentalProduct product) {
            return 1;
        }

        @Override
        public int deleteById(Long id) {
            return products.removeIf(product -> id.equals(product.getId())) ? 1 : 0;
        }

        @Override
        public int updateStatus(Long id, String status) {
            RentalProduct product = findById(id);
            if (product == null) {
                return 0;
            }
            product.setStatus(status);
            return 1;
        }
    }
}
