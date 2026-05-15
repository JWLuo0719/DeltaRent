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
    public ApiResponse<Map<String, Object>> list(@RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String tags,
                                                 @RequestParam(required = false) String category,
                                                 @RequestParam(required = false) String level,
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String sortBy,
                                                 @RequestParam(required = false) Integer page,
                                                 @RequestParam(required = false) Integer pageSize) {
        Map<String, Object> payload = productService.listForBrowse(keyword, tags, category, level, status, sortBy, page, pageSize);
        @SuppressWarnings("unchecked")
        List<RentalProduct> list = (List<RentalProduct>) payload.getOrDefault("list", List.of());

        List<Map<String, Object>> result = new ArrayList<>();
        for (RentalProduct product : list) {
            result.add(toView(product));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("list", result);
        response.put("total", payload.get("total"));
        response.put("allTags", payload.get("allTags"));
        response.put("allCategories", payload.get("allCategories"));
        return ApiResponse.success(response);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        RentalProduct product = productService.getById(id);
        if (product == null) {
            return ApiResponse.fail("账号不存在");
        }
        return ApiResponse.success(toView(product));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> payload) {
        try {
            RentalProduct product = fromPayload(null, payload);
            productService.create(product);
            return ApiResponse.success("创建成功", toView(product));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            RentalProduct product = fromPayload(id, payload);
            productService.update(product);
            return ApiResponse.success("更新成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "AVAILABLE");
        productService.updateStatus(id, status);
        return ApiResponse.success("状态更新成功", null);
    }

    private RentalProduct fromPayload(Long id, Map<String, Object> payload) {
        RentalProduct product = new RentalProduct();
        if (id != null) {
            product.setId(id);
        }

        product.setName(String.valueOf(payload.getOrDefault("name", "")));
        product.setCategory(String.valueOf(payload.getOrDefault("category", "")));
        product.setTagText(String.valueOf(payload.getOrDefault("tagText", "")));
        product.setHourPrice(new BigDecimal(String.valueOf(payload.getOrDefault("hourPrice", "0"))));

        Object coinAmountVal = payload.getOrDefault("coinAmount", payload.getOrDefault("coinAmountText", 0));
        product.setCoinAmount(Long.parseLong(String.valueOf(coinAmountVal)));

        product.setEquipmentLevelText(String.valueOf(payload.getOrDefault("equipmentLevelText", "")));
        product.setWarehouseValueText(String.valueOf(payload.getOrDefault("warehouseValueText", "")));
        product.setRatioText(String.valueOf(payload.getOrDefault("ratioText", "")));
        product.setInsuranceBoxText(String.valueOf(payload.getOrDefault("insuranceBoxText", "")));
        product.setStaminaText(String.valueOf(payload.getOrDefault("staminaText", "")));
        product.setWeightText(String.valueOf(payload.getOrDefault("weightText", "")));
        product.setRankText(String.valueOf(payload.getOrDefault("rankText", "")));
        product.setLoginRegion(String.valueOf(payload.getOrDefault("loginRegion", "")));
        product.setWeaponSkinText(String.valueOf(payload.getOrDefault("weaponSkinText", "")));
        product.setCharacterSkinText(String.valueOf(payload.getOrDefault("characterSkinText", "")));
        product.setCoverImageUrl(String.valueOf(payload.getOrDefault("coverImageUrl", "")));
        product.setStatus(String.valueOf(payload.getOrDefault("status", "AVAILABLE")));
        product.setDescription(String.valueOf(payload.getOrDefault("description", "")));
        return product;
    }

    private Map<String, Object> toView(RentalProduct product) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", product.getId());
        item.put("name", product.getName());
        item.put("category", product.getCategory());
        item.put("tagText", product.getTagText());
        item.put("hourPrice", product.getHourPrice());
        item.put("coinAmount", product.getCoinAmount());
        item.put("equipmentLevelText", product.getEquipmentLevelText());
        item.put("warehouseValueText", product.getWarehouseValueText());
        item.put("ratioText", product.getRatioText());
        item.put("insuranceBoxText", product.getInsuranceBoxText());
        item.put("staminaText", product.getStaminaText());
        item.put("weightText", product.getWeightText());
        item.put("rankText", product.getRankText());
        item.put("loginRegion", product.getLoginRegion());
        item.put("weaponSkinText", product.getWeaponSkinText());
        item.put("characterSkinText", product.getCharacterSkinText());
        item.put("coverImageUrl", product.getCoverImageUrl());
        item.put("status", product.getStatus());
        item.put("description", product.getDescription());

        item.put("tag", product.getTagText());
        item.put("price", product.getHourPrice() + " / 小时");
        item.put("equipmentLevel", product.getEquipmentLevelText());
        item.put("warehouseValue", product.getWarehouseValueText());
        item.put("ratio", product.getRatioText());
        item.put("insuranceBox", product.getInsuranceBoxText());
        item.put("stamina", product.getStaminaText());
        item.put("weight", product.getWeightText());
        item.put("rank", product.getRankText());
        item.put("isHot", "AVAILABLE".equals(product.getStatus())
                && product.getHourPrice() != null
                && product.getHourPrice().compareTo(new BigDecimal("15")) >= 0);
        return item;
    }
}
