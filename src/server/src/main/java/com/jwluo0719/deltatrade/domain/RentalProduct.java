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
    private String loginMethod;
    private String ratioText;
    private String insuranceBoxText;
    private String staminaText;
    private String weightText;
    private String rankText;
    private String kdText;
    private String divingLevelText;
    private String loginRegion;
    private String tradeTimeText;
    private Integer rentalDays;
    private BigDecimal deposit;
    private String knifeSkinText;
    private String weaponSkinText;
    private String characterSkinText;
    private String coverImageUrl;
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

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public String getRatioText() {
        return ratioText;
    }

    public void setRatioText(String ratioText) {
        this.ratioText = ratioText;
    }

    public String getInsuranceBoxText() {
        return insuranceBoxText;
    }

    public void setInsuranceBoxText(String insuranceBoxText) {
        this.insuranceBoxText = insuranceBoxText;
    }

    public String getStaminaText() {
        return staminaText;
    }

    public void setStaminaText(String staminaText) {
        this.staminaText = staminaText;
    }

    public String getWeightText() {
        return weightText;
    }

    public void setWeightText(String weightText) {
        this.weightText = weightText;
    }

    public String getRankText() {
        return rankText;
    }

    public void setRankText(String rankText) {
        this.rankText = rankText;
    }

    public String getKdText() {
        return kdText;
    }

    public void setKdText(String kdText) {
        this.kdText = kdText;
    }

    public String getDivingLevelText() {
        return divingLevelText;
    }

    public void setDivingLevelText(String divingLevelText) {
        this.divingLevelText = divingLevelText;
    }

    public String getLoginRegion() {
        return loginRegion;
    }

    public void setLoginRegion(String loginRegion) {
        this.loginRegion = loginRegion;
    }

    public String getTradeTimeText() {
        return tradeTimeText;
    }

    public void setTradeTimeText(String tradeTimeText) {
        this.tradeTimeText = tradeTimeText;
    }

    public Integer getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public String getKnifeSkinText() {
        return knifeSkinText;
    }

    public void setKnifeSkinText(String knifeSkinText) {
        this.knifeSkinText = knifeSkinText;
    }

    public String getWeaponSkinText() {
        return weaponSkinText;
    }

    public void setWeaponSkinText(String weaponSkinText) {
        this.weaponSkinText = weaponSkinText;
    }

    public String getCharacterSkinText() {
        return characterSkinText;
    }

    public void setCharacterSkinText(String characterSkinText) {
        this.characterSkinText = characterSkinText;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
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
