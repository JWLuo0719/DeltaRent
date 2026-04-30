package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品业务服务 — 负责租赁账号的增删改查和上下架。
 */
@Service
public class ProductService {

    private final RentalProductMapper productMapper;

    public ProductService(RentalProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    /** 前台 — 查询全部商品列表 */
    public List<RentalProduct> listAll() {
        return productMapper.findAll();
    }

    /** 按 ID 查单个商品 */
    public RentalProduct getById(Long id) {
        return productMapper.findById(id);
    }

    /** 统计可租数量 */
    public long countAvailable() {
        return productMapper.countAvailable();
    }

    /** 管理员 — 新增商品 */
    public RentalProduct create(RentalProduct product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        if (product.getHourPrice() == null || product.getHourPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("价格必须大于 0");
        }
        if (product.getStatus() == null) {
            product.setStatus("AVAILABLE");
        }
        productMapper.insert(product);
        return product;
    }

    /** 管理员 — 更新商品信息 */
    public void update(RentalProduct product) {
        RentalProduct exist = productMapper.findById(product.getId());
        if (exist == null) throw new IllegalArgumentException("商品不存在");
        productMapper.update(product);
    }

    /** 管理员 — 删除商品 */
    public void delete(Long id) {
        productMapper.deleteById(id);
    }

    /** 管理员 — 上架/下架/维护 */
    public void updateStatus(Long id, String status) {
        productMapper.updateStatus(id, status);
    }
}
