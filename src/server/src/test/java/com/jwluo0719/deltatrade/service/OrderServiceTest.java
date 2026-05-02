package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.RentalOrder;
import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalOrderMapper;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private RentalOrderMapper orderMapper;
    @Mock
    private RentalProductMapper productMapper;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderMapper, productMapper);
    }

    // ==================== create ====================

    @Test
    void create_shouldSucceed_whenProductAvailable() {
        RentalProduct product = new RentalProduct();
        product.setId(1001L);
        product.setHourPrice(new BigDecimal("18.00"));
        product.setStatus("AVAILABLE");
        when(productMapper.findById(1001L)).thenReturn(product);

        RentalOrder order = orderService.create(1L, 1001L, 3, "13800000000", "remark");

        assertNotNull(order);
        assertEquals("WAITING_CONFIRM", order.getStatus());
        assertEquals(new BigDecimal("54.00"), order.getOrderAmount());
        assertTrue(order.getOrderNo().startsWith("DR"));
        verify(orderMapper).insert(any(RentalOrder.class));
    }

    @Test
    void create_shouldFail_whenRentHoursZero() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.create(1L, 1001L, 0, "", ""));
    }

    @Test
    void create_shouldFail_whenRentHoursNull() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.create(1L, 1001L, null, "", ""));
    }

    @Test
    void create_shouldFail_whenProductNotFound() {
        when(productMapper.findById(9999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.create(1L, 9999L, 3, "", ""));
    }

    @Test
    void create_shouldFail_whenProductNotAvailable() {
        RentalProduct product = new RentalProduct();
        product.setStatus("MAINTENANCE");
        when(productMapper.findById(1001L)).thenReturn(product);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.create(1L, 1001L, 3, "", ""));
    }

    // ==================== status transitions ====================

    @Test
    void transition_shouldAllow_waitingConfirm_to_inProgress() {
        RentalOrder order = new RentalOrder();
        order.setStatus("WAITING_CONFIRM");
        when(orderMapper.findById(1L)).thenReturn(order);

        assertDoesNotThrow(() -> orderService.transitionStatus(1L, "IN_PROGRESS"));
        verify(orderMapper).updateStatus(1L, "IN_PROGRESS");
    }

    @Test
    void transition_shouldAllow_waitingConfirm_to_cancelled() {
        RentalOrder order = new RentalOrder();
        order.setStatus("WAITING_CONFIRM");
        when(orderMapper.findById(1L)).thenReturn(order);

        assertDoesNotThrow(() -> orderService.transitionStatus(1L, "CANCELLED"));
    }

    @Test
    void transition_shouldAllow_inProgress_to_completed() {
        RentalOrder order = new RentalOrder();
        order.setStatus("IN_PROGRESS");
        when(orderMapper.findById(1L)).thenReturn(order);

        assertDoesNotThrow(() -> orderService.transitionStatus(1L, "COMPLETED"));
    }

    @Test
    void transition_shouldAllow_completed_to_afterSale() {
        RentalOrder order = new RentalOrder();
        order.setStatus("COMPLETED");
        when(orderMapper.findById(1L)).thenReturn(order);

        assertDoesNotThrow(() -> orderService.transitionStatus(1L, "AFTER_SALE"));
    }

    @Test
    void transition_shouldReject_skipStep() {
        RentalOrder order = new RentalOrder();
        order.setStatus("WAITING_CONFIRM");
        when(orderMapper.findById(1L)).thenReturn(order);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.transitionStatus(1L, "COMPLETED"));
    }

    @Test
    void transition_shouldReject_invalidStatus() {
        RentalOrder order = new RentalOrder();
        order.setStatus("COMPLETED");
        when(orderMapper.findById(1L)).thenReturn(order);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.transitionStatus(1L, "WAITING_CONFIRM"));
    }

    @Test
    void transition_shouldFail_whenOrderNotFound() {
        when(orderMapper.findById(9999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.transitionStatus(9999L, "IN_PROGRESS"));
    }

    @Test
    void transition_shouldReject_completed_to_cancelled() {
        RentalOrder order = new RentalOrder();
        order.setStatus("COMPLETED");
        when(orderMapper.findById(1L)).thenReturn(order);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.transitionStatus(1L, "CANCELLED"));
    }
}
