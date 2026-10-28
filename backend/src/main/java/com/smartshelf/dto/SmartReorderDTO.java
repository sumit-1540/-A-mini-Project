package com.smartshelf.dto;

import java.math.BigDecimal;

public class SmartReorderDTO {
    private Long productId;
    private String productName;
    private String sku;
    private Integer currentStock;
    private Integer minimumStock;
    private Integer predictedDemand;
    private Integer recommendedReorderQuantity;
    private Long supplierId;
    private String supplierName;
    private BigDecimal unitPurchasePrice;
    private BigDecimal estimatedCost;
    private String urgency;

    public SmartReorderDTO() {}

    public SmartReorderDTO(Long productId, String productName, String sku, Integer currentStock, Integer minimumStock, Integer predictedDemand, Integer recommendedReorderQuantity, Long supplierId, String supplierName, BigDecimal unitPurchasePrice, BigDecimal estimatedCost, String urgency) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.predictedDemand = predictedDemand;
        this.recommendedReorderQuantity = recommendedReorderQuantity;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.unitPurchasePrice = unitPurchasePrice;
        this.estimatedCost = estimatedCost;
        this.urgency = urgency;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long productId;
        private String productName;
        private String sku;
        private Integer currentStock;
        private Integer minimumStock;
        private Integer predictedDemand;
        private Integer recommendedReorderQuantity;
        private Long supplierId;
        private String supplierName;
        private BigDecimal unitPurchasePrice;
        private BigDecimal estimatedCost;
        private String urgency;

        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder currentStock(Integer currentStock) { this.currentStock = currentStock; return this; }
        public Builder minimumStock(Integer minimumStock) { this.minimumStock = minimumStock; return this; }
        public Builder predictedDemand(Integer predictedDemand) { this.predictedDemand = predictedDemand; return this; }
        public Builder recommendedReorderQuantity(Integer recommendedOrderQuantity) { this.recommendedReorderQuantity = recommendedOrderQuantity; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder unitPurchasePrice(BigDecimal unitPurchasePrice) { this.unitPurchasePrice = unitPurchasePrice; return this; }
        public Builder estimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; return this; }
        public Builder urgency(String urgency) { this.urgency = urgency; return this; }

        public SmartReorderDTO build() {
            return new SmartReorderDTO(productId, productName, sku, currentStock, minimumStock, predictedDemand, recommendedReorderQuantity, supplierId, supplierName, unitPurchasePrice, estimatedCost, urgency);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Integer minimumStock) { this.minimumStock = minimumStock; }

    public Integer getPredictedDemand() { return predictedDemand; }
    public void setPredictedDemand(Integer predictedDemand) { this.predictedDemand = predictedDemand; }

    public Integer getRecommendedReorderQuantity() { return recommendedReorderQuantity; }
    public void setRecommendedReorderQuantity(Integer recommendedReorderQuantity) { this.recommendedReorderQuantity = recommendedReorderQuantity; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public BigDecimal getUnitPurchasePrice() { return unitPurchasePrice; }
    public void setUnitPurchasePrice(BigDecimal unitPurchasePrice) { this.unitPurchasePrice = unitPurchasePrice; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
}
