package com.smartshelf.dto;

import com.smartshelf.models.TransactionType;

import java.time.LocalDateTime;

public class InventoryTransactionResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private TransactionType type;
    private Integer quantityChange;
    private Integer previousStock;
    private Integer newStock;
    private String reason;
    private String referenceId;
    private String performedBy;
    private LocalDateTime createdAt;

    public InventoryTransactionResponse() {}

    public InventoryTransactionResponse(Long id, Long productId, String productName, String sku, TransactionType type, Integer quantityChange, Integer previousStock, Integer newStock, String reason, String referenceId, String performedBy, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.type = type;
        this.quantityChange = quantityChange;
        this.previousStock = previousStock;
        this.newStock = newStock;
        this.reason = reason;
        this.referenceId = referenceId;
        this.performedBy = performedBy;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private TransactionType type;
        private Integer quantityChange;
        private Integer previousStock;
        private Integer newStock;
        private String reason;
        private String referenceId;
        private String performedBy;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder type(TransactionType type) { this.type = type; return this; }
        public Builder quantityChange(Integer quantityChange) { this.quantityChange = quantityChange; return this; }
        public Builder previousStock(Integer previousStock) { this.previousStock = previousStock; return this; }
        public Builder newStock(Integer newStock) { this.newStock = newStock; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public Builder performedBy(String performedBy) { this.performedBy = performedBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public InventoryTransactionResponse build() {
            return new InventoryTransactionResponse(id, productId, productName, sku, type, quantityChange, previousStock, newStock, reason, referenceId, performedBy, createdAt);
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

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public Integer getQuantityChange() { return quantityChange; }
    public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }

    public Integer getPreviousStock() { return previousStock; }
    public void setPreviousStock(Integer previousStock) { this.previousStock = previousStock; }

    public Integer getNewStock() { return newStock; }
    public void setNewStock(Integer newStock) { this.newStock = newStock; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
