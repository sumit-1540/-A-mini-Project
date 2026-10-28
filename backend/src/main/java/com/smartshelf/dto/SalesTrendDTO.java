package com.smartshelf.dto;

import java.math.BigDecimal;

public class SalesTrendDTO {
    private String date;
    private BigDecimal sales;
    private Integer orderCount;

    public SalesTrendDTO() {}

    public SalesTrendDTO(String date, BigDecimal sales, Integer orderCount) {
        this.date = date;
        this.sales = sales;
        this.orderCount = orderCount;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String date;
        private BigDecimal sales;
        private Integer orderCount;

        public Builder date(String date) { this.date = date; return this; }
        public Builder sales(BigDecimal sales) { this.sales = sales; return this; }
        public Builder orderCount(Integer orderCount) { this.orderCount = orderCount; return this; }

        public SalesTrendDTO build() {
            return new SalesTrendDTO(date, sales, orderCount);
        }
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public BigDecimal getSales() { return sales; }
    public void setSales(BigDecimal sales) { this.sales = sales; }

    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
}
