package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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

    public Map<String, Object> listForBrowse(String keyword,
                                             String tags,
                                             String level,
                                             String status,
                                             String sortBy,
                                             Integer page,
                                             Integer pageSize) {
        List<RentalProduct> products = productMapper.findAll();
        List<String> allTags = collectAllTags(products);

        if (keyword != null && !keyword.isBlank()) {
            String normalized = keyword.trim().toLowerCase(Locale.ROOT);
            products = products.stream()
                    .filter(product -> containsIgnoreCase(product.getName(), normalized)
                            || containsIgnoreCase(product.getCategory(), normalized)
                            || containsIgnoreCase(product.getTagText(), normalized)
                            || containsIgnoreCase(product.getEquipmentLevelText(), normalized))
                    .collect(Collectors.toList());
        }

        if (level != null && !level.isBlank()) {
            products = products.stream()
                    .filter(product -> matchesLevel(product.getEquipmentLevelText(), level))
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

        int currentPage = page == null || page < 1 ? 1 : page;
        int currentPageSize = pageSize == null || pageSize < 1 ? 12 : pageSize;
        int total = products.size();
        int fromIndex = Math.min((currentPage - 1) * currentPageSize, total);
        int toIndex = Math.min(fromIndex + currentPageSize, total);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", products.subList(fromIndex, toIndex));
        result.put("total", total);
        result.put("allTags", allTags);
        return result;
    }

    public RentalProduct getById(Long id) {
        return productMapper.findById(id);
    }

    public long countAvailable() {
        return productMapper.countAvailable();
    }

    public RentalProduct create(RentalProduct product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("账号名称不能为空");
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

    public void update(RentalProduct product) {
        RentalProduct exist = productMapper.findById(product.getId());
        if (exist == null) throw new IllegalArgumentException("账号不存在");
        productMapper.update(product);
    }

    public void delete(Long id) {
        int affected = productMapper.deleteById(id);
        if (affected == 0) {
            throw new IllegalArgumentException("账号不存在");
        }
    }

    public void updateStatus(Long id, String status) {
        productMapper.updateStatus(id, status);
    }

    private boolean containsIgnoreCase(String raw, String expectedLowerCase) {
        return raw != null && raw.toLowerCase(Locale.ROOT).contains(expectedLowerCase);
    }

    private boolean matchesLevel(String equipmentLevelText, String level) {
        String normalizedLevel = level == null ? "" : level.trim();
        return switch (normalizedLevel) {
            case "Basic" -> containsAny(equipmentLevelText, "新手", "基础", "basic", "beginner");
            case "Mid" -> containsAny(equipmentLevelText, "进阶", "中阶", "mid", "medium");
            case "Advanced" -> containsAny(equipmentLevelText, "高阶", "毕业", "advanced");
            case "Full" -> containsAny(equipmentLevelText, "满配", "顶级", "毕业装", "六套", "full", "top");
            default -> true;
        };
    }

    private boolean containsAny(String raw, String... parts) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        String normalizedRaw = raw.toLowerCase(Locale.ROOT);
        return Arrays.stream(parts)
                .filter(Objects::nonNull)
                .map(part -> part.toLowerCase(Locale.ROOT))
                .anyMatch(normalizedRaw::contains);
    }

    private List<String> collectAllTags(List<RentalProduct> products) {
        return products.stream()
                .map(RentalProduct::getTagText)
                .filter(Objects::nonNull)
                .flatMap(tagText -> Arrays.stream(tagText.split(",")))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }
}
