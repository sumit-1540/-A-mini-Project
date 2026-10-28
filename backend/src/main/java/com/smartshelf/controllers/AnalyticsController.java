package com.smartshelf.controllers;

import com.smartshelf.dto.*;
import com.smartshelf.services.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetricsResponse> getDashboardMetrics() {
        return ResponseEntity.ok(analyticsService.getDashboardMetrics());
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TopProductDTO>> getTopProducts(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getTopSellingProducts(limit));
    }

    @GetMapping("/category-sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategorySalesDTO>> getCategorySales() {
        return ResponseEntity.ok(analyticsService.getCategorySalesDistribution());
    }

    @GetMapping("/profit-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProfitSummaryDTO> getProfitSummary() {
        return ResponseEntity.ok(analyticsService.getProfitSummary());
    }
}
