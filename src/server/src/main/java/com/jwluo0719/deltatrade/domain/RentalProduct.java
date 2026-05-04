package com.jwluo0719.deltatrade.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalProduct {

    private Long id;
    private String name;
    private String category;
    private String tagText;
    private BigDecimal hourPrice;
    private Long coinAmount;
    private String equipmentLevelText;
    private String warehouseValueText;
    private String status;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTagText() {
        return tagText;
    }

    public void setTagText(String tagText) {
        this.tagText = tagText;
    }

    public BigDecimal getHourPrice() {
        return hourPrice;
    }

    public void setHourPrice(BigDecimal hourPrice) {
        this.hourPrice = hourPrice;
    }

    public Long getCoinAmount() {
        return coinAmount;
    }

    public void setCoinAmount(Long coinAmount) {
        this.coinAmount = coinAmount;
    }

    public String getEquipmentLevelText() {
        return equipmentLevelText;
    }

    public void setEquipmentLevelText(String equipmentLevelText) {
        this.equipmentLevelText = equipmentLevelText;
    }

    public String getWarehouseValueText() {
        return warehouseValueText;
    }

    public void setWarehouseValueText(String warehouseValueText) {
        this.warehouseValueText = warehouseValueText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
