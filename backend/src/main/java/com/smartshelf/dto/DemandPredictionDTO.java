package com.smartshelf.dto;

import java.math.BigDecimal;

public class DemandPredictionDTO {
    private Long productId;
    private String productName;
    private String sku;
    private String categoryName;
    private Integer currentStock;
    private Integer historicalSalesLast30Days;
    private Double averageDailySales;
    private Integer predictedDemandNext7Days;
    private Integer predictedMonthlyVolume;
    private Integer safetyStock;
    private Integer recommendedOrderQuantity;
    private String stockoutRiskLevel;
    private String restockTier;
    private Integer daysOfInventoryLeft;
    private BigDecimal estimatedReorderCost;
    private BigDecimal unitPrice;
    private Integer turnoverRate;
    private String mlModelUsed;
    private Double mlConfidenceScore;
    private Long supplierId;
    private String supplierName;
    private String imageUrl;

    public DemandPredictionDTO() {}

    public DemandPredictionDTO(Long productId, String productName, String sku, String categoryName, Integer currentStock, Integer historicalSalesLast30Days, Double averageDailySales, Integer predictedDemandNext7Days, Integer predictedMonthlyVolume, Integer safetyStock, Integer recommendedOrderQuantity, String stockoutRiskLevel, String restockTier, Integer daysOfInventoryLeft, BigDecimal estimatedReorderCost, BigDecimal unitPrice, Integer turnoverRate, String mlModelUsed, Double mlConfidenceScore, Long supplierId, String supplierName, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.categoryName = categoryName;
        this.currentStock = currentStock;
        this.historicalSalesLast30Days = historicalSalesLast30Days;
        this.averageDailySales = averageDailySales;
        this.predictedDemandNext7Days = predictedDemandNext7Days;
        this.predictedMonthlyVolume = predictedMonthlyVolume;
        this.safetyStock = safetyStock;
        this.recommendedOrderQuantity = recommendedOrderQuantity;
        this.stockoutRiskLevel = stockoutRiskLevel;
        this.restockTier = restockTier;
        this.daysOfInventoryLeft = daysOfInventoryLeft;
        this.estimatedReorderCost = estimatedReorderCost;
        this.unitPrice = unitPrice;
        this.turnoverRate = turnoverRate;
        this.mlModelUsed = mlModelUsed;
        this.mlConfidenceScore = mlConfidenceScore;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.imageUrl = imageUrl;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long productId;
        private String productName;
        private String sku;
        private String categoryName;
        private Integer currentStock;
        private Integer historicalSalesLast30Days;
        private Double averageDailySales;
        private Integer predictedDemandNext7Days;
        private Integer predictedMonthlyVolume;
        private Integer safetyStock;
        private Integer recommendedOrderQuantity;
        private String stockoutRiskLevel;
        private String restockTier;
        private Integer daysOfInventoryLeft;
        private BigDecimal estimatedReorderCost;
        private BigDecimal unitPrice;
        private Integer turnoverRate;
        private String mlModelUsed;
        private Double mlConfidenceScore;
        private Long supplierId;
        private String supplierName;
        private String imageUrl;

        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder currentStock(Integer currentStock) { this.currentStock = currentStock; return this; }
        public Builder historicalSalesLast30Days(Integer historicalSalesLast30Days) { this.historicalSalesLast30Days = historicalSalesLast30Days; return this; }
        public Builder averageDailySales(Double averageDailySales) { this.averageDailySales = averageDailySales; return this; }
        public Builder predictedDemandNext7Days(Integer predictedDemandNext7Days) { this.predictedDemandNext7Days = predictedDemandNext7Days; return this; }
        public Builder predictedMonthlyVolume(Integer predictedMonthlyVolume) { this.predictedMonthlyVolume = predictedMonthlyVolume; return this; }
        public Builder safetyStock(Integer safetyStock) { this.safetyStock = safetyStock; return this; }
        public Builder recommendedOrderQuantity(Integer recommendedOrderQuantity) { this.recommendedOrderQuantity = recommendedOrderQuantity; return this; }
        public Builder stockoutRiskLevel(String stockoutRiskLevel) { this.stockoutRiskLevel = stockoutRiskLevel; return this; }
        public Builder restockTier(String restockTier) { this.restockTier = restockTier; return this; }
        public Builder daysOfInventoryLeft(Integer daysOfInventoryLeft) { this.daysOfInventoryLeft = daysOfInventoryLeft; return this; }
        public Builder estimatedReorderCost(BigDecimal estimatedReorderCost) { this.estimatedReorderCost = estimatedReorderCost; return this; }
        public Builder unitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; return this; }
        public Builder turnoverRate(Integer turnoverRate) { this.turnoverRate = turnoverRate; return this; }
        public Builder mlModelUsed(String mlModelUsed) { this.mlModelUsed = mlModelUsed; return this; }
        public Builder mlConfidenceScore(Double mlConfidenceScore) { this.mlConfidenceScore = mlConfidenceScore; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public DemandPredictionDTO build() {
            return new DemandPredictionDTO(productId, productName, sku, categoryName, currentStock, historicalSalesLast30Days, averageDailySales, predictedDemandNext7Days, predictedMonthlyVolume, safetyStock, recommendedOrderQuantity, stockoutRiskLevel, restockTier, daysOfInventoryLeft, estimatedReorderCost, unitPrice, turnoverRate, mlModelUsed, mlConfidenceScore, supplierId, supplierName, imageUrl);
        }
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getHistoricalSalesLast30Days() { return historicalSalesLast30Days; }
    public void setHistoricalSalesLast30Days(Integer historicalSalesLast30Days) { this.historicalSalesLast30Days = historicalSalesLast30Days; }

    public Double getAverageDailySales() { return averageDailySales; }
    public void setAverageDailySales(Double averageDailySales) { this.averageDailySales = averageDailySales; }

    public Integer getPredictedDemandNext7Days() { return predictedDemandNext7Days; }
    public void setPredictedDemandNext7Days(Integer predictedDemandNext7Days) { this.predictedDemandNext7Days = predictedDemandNext7Days; }

    public Integer getPredictedMonthlyVolume() { return predictedMonthlyVolume; }
    public void setPredictedMonthlyVolume(Integer predictedMonthlyVolume) { this.predictedMonthlyVolume = predictedMonthlyVolume; }

    public Integer getSafetyStock() { return safetyStock; }
    public void setSafetyStock(Integer safetyStock) { this.safetyStock = safetyStock; }

    public Integer getRecommendedOrderQuantity() { return recommendedOrderQuantity; }
    public void setRecommendedOrderQuantity(Integer recommendedOrderQuantity) { this.recommendedOrderQuantity = recommendedOrderQuantity; }

    public String getStockoutRiskLevel() { return stockoutRiskLevel; }
    public void setStockoutRiskLevel(String stockoutRiskLevel) { this.stockoutRiskLevel = stockoutRiskLevel; }

    public String getRestockTier() { return restockTier; }
    public void setRestockTier(String restockTier) { this.restockTier = restockTier; }

    public Integer getDaysOfInventoryLeft() { return daysOfInventoryLeft; }
    public void setDaysOfInventoryLeft(Integer daysOfInventoryLeft) { this.daysOfInventoryLeft = daysOfInventoryLeft; }

    public BigDecimal getEstimatedReorderCost() { return estimatedReorderCost; }
    public void setEstimatedReorderCost(BigDecimal estimatedReorderCost) { this.estimatedReorderCost = estimatedReorderCost; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getTurnoverRate() { return turnoverRate; }
    public void setTurnoverRate(Integer turnoverRate) { this.turnoverRate = turnoverRate; }

    public String getMlModelUsed() { return mlModelUsed; }
    public void setMlModelUsed(String mlModelUsed) { this.mlModelUsed = mlModelUsed; }

    public Double getMlConfidenceScore() { return mlConfidenceScore; }
    public void setMlConfidenceScore(Double mlConfidenceScore) { this.mlConfidenceScore = mlConfidenceScore; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
