package com.smartshelf.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class OrderItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal customPrice;
    private BigDecimal discount;

    public OrderItemRequest() {}

    public OrderItemRequest(Long productId, Integer quantity, BigDecimal customPrice, BigDecimal discount) {
        this.productId = productId;
        this.quantity = quantity;
        this.customPrice = customPrice;
        this.discount = discount;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long productId;
        private Integer quantity;
        private BigDecimal customPrice;
        private BigDecimal discount;

        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder customPrice(BigDecimal customPrice) { this.customPrice = customPrice; return this; }
        public Builder discount(BigDecimal discount) { this.discount = discount; return this; }

        public OrderItemRequest build() {
            return new OrderItemRequest(productId, quantity, customPrice, discount);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getCustomPrice() { return customPrice; }
    public void setCustomPrice(BigDecimal customPrice) { this.customPrice = customPrice; }

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
}
