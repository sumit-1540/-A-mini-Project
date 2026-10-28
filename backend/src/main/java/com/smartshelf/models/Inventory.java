package com.smartshelf.models;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    private Integer currentStock = 0;

    @Column(nullable = false)
    private Integer minimumStock = 5;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryStatus status = InventoryStatus.HEALTHY;

    @Column(length = 50)
    private String aisle;

    @Column(length = 50)
    private String shelf;

    private LocalDateTime lastRestockedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Inventory() {}

    public Inventory(Long id, Product product, Integer currentStock, Integer minimumStock, InventoryStatus status, String aisle, String shelf, LocalDateTime lastRestockedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.product = product;
        this.currentStock = (currentStock != null) ? currentStock : 0;
        this.minimumStock = (minimumStock != null) ? minimumStock : 5;
        this.status = (status != null) ? status : InventoryStatus.HEALTHY;
        this.aisle = aisle;
        this.shelf = shelf;
        this.lastRestockedAt = lastRestockedAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Product product;
        private Integer currentStock = 0;
        private Integer minimumStock = 5;
        private InventoryStatus status = InventoryStatus.HEALTHY;
        private String aisle;
        private String shelf;
        private LocalDateTime lastRestockedAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder product(Product product) { this.product = product; return this; }
        public Builder currentStock(Integer currentStock) { this.currentStock = currentStock; return this; }
        public Builder minimumStock(Integer minimumStock) { this.minimumStock = minimumStock; return this; }
        public Builder status(InventoryStatus status) { this.status = status; return this; }
        public Builder aisle(String aisle) { this.aisle = aisle; return this; }
        public Builder shelf(String shelf) { this.shelf = shelf; return this; }
        public Builder lastRestockedAt(LocalDateTime lastRestockedAt) { this.lastRestockedAt = lastRestockedAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Inventory build() {
            return new Inventory(id, product, currentStock, minimumStock, status, aisle, shelf, lastRestockedAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Integer minimumStock) { this.minimumStock = minimumStock; }

    public InventoryStatus getStatus() { return status; }
    public void setStatus(InventoryStatus status) { this.status = status; }

    public String getAisle() { return aisle; }
    public void setAisle(String aisle) { this.aisle = aisle; }

    public String getShelf() { return shelf; }
    public void setShelf(String shelf) { this.shelf = shelf; }

    public LocalDateTime getLastRestockedAt() { return lastRestockedAt; }
    public void setLastRestockedAt(LocalDateTime lastRestockedAt) { this.lastRestockedAt = lastRestockedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
