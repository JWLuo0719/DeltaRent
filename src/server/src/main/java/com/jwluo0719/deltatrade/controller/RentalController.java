package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.service.ProductService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final ProductService productService;

    public RentalController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String tags,
                                                       @RequestParam(required = false) String sortBy) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (RentalProduct product : productService.listForBrowse(keyword, status, tags, sortBy)) {
            result.add(toView(product));
        }
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        RentalProduct product = productService.getById(id);
        if (product == null) return ApiResponse.fail("鍟嗗搧涓嶅瓨鍦?");
        return ApiResponse.success(toView(product));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> payload) {
        try {
            RentalProduct product = new RentalProduct();
            product.setName(String.valueOf(payload.getOrDefault("name", "")));
            product.setCategory(String.valueOf(payload.getOrDefault("category", "")));
            product.setTagText(String.valueOf(payload.getOrDefault("tagText", "")));
            product.setHourPrice(new BigDecimal(String.valueOf(payload.getOrDefault("hourPrice", "0"))));
            product.setCoinAmountText(String.valueOf(payload.getOrDefault("coinAmountText", "")));
            product.setEquipmentLevelText(String.valueOf(payload.getOrDefault("equipmentLevelText", "")));
            product.setWarehouseValueText(String.valueOf(payload.getOrDefault("warehouseValueText", "")));
            product.setStatus(String.valueOf(payload.getOrDefault("status", "AVAILABLE")));
            product.setDescription(String.valueOf(payload.getOrDefault("description", "")));
            productService.create(product);
            return ApiResponse.success("鍒涘缓鎴愬姛", toView(product));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            RentalProduct product = new RentalProduct();
            product.setId(id);
            product.setName(String.valueOf(payload.getOrDefault("name", "")));
            product.setCategory(String.valueOf(payload.getOrDefault("category", "")));
            product.setTagText(String.valueOf(payload.getOrDefault("tagText", "")));
            product.setHourPrice(new BigDecimal(String.valueOf(payload.getOrDefault("hourPrice", "0"))));
            product.setCoinAmountText(String.valueOf(payload.getOrDefault("coinAmountText", "")));
            product.setEquipmentLevelText(String.valueOf(payload.getOrDefault("equipmentLevelText", "")));
            product.setWarehouseValueText(String.valueOf(payload.getOrDefault("warehouseValueText", "")));
            product.setStatus(String.valueOf(payload.getOrDefault("status", "AVAILABLE")));
            product.setDescription(String.valueOf(payload.getOrDefault("description", "")));
            productService.update(product);
            return ApiResponse.success("鏇存柊鎴愬姛", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.success("鍒犻櫎鎴愬姛", null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "AVAILABLE");
        productService.updateStatus(id, status);
        return ApiResponse.success("鐘舵€佹洿鏂版垚鍔?", null);
    }

    private Map<String, Object> toView(RentalProduct product) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", product.getId());
        item.put("name", product.getName());
        item.put("category", product.getCategory());
        item.put("tagText", product.getTagText());
        item.put("hourPrice", product.getHourPrice());
        item.put("coinAmountText", product.getCoinAmountText());
        item.put("equipmentLevelText", product.getEquipmentLevelText());
        item.put("warehouseValueText", product.getWarehouseValueText());
        item.put("tag", product.getTagText());
        item.put("price", product.getHourPrice() + " / 灏忔椂");
        item.put("status", product.getStatus());
        item.put("coinAmount", product.getCoinAmountText());
        item.put("equipmentLevel", product.getEquipmentLevelText());
        item.put("warehouseValue", product.getWarehouseValueText());
        item.put("description", product.getDescription());
        item.put("isHot", "AVAILABLE".equals(product.getStatus())
                && product.getHourPrice() != null
                && product.getHourPrice().compareTo(new BigDecimal("15")) >= 0);
        return item;
    }
}
