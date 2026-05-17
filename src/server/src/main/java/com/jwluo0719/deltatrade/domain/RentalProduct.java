package com.jwluo0719.deltatrade.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalProduct {

    private Long id;
    private String name;
    private Long ownerId;
    private String category;
    private String tagText;
    private BigDecimal price;
    private BigDecimal deposit;
    private Integer rentalDays;
    private Long coinAmount;
    private String loginMethod;
    private String insuranceBox;
    private Integer staminaLevel;
    private Integer weightLevel;
    private String rankText;
    private BigDecimal kd;
    private Integer divingLevel;
    private String ratioText;
    private String loginRegion;
    private String tradeTimeText;
    private String knifeSkinText;
    private String weaponSkinText;
    private String characterSkinText;
    private String coverImageUrl;
    private String warehouseValueText;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTagText() { return tagText; }
    public void setTagText(String tagText) { this.tagText = tagText; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDeposit() { return deposit; }
    public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }

    public Integer getRentalDays() { return rentalDays; }
    public void setRentalDays(Integer rentalDays) { this.rentalDays = rentalDays; }

    public Long getCoinAmount() { return coinAmount; }
    public void setCoinAmount(Long coinAmount) { this.coinAmount = coinAmount; }

    public String getLoginMethod() { return loginMethod; }
    public void setLoginMethod(String loginMethod) { this.loginMethod = loginMethod; }

    public String getInsuranceBox() { return insuranceBox; }
    public void setInsuranceBox(String insuranceBox) { this.insuranceBox = insuranceBox; }

    public Integer getStaminaLevel() { return staminaLevel; }
    public void setStaminaLevel(Integer staminaLevel) { this.staminaLevel = staminaLevel; }

    public Integer getWeightLevel() { return weightLevel; }
    public void setWeightLevel(Integer weightLevel) { this.weightLevel = weightLevel; }

    public String getRankText() { return rankText; }
    public void setRankText(String rankText) { this.rankText = rankText; }

    public BigDecimal getKd() { return kd; }
    public void setKd(BigDecimal kd) { this.kd = kd; }

    public Integer getDivingLevel() { return divingLevel; }
    public void setDivingLevel(Integer divingLevel) { this.divingLevel = divingLevel; }

    public String getRatioText() { return ratioText; }
    public void setRatioText(String ratioText) { this.ratioText = ratioText; }

    public String getLoginRegion() { return loginRegion; }
    public void setLoginRegion(String loginRegion) { this.loginRegion = loginRegion; }

    public String getTradeTimeText() { return tradeTimeText; }
    public void setTradeTimeText(String tradeTimeText) { this.tradeTimeText = tradeTimeText; }

    public String getKnifeSkinText() { return knifeSkinText; }
    public void setKnifeSkinText(String knifeSkinText) { this.knifeSkinText = knifeSkinText; }

    public String getWeaponSkinText() { return weaponSkinText; }
    public void setWeaponSkinText(String weaponSkinText) { this.weaponSkinText = weaponSkinText; }

    public String getCharacterSkinText() { return characterSkinText; }
    public void setCharacterSkinText(String characterSkinText) { this.characterSkinText = characterSkinText; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getWarehouseValueText() { return warehouseValueText; }
    public void setWarehouseValueText(String warehouseValueText) { this.warehouseValueText = warehouseValueText; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
