package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.common.JwtUtil;
import com.jwluo0719.deltatrade.domain.AppealRecord;
import com.jwluo0719.deltatrade.service.AppealService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 申诉控制器 — 用户提交售后申诉 + 管理员处理。
 */
@RestController
@RequestMapping("/api/appeals")
public class AppealController {

    private final AppealService appealService;

    public AppealController(AppealService appealService) {
        this.appealService = appealService;
    }

    /** 用户 — 提交申诉 */
    @PostMapping
    public ApiResponse<Map<String, Object>> submit(@RequestBody Map<String, Object> payload,
                                                    @RequestHeader(value = "Authorization", required = false) String auth) {
        try {
            Long userId = extractUserId(auth);
            String orderType = String.valueOf(payload.getOrDefault("orderType", "RENTAL"));
            Long orderId = toLong(payload.get("orderId"), 0L);
            String content = String.valueOf(payload.getOrDefault("content", ""));
            AppealRecord record = appealService.submit(userId, orderType, orderId, content);
            return ApiResponse.success("申诉已提交", toView(record));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 用户 — 查看自己的申诉 */
    @GetMapping("/my")
    public ApiResponse<List<Map<String, Object>>> myAppeals(@RequestHeader(value = "Authorization", required = false) String auth) {
        Long userId = extractUserId(auth);
        List<Map<String, Object>> result = new ArrayList<>();
        for (AppealRecord r : appealService.listByUser(userId)) {
            result.add(toView(r));
        }
        return ApiResponse.success(result);
    }

    /** 管理员 — 查看全部申诉 */
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> listAll() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (AppealRecord r : appealService.listAll()) {
            result.add(toView(r));
        }
        return ApiResponse.success(result);
    }

    /** 管理员 — 处理申诉（通过/驳回） */
    @PutMapping("/{id}/handle")
    public ApiResponse<?> handle(@PathVariable Long id, @RequestBody Map<String, String> payload,
                                @RequestHeader(value = "Authorization", required = false) String auth) {
        if (!isAdmin(auth)) return ApiResponse.fail("无权操作");
        try {
            Long handlerId = extractUserId(auth);
            String status = payload.getOrDefault("status", "RESOLVED");
            String handlerRemark = payload.getOrDefault("handlerRemark", "");
            appealService.handle(id, status, handlerId, handlerRemark);
            return ApiResponse.success("处理完成", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 从 Authorization 头提取用户 ID */
    private Long extractUserId(String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            Long uid = JwtUtil.getUserId(auth.substring(7));
            if (uid != null) return uid;
        }
        return 1L; // 兼容 Mock
    }

    private boolean isAdmin(String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            String role = JwtUtil.getRole(auth.substring(7));
            return "ADMIN".equals(role) || "CS".equals(role);
        }
        return false;
    }

    private Long toLong(Object value, Long defaultVal) {
        if (value == null) return defaultVal;
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(String.valueOf(value));
    }

    private Map<String, Object> toView(AppealRecord r) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", r.getId());
        item.put("orderType", r.getOrderType());
        item.put("orderId", r.getOrderId());
        item.put("userId", r.getUserId());
        item.put("content", r.getContent());
        item.put("status", r.getStatus());
        item.put("handlerId", r.getHandlerId());
        item.put("handlerRemark", r.getHandlerRemark());
        item.put("handledAt", r.getHandledAt());
        item.put("updatedAt", r.getUpdatedAt());
        return item;
    }
}
