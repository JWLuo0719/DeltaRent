package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.common.ApiResponse;
import com.jwluo0719.deltatrade.domain.Notice;
import com.jwluo0719.deltatrade.service.NoticeService;
import com.jwluo0719.deltatrade.service.OrderService;
import com.jwluo0719.deltatrade.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 门户首页控制器 — 聚合展示统计数据、功能模块和公告。
 */
@RestController
@RequestMapping("/api/portal")
public class PortalController {

    private final ProductService productService;
    private final OrderService orderService;
    private final NoticeService noticeService;

    public PortalController(ProductService productService, OrderService orderService, NoticeService noticeService) {
        this.productService = productService;
        this.orderService = orderService;
        this.noticeService = noticeService;
    }

    /** 门户摘要 — 汇总在线账号数、订单数、公告列表供首页渲染 */
    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("heroTitle", "三角洲行动账号租赁平台");
        result.put("heroSubtitle", "严选高配账号、活动收藏号和低价体验号，支持在线下单、客服确认、订单跟踪与售后申诉。");

        List<Map<String, String>> metrics = Arrays.asList(
                metric("在线可租账号", String.valueOf(productService.countAvailable())),
                metric("累计租赁订单", String.valueOf(orderService.countAll())),
                metric("当前服务状态", "稳定运行")
        );
        result.put("metrics", metrics);

        result.put("modules", Arrays.asList(
                "用户认证", "账号展示", "账号租赁", "订单中心", "售后申诉", "后台审核", "公告管理", "数据看板"
        ));

        List<Map<String, Object>> notices = new ArrayList<>();
        for (Notice n : noticeService.listPublished()) {
            notices.add(notice(n.getId(), n.getTitle(), n.getContent()));
        }
        result.put("notices", notices);
        return ApiResponse.success(result);
    }

    /** 组装单个指标 {label, value} */
    private Map<String, String> metric(String label, String value) {
        Map<String, String> item = new LinkedHashMap<>();
        item.put("label", label);
        item.put("value", value);
        return item;
    }

    /** 组装公告条目 {id, title, content} */
    private Map<String, Object> notice(long id, String title, String content) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("title", title);
        item.put("content", content);
        return item;
    }
}
