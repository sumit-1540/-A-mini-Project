package com.smartshelf.dto;

import java.math.BigDecimal;

public class TopProductDTO {
    private Long productId;
    private String productName;
    private String categoryName;
    private Long unitsSold;
    private BigDecimal totalRevenue;
    private BigDecimal grossProfit;

    public TopProductDTO() {}

    public TopProductDTO(Long productId, String productName, String categoryName, Long unitsSold, BigDecimal totalRevenue, BigDecimal grossProfit) {
        this.productId = productId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.unitsSold = unitsSold;
        this.totalRevenue = totalRevenue;
        this.grossProfit = grossProfit;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long productId;
        private String productName;
        private String categoryName;
        private Long unitsSold;
        private BigDecimal totalRevenue;
        private BigDecimal grossProfit;

        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder unitsSold(Long unitsSold) { this.unitsSold = unitsSold; return this; }
        public Builder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public Builder grossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; return this; }

        public TopProductDTO build() {
            return new TopProductDTO(productId, productName, categoryName, unitsSold, totalRevenue, grossProfit);
        }
    }

    public Long getProductId() { return productId; }
    public void setId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getUnitsSold() { return unitsSold; }
    public void setUnitsSold(Long unitsSold) { this.unitsSold = unitsSold; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getGrossProfit() { return grossProfit; }
    public void setGrossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; }
}
