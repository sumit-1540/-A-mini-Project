package com.smartshelf.dto;

import java.math.BigDecimal;

public class RecommendationDTO {
    private Long productId;
    private String productName;
    private String sku;
    private String categoryName;
    private BigDecimal sellingPrice;
    private String reason;
    private Double confidenceScore;
    private String imageUrl;

    public RecommendationDTO() {}

    public RecommendationDTO(Long productId, String productName, String sku, String categoryName, BigDecimal sellingPrice, String reason, Double confidenceScore, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.categoryName = categoryName;
        this.sellingPrice = sellingPrice;
        this.reason = reason;
        this.confidenceScore = confidenceScore;
        this.imageUrl = imageUrl;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long productId;
        private String productName;
        private String sku;
        private String categoryName;
        private BigDecimal sellingPrice;
        private String reason;
        private Double confidenceScore;
        private String imageUrl;

        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder sellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder confidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public RecommendationDTO build() {
            return new RecommendationDTO(productId, productName, sku, categoryName, sellingPrice, reason, confidenceScore, imageUrl);
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

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
