package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/portal")
public class PortalController {

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("heroTitle", "三角洲行动账号租赁管理系统");
        result.put("heroSubtitle", "Spring Boot 后端已预留与前端一致的数据结构，后续可以直接替换 Mock 服务。");
        result.put("metrics", Arrays.asList(
            metric("在线可租账号", "18"),
            metric("今日新增订单", "12"),
            metric("订单完成率", "97.4%")
        ));
        result.put("modules", Arrays.asList("用户认证", "账号展示", "账号租赁", "订单中心", "售后申诉", "后台审核", "公告管理", "数据看板"));
        result.put("notices", Arrays.asList(
            notice(1, "课程演示提示", "当前返回的是后端占位数据，后续可替换为数据库查询。"),
            notice(2, "联调说明", "接口字段已与前端页面保持一致。")
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
