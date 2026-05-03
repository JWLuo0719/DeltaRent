package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.Notice;
import com.jwluo0719.deltatrade.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告业务服务 — 负责公告的发布、编辑和查询。
 */
@Service
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    /** 前台 — 查询已发布的公告 */
    public List<Notice> listPublished() {
        return noticeMapper.findPublished();
    }

    /** 管理员 — 查询全部公告 */
    public List<Notice> listAll() {
        return noticeMapper.findAll();
    }

    /** 按 ID 查询 */
    public Notice getById(Long id) {
        return noticeMapper.findById(id);
    }

    /** 管理员 — 新增公告 */
    public Notice create(String title, String content, Integer status) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("标题不能为空");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("内容不能为空");
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setStatus(status != null ? status : 1);
        noticeMapper.insert(notice);
        return notice;
    }

    /** 管理员 — 更新公告 */
    public void update(Notice notice) {
        if (noticeMapper.findById(notice.getId()) == null) {
            throw new IllegalArgumentException("公告不存在");
        }
        noticeMapper.update(notice);
    }

    /** 管理员 — 删除公告 */
    public void delete(Long id) {
        noticeMapper.deleteById(id);
    }
}
