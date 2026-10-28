package com.smartshelf.services;

import com.smartshelf.dto.DemandPredictionDTO;
import com.smartshelf.dto.SmartReorderDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmartReorderService {

    private final DemandPredictionService demandPredictionService;

    public SmartReorderService(DemandPredictionService demandPredictionService) {
        this.demandPredictionService = demandPredictionService;
    }

    public List<SmartReorderDTO> getReorderSuggestions() {
        List<DemandPredictionDTO> predictions = demandPredictionService.getDemandPredictions();

        return predictions.stream()
                .filter(p -> p.getRecommendedOrderQuantity() > 0 || "CRITICAL".equals(p.getStockoutRiskLevel()) || "HIGH".equals(p.getStockoutRiskLevel()))
                .map(p -> SmartReorderDTO.builder()
                        .productId(p.getProductId())
                        .productName(p.getProductName())
                        .sku(p.getSku())
                        .currentStock(p.getCurrentStock())
                        .minimumStock(p.getSafetyStock())
                        .predictedDemand(p.getPredictedDemandNext7Days())
                        .recommendedReorderQuantity(p.getRecommendedOrderQuantity() > 0 ? p.getRecommendedOrderQuantity() : 10)
                        .supplierId(p.getSupplierId())
                        .supplierName(p.getSupplierName())
                        .unitPurchasePrice(p.getEstimatedReorderCost().compareTo(BigDecimal.ZERO) > 0 && p.getRecommendedOrderQuantity() > 0
                                ? p.getEstimatedReorderCost().divide(BigDecimal.valueOf(p.getRecommendedOrderQuantity()), 2, RoundingMode.HALF_UP)
                                : BigDecimal.valueOf(50.0))
                        .estimatedCost(p.getEstimatedReorderCost())
                        .urgency(p.getStockoutRiskLevel())
                        .build())
                .collect(Collectors.toList());
    }
}
