package com.smartshelf.dto;

import com.smartshelf.models.InventoryStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InventoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private String barcode;
    private String categoryName;
    private String unit;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Integer currentStock;
    private Integer minimumStock;
    private InventoryStatus status;
    private String aisle;
    private String shelf;
    private LocalDate expiryDate;
    private Long daysUntilExpiry;
    private String supplierName;
    private LocalDateTime lastRestockedAt;
    private LocalDateTime updatedAt;

    public InventoryResponse() {}

    public InventoryResponse(Long id, Long productId, String productName, String sku, String barcode, String categoryName, String unit, BigDecimal purchasePrice, BigDecimal sellingPrice, Integer currentStock, Integer minimumStock, InventoryStatus status, String aisle, String shelf, LocalDate expiryDate, Long daysUntilExpiry, String supplierName, LocalDateTime lastRestockedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.barcode = barcode;
        this.categoryName = categoryName;
        this.unit = unit;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.status = status;
        this.aisle = aisle;
        this.shelf = shelf;
        this.expiryDate = expiryDate;
        this.daysUntilExpiry = daysUntilExpiry;
        this.supplierName = supplierName;
        this.lastRestockedAt = lastRestockedAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long productId;
        private String productName;
        private String sku;
        private String barcode;
        private String categoryName;
        private String unit;
        private BigDecimal purchasePrice;
        private BigDecimal sellingPrice;
        private Integer currentStock;
        private Integer minimumStock;
        private InventoryStatus status;
        private String aisle;
        private String shelf;
        private LocalDate expiryDate;
        private Long daysUntilExpiry;
        private String supplierName;
        private LocalDateTime lastRestockedAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder barcode(String barcode) { this.barcode = barcode; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder purchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; return this; }
        public Builder sellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; return this; }
        public Builder currentStock(Integer currentStock) { this.currentStock = currentStock; return this; }
        public Builder minimumStock(Integer minimumStock) { this.minimumStock = minimumStock; return this; }
        public Builder status(InventoryStatus status) { this.status = status; return this; }
        public Builder aisle(String aisle) { this.aisle = aisle; return this; }
        public Builder shelf(String shelf) { this.shelf = shelf; return this; }
        public Builder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }
        public Builder daysUntilExpiry(Long daysUntilExpiry) { this.daysUntilExpiry = daysUntilExpiry; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder lastRestockedAt(LocalDateTime lastRestockedAt) { this.lastRestockedAt = lastRestockedAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public InventoryResponse build() {
            return new InventoryResponse(id, productId, productName, sku, barcode, categoryName, unit, purchasePrice, sellingPrice, currentStock, minimumStock, status, aisle, shelf, expiryDate, daysUntilExpiry, supplierName, lastRestockedAt, updatedAt);
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

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

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

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Long getDaysUntilExpiry() { return daysUntilExpiry; }
    public void setDaysUntilExpiry(Long daysUntilExpiry) { this.daysUntilExpiry = daysUntilExpiry; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public LocalDateTime getLastRestockedAt() { return lastRestockedAt; }
    public void setLastRestockedAt(LocalDateTime lastRestockedAt) { this.lastRestockedAt = lastRestockedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
