package com.smartshelf.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions", indexes = {
        @Index(name = "idx_inv_tx_product", columnList = "product_id"),
        @Index(name = "idx_inv_tx_created", columnList = "createdAt")
})
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionType type;

    @Column(nullable = false)
    private Integer quantityChange;

    @Column(nullable = false)
    private Integer previousStock;

    @Column(nullable = false)
    private Integer newStock;

    @Column(length = 255)
    private String reason;

    @Column(length = 100)
    private String referenceId;

    @Column(length = 100)
    private String performedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public InventoryTransaction() {}

    public InventoryTransaction(Long id, Product product, TransactionType type, Integer quantityChange, Integer previousStock, Integer newStock, String reason, String referenceId, String performedBy, LocalDateTime createdAt) {
        this.id = id;
        this.product = product;
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
        private Product product;
        private TransactionType type;
        private Integer quantityChange;
        private Integer previousStock;
        private Integer newStock;
        private String reason;
        private String referenceId;
        private String performedBy;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder product(Product product) { this.product = product; return this; }
        public Builder type(TransactionType type) { this.type = type; return this; }
        public Builder quantityChange(Integer quantityChange) { this.quantityChange = quantityChange; return this; }
        public Builder previousStock(Integer previousStock) { this.previousStock = previousStock; return this; }
        public Builder newStock(Integer newStock) { this.newStock = newStock; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public Builder performedBy(String performedBy) { this.performedBy = performedBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public InventoryTransaction build() {
            return new InventoryTransaction(id, product, type, quantityChange, previousStock, newStock, reason, referenceId, performedBy, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

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
