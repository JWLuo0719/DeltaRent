package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.mapper.RentalOrderMapper;
import com.jwluo0719.deltatrade.mapper.SysUserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RentalOrderMapper rentalOrderMapper;
    private final SysUserMapper sysUserMapper;

    public DashboardController(RentalOrderMapper rentalOrderMapper, SysUserMapper sysUserMapper) {
        this.rentalOrderMapper = rentalOrderMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("metrics", Arrays.asList(
            metric("Waiting orders", String.valueOf(rentalOrderMapper.countByStatus("WAITING_CONFIRM"))),
            metric("Running orders", String.valueOf(rentalOrderMapper.countByStatus("IN_PROGRESS"))),
            metric("Users", String.valueOf(sysUserMapper.countAll()))
        ));
        result.put("menus", Arrays.asList("User Management", "Rental Products", "Rental Orders", "Appeals", "Notices", "Price Rules"));
        result.put("recentOrders", recentOrders());
        return ApiResponse.success(result);
    }

    private Map<String, Object> metric(String label, String value) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("label", label);
        item.put("value", value);
        return item;
    }

    private List<Map<String, Object>> recentOrders() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> row : rentalOrderMapper.findRecent()) {
            result.add(order(
                String.valueOf(row.get("orderNo")),
                String.valueOf(row.get("username")),
                String.valueOf(row.get("productName")),
                String.valueOf(row.get("status"))
            ));
        }
        return result;
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
