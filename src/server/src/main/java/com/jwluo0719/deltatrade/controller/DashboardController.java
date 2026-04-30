package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.service.OrderService;
import com.jwluo0719.deltatrade.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 后台管理看板控制器 — 展示核心运营指标、管理菜单入口和最近订单。
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final OrderService orderService;
    private final UserService userService;

    public DashboardController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /** 看板概览 — 待确认/进行中订单数、用户数、最近订单 */
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("metrics", Arrays.asList(
                metric("待确认订单", orderService.countByStatus("WAITING_CONFIRM")),
                metric("进行中订单", orderService.countByStatus("IN_PROGRESS")),
                metric("注册用户数", userService.countAll())
        ));
        result.put("menus", Arrays.asList(
                "用户管理", "账号管理", "租赁订单管理", "售后申诉管理", "公告管理", "价格规则管理"
        ));
        result.put("recentOrders", orderService.listRecent());
        return ApiResponse.success(result);
    }

    private Map<String, String> metric(String label, Object value) {
        Map<String, String> item = new LinkedHashMap<>();
        item.put("label", label);
        item.put("value", String.valueOf(value));
        return item;
    }
}
