package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final RentalProductMapper productMapper;

    public ProductService(RentalProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public List<RentalProduct> listAll() {
        return productMapper.findAll();
    }

    public List<RentalProduct> listForBrowse(String keyword, String status, String tags, String sortBy) {
        List<RentalProduct> products = productMapper.findAll();

        if (keyword != null && !keyword.isBlank()) {
            String normalized = keyword.trim().toLowerCase(Locale.ROOT);
            products = products.stream()
                    .filter(product -> containsIgnoreCase(product.getName(), normalized)
                            || containsIgnoreCase(product.getCategory(), normalized)
                            || containsIgnoreCase(product.getTagText(), normalized)
                            || containsIgnoreCase(product.getEquipmentLevelText(), normalized))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isBlank()) {
            products = products.stream()
                    .filter(product -> status.equalsIgnoreCase(product.getStatus()))
                    .collect(Collectors.toList());
        }

        if (tags != null && !tags.isBlank()) {
            List<String> expectedTags = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isBlank())
                    .map(tag -> tag.toLowerCase(Locale.ROOT))
                    .toList();

            if (!expectedTags.isEmpty()) {
                products = products.stream()
                        .filter(product -> {
                            String currentTags = product.getTagText() == null ? "" : product.getTagText().toLowerCase(Locale.ROOT);
                            return expectedTags.stream().allMatch(currentTags::contains);
                        })
                        .collect(Collectors.toList());
            }
        }

        if ("price_asc".equalsIgnoreCase(sortBy)) {
            products = products.stream()
                    .sorted((left, right) -> left.getHourPrice().compareTo(right.getHourPrice()))
                    .collect(Collectors.toList());
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            products = products.stream()
                    .sorted((left, right) -> right.getHourPrice().compareTo(left.getHourPrice()))
                    .collect(Collectors.toList());
        }

        return products;
    }

    public RentalProduct getById(Long id) {
        return productMapper.findById(id);
    }

    public long countAvailable() {
        return productMapper.countAvailable();
    }

    public RentalProduct create(RentalProduct product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("鍟嗗搧鍚嶇О涓嶈兘涓虹┖");
        }
        if (product.getHourPrice() == null || product.getHourPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("浠锋牸蹇呴』澶т簬 0");
        }
        if (product.getStatus() == null) {
            product.setStatus("AVAILABLE");
        }
        productMapper.insert(product);
        return product;
    }

    public void update(RentalProduct product) {
        RentalProduct exist = productMapper.findById(product.getId());
        if (exist == null) throw new IllegalArgumentException("鍟嗗搧涓嶅瓨鍦?");
        productMapper.update(product);
    }

    public void delete(Long id) {
        productMapper.deleteById(id);
    }

    public void updateStatus(Long id, String status) {
        productMapper.updateStatus(id, status);
    }

    private boolean containsIgnoreCase(String raw, String expectedLowerCase) {
        return raw != null && raw.toLowerCase(Locale.ROOT).contains(expectedLowerCase);
    }
}
