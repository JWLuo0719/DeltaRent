package com.jwluo0719.deltatrade.domain;

/**
 * 申诉记录实体 — 对应 appeal_record 表，用户可对订单提出售后申诉，管理员可处理。
 */
public class AppealRecord {

    private Long id;
    private String orderType;   // 关联订单类型
    private Long orderId;       // 关联订单 ID
    private Long userId;        // 申诉人 ID
    private String content;     // 申诉内容
    private String status;      // PENDING=待处理, RESOLVED=已处理, REJECTED=已驳回

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
