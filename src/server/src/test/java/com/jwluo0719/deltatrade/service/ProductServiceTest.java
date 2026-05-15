package com.jwluo0719.deltatrade.service;

import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.mapper.RentalProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductServiceTest {

    private InMemoryRentalProductMapper productMapper;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productMapper = new InMemoryRentalProductMapper();
        productMapper.products.add(product(1001L, "高战满仓号 A01", "高配冲分", "满仓库", "六套毕业装", "AVAILABLE", "28.00"));
        productMapper.products.add(product(1002L, "活动收藏号 B02", "活动收藏", "稀有外观", "中高配作战装", "AVAILABLE", "18.00"));
        productMapper.products.add(product(1003L, "新手体验号 C03", "低价体验", "新手试用", "基础装备", "MAINTENANCE", "8.00"));
        productService = new ProductService(productMapper);
    }

    @Test
    void listForBrowseFiltersByKeywordStatusAndSortsByPrice() {
        Map<String, Object> result = productService.listForBrowse("号", null, null, null, "AVAILABLE", "price_desc", 1, 10);

        @SuppressWarnings("unchecked")
        List<RentalProduct> list = (List<RentalProduct>) result.get("list");

        assertEquals(2, result.get("total"));
        assertEquals(1001L, list.get(0).getId());
        assertEquals(1002L, list.get(1).getId());
    }

    @Test
    void listForBrowseFiltersByCategory() {
        Map<String, Object> result = productService.listForBrowse(null, null, "活动收藏", null, null, null, 1, 10);

        @SuppressWarnings("unchecked")
        List<RentalProduct> list = (List<RentalProduct>) result.get("list");

        assertEquals(1, result.get("total"));
        assertEquals(1002L, list.get(0).getId());
    }

    @Test
    void listForBrowseMatchesEquipmentLevel() {
        Map<String, Object> advanced = productService.listForBrowse(null, null, null, "Advanced", null, null, 1, 10);
        Map<String, Object> basic = productService.listForBrowse(null, null, null, "Basic", null, null, 1, 10);

        @SuppressWarnings("unchecked")
        List<RentalProduct> advancedList = (List<RentalProduct>) advanced.get("list");
        @SuppressWarnings("unchecked")
        List<RentalProduct> basicList = (List<RentalProduct>) basic.get("list");

        assertEquals(1, advanced.get("total"));
        assertEquals(1001L, advancedList.get(0).getId());
        assertEquals(1, basic.get("total"));
        assertEquals(1003L, basicList.get(0).getId());
    }

    @Test
    void createRejectsEmptyNameAndInvalidPrice() {
        RentalProduct emptyName = product(null, "", "premium", "", "Advanced equipment set", "AVAILABLE", "18.00");
        RentalProduct invalidPrice = product(null, "Invalid Account", "premium", "", "Advanced equipment set", "AVAILABLE", "0.00");

        assertEquals("账号名称不能为空", assertThrows(IllegalArgumentException.class, () -> productService.create(emptyName)).getMessage());
        assertEquals("价格必须大于 0", assertThrows(IllegalArgumentException.class, () -> productService.create(invalidPrice)).getMessage());
    }

    private RentalProduct product(Long id, String name, String category, String tags, String level, String status, String price) {
        RentalProduct product = new RentalProduct();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setTagText(tags);
        product.setHourPrice(new BigDecimal(price));
        product.setCoinAmount(1_000_000L);
        product.setEquipmentLevelText(level);
        product.setWarehouseValueText("Demo warehouse");
        product.setStatus(status);
        product.setDescription("Demo product");
        return product;
    }

    private static class InMemoryRentalProductMapper implements RentalProductMapper {

        private final List<RentalProduct> products = new ArrayList<>();
        private long nextId = 2000L;

        @Override
        public List<RentalProduct> findAll() {
            return products;
        }

        @Override
        public RentalProduct findById(Long id) {
            return products.stream().filter(product -> id.equals(product.getId())).findFirst().orElse(null);
        }

        @Override
        public long countAvailable() {
            return products.stream().filter(product -> "AVAILABLE".equals(product.getStatus())).count();
        }

        @Override
        public int insert(RentalProduct product) {
            product.setId(nextId++);
            products.add(product);
            return 1;
        }

        @Override
        public int update(RentalProduct product) {
            return 1;
        }

        @Override
        public int deleteById(Long id) {
            return products.removeIf(product -> id.equals(product.getId())) ? 1 : 0;
        }

        @Override
        public int updateStatus(Long id, String status) {
            RentalProduct product = findById(id);
            if (product == null) {
                return 0;
            }
            product.setStatus(status);
            return 1;
        }
    }
}
