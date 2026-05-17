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
        product.setPrice(new BigDecimal(String.valueOf(payload.getOrDefault("price", payload.getOrDefault("hourPrice", "0")))));
        product.setDeposit(new BigDecimal(String.valueOf(parseInteger(payload.getOrDefault("deposit", 0)))));

        Object coinAmountVal = payload.getOrDefault("coinAmount", 0);
        product.setCoinAmount(Long.parseLong(String.valueOf(coinAmountVal)));

        Object rentalDaysVal = payload.getOrDefault("rentalDays", 0);
        product.setRentalDays(Integer.parseInt(String.valueOf(rentalDaysVal)));

        product.setWarehouseValueText(String.valueOf(payload.getOrDefault("warehouseValueText", "")));
        product.setLoginMethod(String.valueOf(payload.getOrDefault("loginMethod", "")));
        product.setRatioText(String.valueOf(payload.getOrDefault("ratioText", "")));
        product.setInsuranceBox(String.valueOf(payload.getOrDefault("insuranceBox", payload.getOrDefault("insuranceBoxText", ""))));

        Object staminaVal = payload.getOrDefault("staminaLevel", payload.getOrDefault("staminaText", ""));
        product.setStaminaLevel(parseLevel(String.valueOf(staminaVal)));

        Object weightVal = payload.getOrDefault("weightLevel", payload.getOrDefault("weightText", ""));
        product.setWeightLevel(parseLevel(String.valueOf(weightVal)));

        product.setRankText(String.valueOf(payload.getOrDefault("rankText", "")));

        Object kdVal = payload.getOrDefault("kd", payload.getOrDefault("kdText", ""));
        String kdStr = String.valueOf(kdVal).trim();
        product.setKd(kdStr.isEmpty() ? null : new BigDecimal(kdStr));

        Object divingVal = payload.getOrDefault("divingLevel", payload.getOrDefault("divingLevelText", ""));
        product.setDivingLevel(parseLevel(String.valueOf(divingVal)));

        product.setLoginRegion(String.valueOf(payload.getOrDefault("loginRegion", "")));
        product.setTradeTimeText(String.valueOf(payload.getOrDefault("tradeTimeText", "")));
        product.setKnifeSkinText(String.valueOf(payload.getOrDefault("knifeSkinText", "")));
        product.setWeaponSkinText(String.valueOf(payload.getOrDefault("weaponSkinText", "")));
        product.setCharacterSkinText(String.valueOf(payload.getOrDefault("characterSkinText", "")));
        product.setLevel(parseInteger(payload.get("level")));
        product.setHelmetCount(parseInteger(payload.get("helmetCount")));
        product.setArmorCount(parseInteger(payload.get("armorCount")));
        product.setAwmAmmoCount(parseInteger(payload.get("awmAmmoCount")));
        product.setNineGridTrialCardCount(parseInteger(payload.get("nineGridTrialCardCount")));
        product.setRecentBanRecord(String.valueOf(payload.getOrDefault("recentBanRecord", "")));
        product.setCoverImageUrl(String.valueOf(payload.getOrDefault("coverImageUrl", "")));
        product.setStatus(String.valueOf(payload.getOrDefault("status", "AVAILABLE")));
        product.setDescription(String.valueOf(payload.getOrDefault("description", "")));
        return product;
    }

    private Integer parseInteger(Object val) {
        if (val == null) return null;
        String raw = String.valueOf(val).trim();
        if (raw.isEmpty()) return null;
        return Integer.parseInt(raw);
    }

    private Integer parseLevel(String val) {
        if (val == null || val.isBlank()) return null;
        String digits = val.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return null;
        return Integer.parseInt(digits);
    }

    private Map<String, Object> toView(RentalProduct product) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", product.getId());
        item.put("name", product.getName());
        item.put("ownerId", product.getOwnerId());
        item.put("category", product.getCategory());
        item.put("tagText", product.getTagText());
        item.put("hourPrice", product.getPrice());
        item.put("price", product.getPrice());
        item.put("coinAmount", product.getCoinAmount());
        item.put("deposit", product.getDeposit());
        item.put("rentalDays", product.getRentalDays());
        item.put("warehouseValueText", product.getWarehouseValueText());
        item.put("loginMethod", product.getLoginMethod());
        item.put("ratioText", product.getRatioText());
        item.put("insuranceBoxText", product.getInsuranceBox());
        item.put("insuranceBox", product.getInsuranceBox());
        String staminaStr = product.getStaminaLevel() != null ? product.getStaminaLevel() + "级" : "";
        String weightStr = product.getWeightLevel() != null ? product.getWeightLevel() + "级" : "";
        item.put("staminaText", staminaStr);
        item.put("weightText", weightStr);
        item.put("staminaLevel", product.getStaminaLevel());
        item.put("weightLevel", product.getWeightLevel());
        item.put("rankText", product.getRankText());
        item.put("kdText", product.getKd() != null ? product.getKd().toString() : "");
        item.put("kd", product.getKd());
        String divingStr = product.getDivingLevel() != null ? product.getDivingLevel() + "级" : "";
        item.put("divingLevelText", divingStr);
        item.put("divingLevel", product.getDivingLevel());
        item.put("loginRegion", product.getLoginRegion());
        item.put("tradeTimeText", product.getTradeTimeText());
        item.put("knifeSkinText", product.getKnifeSkinText());
        item.put("weaponSkinText", product.getWeaponSkinText());
        item.put("characterSkinText", product.getCharacterSkinText());
        item.put("level", product.getLevel());
        item.put("helmetCount", product.getHelmetCount());
        item.put("armorCount", product.getArmorCount());
        item.put("awmAmmoCount", product.getAwmAmmoCount());
        item.put("nineGridTrialCardCount", product.getNineGridTrialCardCount());
        item.put("recentBanRecord", product.getRecentBanRecord());
        item.put("coverImageUrl", product.getCoverImageUrl());
        item.put("status", product.getStatus());
        item.put("description", product.getDescription());
        item.put("tag", product.getTagText());
        item.put("rank", product.getRankText());
        item.put("isHot", "AVAILABLE".equals(product.getStatus())
                && product.getPrice() != null
                && product.getPrice().compareTo(new BigDecimal("1000")) >= 0);
        return item;
    }
}
