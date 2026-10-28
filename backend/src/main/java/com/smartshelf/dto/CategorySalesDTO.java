package com.smartshelf.dto;

import java.math.BigDecimal;

public class CategorySalesDTO {
    private String categoryName;
    private BigDecimal totalSales;
    private Long totalQuantitySold;
    private Double percentageShare;

    public CategorySalesDTO() {}

    public CategorySalesDTO(String categoryName, BigDecimal totalSales, Long totalQuantitySold, Double percentageShare) {
        this.categoryName = categoryName;
        this.totalSales = totalSales;
        this.totalQuantitySold = totalQuantitySold;
        this.percentageShare = percentageShare;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String categoryName;
        private BigDecimal totalSales;
        private Long totalQuantitySold;
        private Double percentageShare;

        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder totalSales(BigDecimal totalSales) { this.totalSales = totalSales; return this; }
        public Builder totalQuantitySold(Long totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; return this; }
        public Builder percentageShare(Double percentageShare) { this.percentageShare = percentageShare; return this; }

        public CategorySalesDTO build() {
            return new CategorySalesDTO(categoryName, totalSales, totalQuantitySold, percentageShare);
        }
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }

    public Long getTotalQuantitySold() { return totalQuantitySold; }
    public void setTotalQuantitySold(Long totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; }

    public Double getPercentageShare() { return percentageShare; }
    public void setPercentageShare(Double percentageShare) { this.percentageShare = percentageShare; }
}
