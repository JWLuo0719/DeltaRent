package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.domain.Notice;
import com.jwluo0719.deltatrade.service.NoticeService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 公告控制器 — 前台查看 + 后台管理 CRUD。
 */
@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /** 前台 — 查看已发布公告 */
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> listPublished() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Notice n : noticeService.listPublished()) {
            result.add(toView(n));
        }
        return ApiResponse.success(result);
    }

    /** 管理员 — 查看全部公告（含未发布） */
    @GetMapping("/all")
    public ApiResponse<List<Map<String, Object>>> listAll() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Notice n : noticeService.listAll()) {
            result.add(toView(n));
        }
        return ApiResponse.success(result);
    }

    /** 查看单条公告 */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        Notice n = noticeService.getById(id);
        if (n == null) return ApiResponse.fail("公告不存在");
        return ApiResponse.success(toView(n));
    }

    /** 管理员 — 新增公告 */
    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> payload) {
        try {
            String title = String.valueOf(payload.getOrDefault("title", ""));
            String content = String.valueOf(payload.getOrDefault("content", ""));
            Integer status = toInt(payload.get("status"), 1);
            Notice notice = noticeService.create(title, content, status);
            return ApiResponse.success("创建成功", toView(notice));
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 管理员 — 更新公告 */
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Notice n = new Notice();
            n.setId(id);
            n.setTitle(String.valueOf(payload.getOrDefault("title", "")));
            n.setContent(String.valueOf(payload.getOrDefault("content", "")));
            n.setStatus(toInt(payload.get("status"), 1));
            noticeService.update(n);
            return ApiResponse.success("更新成功", null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 管理员 — 删除公告 */
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    private Map<String, Object> toView(Notice n) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", n.getId());
        item.put("title", n.getTitle());
        item.put("content", n.getContent());
        item.put("status", n.getStatus());
        return item;
    }

    private Integer toInt(Object value, Integer defaultVal) {
        if (value == null) return defaultVal;
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(String.valueOf(value));
    }
}
