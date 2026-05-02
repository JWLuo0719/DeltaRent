package com.jwluo0719.deltatrade.controller;

import com.jwluo0719.deltatrade.domain.RentalProduct;
import com.jwluo0719.deltatrade.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RentalController.class)
@AutoConfigureMockMvc(addFilters = false)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void list_shouldReturnProducts() throws Exception {
        RentalProduct product = new RentalProduct();
        product.setId(1001L);
        product.setName("Test Product");
        product.setCategory("premium");
        product.setTagText("tag");
        product.setHourPrice(new BigDecimal("18.00"));
        product.setCoinAmountText("1000 coins");
        product.setEquipmentLevelText("Advanced");
        product.setWarehouseValueText("High");
        product.setStatus("AVAILABLE");
        when(productService.listForBrowse(null, null, null, null)).thenReturn(List.of(product));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Test Product"))
                .andExpect(jsonPath("$.data[0].tagText").value("tag"))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"));
    }

    @Test
    void detail_shouldReturnProduct_whenFound() throws Exception {
        RentalProduct product = new RentalProduct();
        product.setId(1001L);
        product.setName("Test Product");
        product.setHourPrice(new BigDecimal("18.00"));
        product.setStatus("AVAILABLE");
        when(productService.getById(1001L)).thenReturn(product);

        mockMvc.perform(get("/api/rentals/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test Product"));
    }

    @Test
    void detail_shouldReturnFail_whenNotFound() throws Exception {
        when(productService.getById(9999L)).thenReturn(null);

        mockMvc.perform(get("/api/rentals/9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
