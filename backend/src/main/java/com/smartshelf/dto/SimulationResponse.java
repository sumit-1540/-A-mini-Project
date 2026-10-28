package com.smartshelf.dto;

import java.math.BigDecimal;

public class SimulationResponse {
    private String productName;
    private Integer predictedMonthlySalesVolume;
    private Integer predictedDemandNext7Days;
    private Integer recommendedOrderQuantity;
    private String restockTier;
    private String stockoutRiskLevel;
    private BigDecimal estimatedReorderCost;
    private String mlModelUsed;
    private Double confidenceScore;

    public SimulationResponse() {}

    public SimulationResponse(String productName, Integer predictedMonthlySalesVolume, Integer predictedDemandNext7Days, Integer recommendedOrderQuantity, String restockTier, String stockoutRiskLevel, BigDecimal estimatedReorderCost, String mlModelUsed, Double confidenceScore) {
        this.productName = productName;
        this.predictedMonthlySalesVolume = predictedMonthlySalesVolume;
        this.predictedDemandNext7Days = predictedDemandNext7Days;
        this.recommendedOrderQuantity = recommendedOrderQuantity;
        this.restockTier = restockTier;
        this.stockoutRiskLevel = stockoutRiskLevel;
        this.estimatedReorderCost = estimatedReorderCost;
        this.mlModelUsed = mlModelUsed;
        this.confidenceScore = confidenceScore;
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getPredictedMonthlySalesVolume() { return predictedMonthlySalesVolume; }
    public void setPredictedMonthlySalesVolume(Integer predictedMonthlySalesVolume) { this.predictedMonthlySalesVolume = predictedMonthlySalesVolume; }

    public Integer getPredictedDemandNext7Days() { return predictedDemandNext7Days; }
    public void setPredictedDemandNext7Days(Integer predictedDemandNext7Days) { this.predictedDemandNext7Days = predictedDemandNext7Days; }

    public Integer getRecommendedOrderQuantity() { return recommendedOrderQuantity; }
    public void setRecommendedOrderQuantity(Integer recommendedOrderQuantity) { this.recommendedOrderQuantity = recommendedOrderQuantity; }

    public String getRestockTier() { return restockTier; }
    public void setRestockTier(String restockTier) { this.restockTier = restockTier; }

    public String getStockoutRiskLevel() { return stockoutRiskLevel; }
    public void setStockoutRiskLevel(String stockoutRiskLevel) { this.stockoutRiskLevel = stockoutRiskLevel; }

    public BigDecimal getEstimatedReorderCost() { return estimatedReorderCost; }
    public void setEstimatedReorderCost(BigDecimal estimatedReorderCost) { this.estimatedReorderCost = estimatedReorderCost; }

    public String getMlModelUsed() { return mlModelUsed; }
    public void setMlModelUsed(String mlModelUsed) { this.mlModelUsed = mlModelUsed; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
}
