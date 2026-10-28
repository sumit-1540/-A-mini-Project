package com.smartshelf.dto;

import java.math.BigDecimal;

public class PurchaseItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;

    public PurchaseItemResponse() {}

    public PurchaseItemResponse(Long id, Long productId, String productName, String sku, Integer quantity, BigDecimal unitCost, BigDecimal totalCost) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private Integer quantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder unitCost(BigDecimal unitCost) { this.unitCost = unitCost; return this; }
        public Builder totalCost(BigDecimal totalCost) { this.totalCost = totalCost; return this; }

        public PurchaseItemResponse build() {
            return new PurchaseItemResponse(id, productId, productName, sku, quantity, unitCost, totalCost);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}
