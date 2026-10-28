package com.smartshelf.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SmartDiscountDTO {
    private Long productId;
    private String productName;
    private String sku;
    private String categoryName;
    private Integer currentStock;
    private BigDecimal currentPrice;
    private LocalDate expiryDate;
    private Long daysUntilExpiry;
    private Integer recommendedDiscountPercent;
    private BigDecimal discountedPrice;
    private String reason;
    private String potentialLossPrevented;

    public SmartDiscountDTO() {}

    public SmartDiscountDTO(Long productId, String productName, String sku, String categoryName, Integer currentStock, BigDecimal currentPrice, LocalDate expiryDate, Long daysUntilExpiry, Integer recommendedDiscountPercent, BigDecimal discountedPrice, String reason, String potentialLossPrevented) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.categoryName = categoryName;
        this.currentStock = currentStock;
        this.currentPrice = currentPrice;
        this.expiryDate = expiryDate;
        this.daysUntilExpiry = daysUntilExpiry;
        this.recommendedDiscountPercent = recommendedDiscountPercent;
        this.discountedPrice = discountedPrice;
        this.reason = reason;
        this.potentialLossPrevented = potentialLossPrevented;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long productId;
        private String productName;
        private String sku;
        private String categoryName;
        private Integer currentStock;
        private BigDecimal currentPrice;
        private LocalDate expiryDate;
        private Long daysUntilExpiry;
        private Integer recommendedDiscountPercent;
        private BigDecimal discountedPrice;
        private String reason;
        private String potentialLossPrevented;

        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder currentStock(Integer currentStock) { this.currentStock = currentStock; return this; }
        public Builder currentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; return this; }
        public Builder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }
        public Builder daysUntilExpiry(Long daysUntilExpiry) { this.daysUntilExpiry = daysUntilExpiry; return this; }
        public Builder recommendedDiscountPercent(Integer recommendedDiscountPercent) { this.recommendedDiscountPercent = recommendedDiscountPercent; return this; }
        public Builder discountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder potentialLossPrevented(String potentialLossPrevented) { this.potentialLossPrevented = potentialLossPrevented; return this; }

        public SmartDiscountDTO build() {
            return new SmartDiscountDTO(productId, productName, sku, categoryName, currentStock, currentPrice, expiryDate, daysUntilExpiry, recommendedDiscountPercent, discountedPrice, reason, potentialLossPrevented);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Long getDaysUntilExpiry() { return daysUntilExpiry; }
    public void setDaysUntilExpiry(Long daysUntilExpiry) { this.daysUntilExpiry = daysUntilExpiry; }

    public Integer getRecommendedDiscountPercent() { return recommendedDiscountPercent; }
    public void setRecommendedDiscountPercent(Integer recommendedDiscountPercent) { this.recommendedDiscountPercent = recommendedDiscountPercent; }

    public BigDecimal getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(BigDecimal discountedPrice) { this.discountedPrice = discountedPrice; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getPotentialLossPrevented() { return potentialLossPrevented; }
    public void setPotentialLossPrevented(String potentialLossPrevented) { this.potentialLossPrevented = potentialLossPrevented; }
}
