package com.smartshelf.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    private String barcode;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String brand;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Purchase price is required")
    @Min(value = 0, message = "Purchase price must be non-negative")
    private BigDecimal purchasePrice;

    @NotNull(message = "Selling price is required")
    @Min(value = 0, message = "Selling price must be non-negative")
    private BigDecimal sellingPrice;

    @NotNull(message = "Initial stock is required")
    @Min(value = 0, message = "Initial stock must be non-negative")
    private Integer initialStock;

    @NotNull(message = "Minimum stock is required")
    @Min(value = 0, message = "Minimum stock must be non-negative")
    private Integer minimumStock;

    private LocalDate expiryDate;
    private Long supplierId;
    private String description;
    private String imageUrl;
    private String aisle;
    private String shelf;

    public ProductRequest() {}

    public ProductRequest(String name, String sku, String barcode, Long categoryId, String brand, String unit, BigDecimal purchasePrice, BigDecimal sellingPrice, Integer initialStock, Integer minimumStock, LocalDate expiryDate, Long supplierId, String description, String imageUrl, String aisle, String shelf) {
        this.name = name;
        this.sku = sku;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.brand = brand;
        this.unit = unit;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.initialStock = initialStock;
        this.minimumStock = minimumStock;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.aisle = aisle;
        this.shelf = shelf;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private String sku;
        private String barcode;
        private Long categoryId;
        private String brand;
        private String unit;
        private BigDecimal purchasePrice;
        private BigDecimal sellingPrice;
        private Integer initialStock;
        private Integer minimumStock;
        private LocalDate expiryDate;
        private Long supplierId;
        private String description;
        private String imageUrl;
        private String aisle;
        private String shelf;

        public Builder name(String name) { this.name = name; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder barcode(String barcode) { this.barcode = barcode; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder purchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; return this; }
        public Builder sellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; return this; }
        public Builder initialStock(Integer initialStock) { this.initialStock = initialStock; return this; }
        public Builder minimumStock(Integer minimumStock) { this.minimumStock = minimumStock; return this; }
        public Builder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder aisle(String aisle) { this.aisle = aisle; return this; }
        public Builder shelf(String shelf) { this.shelf = shelf; return this; }

        public ProductRequest build() {
            return new ProductRequest(name, sku, barcode, categoryId, brand, unit, purchasePrice, sellingPrice, initialStock, minimumStock, expiryDate, supplierId, description, imageUrl, aisle, shelf);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public Integer getInitialStock() { return initialStock; }
    public void setInitialStock(Integer initialStock) { this.initialStock = initialStock; }

    public Integer getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Integer minimumStock) { this.minimumStock = minimumStock; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAisle() { return aisle; }
    public void setAisle(String aisle) { this.aisle = aisle; }

    public String getShelf() { return shelf; }
    public void setShelf(String shelf) { this.shelf = shelf; }
}
