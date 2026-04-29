package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalProductMapper rentalProductMapper;

    public RentalController(RentalProductMapper rentalProductMapper) {
        this.rentalProductMapper = rentalProductMapper;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (RentalProduct product : rentalProductMapper.findAll()) {
            result.add(toView(product));
        }
        return ApiResponse.success(result);
    }

    private Map<String, Object> toView(RentalProduct product) {
        Map<String, Object> item = new LinkedHashMap<String, Object>();
        item.put("id", product.getId());
        item.put("name", product.getName());
        item.put("tag", product.getTagText());
        item.put("price", product.getHourPrice() + " / hour");
        item.put("status", product.getStatus());
        item.put("coinAmount", product.getCoinAmountText());
        item.put("equipmentLevel", product.getEquipmentLevelText());
        item.put("warehouseValue", product.getWarehouseValueText());
        return item;
    }
}
