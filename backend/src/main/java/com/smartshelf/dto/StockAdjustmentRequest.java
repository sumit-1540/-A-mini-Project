package com.smartshelf.dto;

import com.smartshelf.models.TransactionType;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity change is required")
    private Integer quantityChange;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    private String reason;

    public StockAdjustmentRequest() {}

    public StockAdjustmentRequest(Long productId, Integer quantityChange, TransactionType type, String reason) {
        this.productId = productId;
        this.quantityChange = quantityChange;
        this.type = type;
        this.reason = reason;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long productId;
        private Integer quantityChange;
        private TransactionType type;
        private String reason;

        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder quantityChange(Integer quantityChange) { this.quantityChange = quantityChange; return this; }
        public Builder type(TransactionType type) { this.type = type; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }

        public StockAdjustmentRequest build() {
            return new StockAdjustmentRequest(productId, quantityChange, type, reason);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantityChange() { return quantityChange; }
    public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
