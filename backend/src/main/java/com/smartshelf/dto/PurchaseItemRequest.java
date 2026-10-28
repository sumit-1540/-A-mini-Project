package com.smartshelf.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PurchaseItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.00", message = "Unit cost cannot be negative")
    private BigDecimal unitCost;

    public PurchaseItemRequest() {}

    public PurchaseItemRequest(Long productId, Integer quantity, BigDecimal unitCost) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long productId;
        private Integer quantity;
        private BigDecimal unitCost;

        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder unitCost(BigDecimal unitCost) { this.unitCost = unitCost; return this; }

        public PurchaseItemRequest build() {
            return new PurchaseItemRequest(productId, quantity, unitCost);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
}
