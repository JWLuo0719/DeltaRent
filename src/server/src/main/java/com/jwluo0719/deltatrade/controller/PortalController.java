package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.mapper.RentalOrderMapper;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/portal")
public class PortalController {

    private final RentalProductMapper rentalProductMapper;
    private final RentalOrderMapper rentalOrderMapper;

    public PortalController(RentalProductMapper rentalProductMapper, RentalOrderMapper rentalOrderMapper) {
        this.rentalProductMapper = rentalProductMapper;
        this.rentalOrderMapper = rentalOrderMapper;
    }

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("heroTitle", "DeltaRent Account Rental Management");
        result.put("heroSubtitle", "A course prototype for account display, rental orders, and admin operations.");
        result.put("metrics", Arrays.asList(
            metric("Available accounts", String.valueOf(rentalProductMapper.countAvailable())),
            metric("Total orders", String.valueOf(rentalOrderMapper.countAll())),
            metric("Demo completion", "Ready")
        ));
        result.put("modules", Arrays.asList("Login", "Rental Products", "Order Create", "Order Status", "Admin Dashboard", "Notice Management"));
        result.put("notices", Arrays.asList(
            notice(1, "Demo Notice", "The Java backend is now connected to MySQL for core product and order data."),
            notice(2, "Test Account", "Use admin / 123456 after importing the seed SQL.")
        ));
        return ApiResponse.success(result);
    }

    private Map<String, Object> metric(String label, String value) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("label", label);
        item.put("value", value);
        return item;
    }

    private Map<String, Object> notice(int id, String title, String content) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("id", id);
        item.put("title", title);
        item.put("content", content);
        return item;
    }
}
