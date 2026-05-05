package com.jwluo0719.deltatrade.domain;

/**
 * 公告实体 — 对应 notice 表，用于首页公告展示和管理后台的公告管理。
 */
public class Notice {

    private Long id;
    private String title;
    private String content;
    private Integer status; // 1=发布, 0=下线

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
