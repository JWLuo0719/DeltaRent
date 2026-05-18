package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.AppealRecord;
import com.jwluo0719.deltatrade.mapper.AppealRecordMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 申诉业务服务 — 负责售后申诉的提交、查询和处理。
 */
@Service
public class AppealService {

    private final AppealRecordMapper appealMapper;

    public AppealService(AppealRecordMapper appealMapper) {
        this.appealMapper = appealMapper;
    }

    /** 用户 — 提交申诉 */
    public AppealRecord submit(Long userId, String orderType, Long orderId, String content, String reason) {
        if (content == null || content.isBlank()) throw new IllegalArgumentException("申诉内容不能为空");
        AppealRecord record = new AppealRecord();
        record.setUserId(userId);
        record.setOrderType(orderType != null ? orderType : "RENTAL");
        record.setOrderId(orderId);
        record.setContent(content);
        record.setReason(reason != null ? reason : "OTHER");
        record.setStatus("PENDING");
        appealMapper.insert(record);
        return record;
    }

    /** 用户 — 查看自己的申诉 */
    public List<AppealRecord> listByUser(Long userId) {
        return appealMapper.findByUserId(userId);
    }

    /** 管理员 — 查看全部申诉 */
    public List<AppealRecord> listAll() {
        return appealMapper.findAll();
    }

    /** 管理员 — 查看全部申诉（带详情） */
    public List<Map<String, Object>> listAllWithDetails() {
        return appealMapper.findAllWithDetails();
    }

    /** 管理员 — 处理申诉（通过/驳回），含退款和赔偿 */
    public void handle(Long id, String status, Long handlerId, String handlerRemark,
                       BigDecimal refundAmount, String compensation) {
        if (!"RESOLVED".equals(status) && !"REJECTED".equals(status)) {
            throw new IllegalArgumentException("处理状态只能为 RESOLVED 或 REJECTED");
        }
        AppealRecord exist = appealMapper.findById(id);
        if (exist == null) throw new IllegalArgumentException("申诉不存在");
        appealMapper.updateStatus(id, status, handlerId, handlerRemark, refundAmount, compensation, LocalDateTime.now());
    }
}
