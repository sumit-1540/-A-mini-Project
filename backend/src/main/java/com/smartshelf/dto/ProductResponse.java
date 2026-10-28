package com.smartshelf.dto;

import com.smartshelf.models.InventoryStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String barcode;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String unit;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private Integer currentStock;
    private Integer minimumStock;
    private InventoryStatus stockStatus;
    private LocalDate expiryDate;
    private Long daysUntilExpiry;
    private Long supplierId;
    private String supplierName;
    private String description;
    private String imageUrl;
    private String aisle;
    private String shelf;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse() {}

    public ProductResponse(Long id, String name, String sku, String barcode, Long categoryId, String categoryName, String brand, String unit, BigDecimal purchasePrice, BigDecimal sellingPrice, Integer currentStock, Integer minimumStock, InventoryStatus stockStatus, LocalDate expiryDate, Long daysUntilExpiry, Long supplierId, String supplierName, String description, String imageUrl, String aisle, String shelf, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brand = brand;
        this.unit = unit;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.stockStatus = stockStatus;
        this.expiryDate = expiryDate;
        this.daysUntilExpiry = daysUntilExpiry;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.aisle = aisle;
        this.shelf = shelf;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String sku;
        private String barcode;
        private Long categoryId;
        private String categoryName;
        private String brand;
        private String unit;
        private BigDecimal purchasePrice;
        private BigDecimal sellingPrice;
        private Integer currentStock;
        private Integer minimumStock;
        private InventoryStatus stockStatus;
        private LocalDate expiryDate;
        private Long daysUntilExpiry;
        private Long supplierId;
        private String supplierName;
        private String description;
        private String imageUrl;
        private String aisle;
        private String shelf;
        private boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder barcode(String barcode) { this.barcode = barcode; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder purchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; return this; }
        public Builder sellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; return this; }
        public Builder currentStock(Integer currentStock) { this.currentStock = currentStock; return this; }
        public Builder minimumStock(Integer minimumStock) { this.minimumStock = minimumStock; return this; }
        public Builder stockStatus(InventoryStatus stockStatus) { this.stockStatus = stockStatus; return this; }
        public Builder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }
        public Builder daysUntilExpiry(Long daysUntilExpiry) { this.daysUntilExpiry = daysUntilExpiry; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder aisle(String aisle) { this.aisle = aisle; return this; }
        public Builder shelf(String shelf) { this.shelf = shelf; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ProductResponse build() {
            return new ProductResponse(id, name, sku, barcode, categoryId, categoryName, brand, unit, purchasePrice, sellingPrice, currentStock, minimumStock, stockStatus, expiryDate, daysUntilExpiry, supplierId, supplierName, description, imageUrl, aisle, shelf, active, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

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

    public InventoryStatus getStockStatus() { return stockStatus; }
    public void setStockStatus(InventoryStatus stockStatus) { this.stockStatus = stockStatus; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Long getDaysUntilExpiry() { return daysUntilExpiry; }
    public void setDaysUntilExpiry(Long daysUntilExpiry) { this.daysUntilExpiry = daysUntilExpiry; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAisle() { return aisle; }
    public void setAisle(String aisle) { this.aisle = aisle; }

    public String getShelf() { return shelf; }
    public void setShelf(String shelf) { this.shelf = shelf; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
