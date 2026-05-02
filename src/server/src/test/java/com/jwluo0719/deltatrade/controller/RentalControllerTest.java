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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalController.class)
@AutoConfigureMockMvc(addFilters = false)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void list_shouldReturnProducts() throws Exception {
        RentalProduct p = new RentalProduct();
        p.setId(1001L);
        p.setName("Test Product");
        p.setCategory("premium");
        p.setTagText("tag");
        p.setHourPrice(new BigDecimal("18.00"));
        p.setCoinAmountText("1000 coins");
        p.setEquipmentLevelText("Advanced");
        p.setWarehouseValueText("High");
        p.setStatus("AVAILABLE");
        when(productService.listAll()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Test Product"))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"));
    }

    @Test
    void detail_shouldReturnProduct_whenFound() throws Exception {
        RentalProduct p = new RentalProduct();
        p.setId(1001L);
        p.setName("Test Product");
        p.setHourPrice(new BigDecimal("18.00"));
        p.setStatus("AVAILABLE");
        when(productService.getById(1001L)).thenReturn(p);

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
