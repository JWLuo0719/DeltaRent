package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 租赁商品控制器 — 前台展示 + 后台管理 CRUD。
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final ProductService productService;

    public RentalController(ProductService productService) {
        this.productService = productService;
    }

    /** 前台 — 列出全部商品 */
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (RentalProduct p : productService.listAll()) {
            result.add(toView(p));
        }
        return ApiResponse.success(result);
    }

    /** 查看单个商品详情 */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        RentalProduct p = productService.getById(id);
        if (p == null) return ApiResponse.fail("商品不存在");
        return ApiResponse.success(toView(p));
    }

    /** 管理员 — 新增商品 */
    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> payload) {
        try {
            RentalProduct p = new RentalProduct();
            p.setName(String.valueOf(payload.getOrDefault("name", "")));
            p.setCategory(String.valueOf(payload.getOrDefault("category", "")));
            p.setTagText(String.valueOf(payload.getOrDefault("tagText", "")));
            p.setHourPrice(new BigDecimal(String.valueOf(payload.getOrDefault("hourPrice", "0"))));
            p.setCoinAmountText(String.valueOf(payload.getOrDefault("coinAmountText", "")));
            p.setEquipmentLevelText(String.valueOf(payload.getOrDefault("equipmentLevelText", "")));
            p.setWarehouseValueText(String.valueOf(payload.getOrDefault("warehouseValueText", "")));
            p.setStatus(String.valueOf(payload.getOrDefault("status", "AVAILABLE")));
            p.setDescription(String.valueOf(payload.getOrDefault("description", "")));
            productService.create(p);
            return ApiResponse.success("创建成功", toView(p));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 管理员 — 更新商品 */
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            RentalProduct p = new RentalProduct();
            p.setId(id);
            p.setName(String.valueOf(payload.getOrDefault("name", "")));
            p.setCategory(String.valueOf(payload.getOrDefault("category", "")));
            p.setTagText(String.valueOf(payload.getOrDefault("tagText", "")));
            p.setHourPrice(new BigDecimal(String.valueOf(payload.getOrDefault("hourPrice", "0"))));
            p.setCoinAmountText(String.valueOf(payload.getOrDefault("coinAmountText", "")));
            p.setEquipmentLevelText(String.valueOf(payload.getOrDefault("equipmentLevelText", "")));
            p.setWarehouseValueText(String.valueOf(payload.getOrDefault("warehouseValueText", "")));
            p.setStatus(String.valueOf(payload.getOrDefault("status", "AVAILABLE")));
            p.setDescription(String.valueOf(payload.getOrDefault("description", "")));
            productService.update(p);
            return ApiResponse.success("更新成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 管理员 — 删除商品 */
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    /** 管理员 — 更改商品状态（上架/下架/维护） */
    @PutMapping("/{id}/status")
    public ApiResponse<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "AVAILABLE");
        productService.updateStatus(id, status);
        return ApiResponse.success("状态更新成功", null);
    }

    /** 将领域对象转为前端视图格式 */
    private Map<String, Object> toView(RentalProduct p) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", p.getId());
        item.put("name", p.getName());
        item.put("tag", p.getTagText());
        item.put("price", p.getHourPrice() + " / 小时");
        item.put("status", p.getStatus());
        item.put("coinAmount", p.getCoinAmountText());
        item.put("equipmentLevel", p.getEquipmentLevelText());
        item.put("warehouseValue", p.getWarehouseValueText());
        return item;
    }
}
