package com.smartshelf.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardMetricsResponse {
    private BigDecimal todaySales;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private BigDecimal profitMarginPercentage;
    private Long totalOrders;
    private Long todayOrders;
    private Long totalProducts;
    private Long lowStockCount;
    private Long expiringSoonCount;
    private Long totalCustomers;
    private BigDecimal pendingUdharTotal;
    private List<SmartInsightDTO> insights = new ArrayList<>();
    private List<SalesTrendDTO> salesTrend = new ArrayList<>();
    private List<RevenueProfitDTO> revenueProfitTrend = new ArrayList<>();
    private List<TopProductDTO> topProducts = new ArrayList<>();
    private List<CategorySalesDTO> categorySales = new ArrayList<>();
    private InventoryStatusSummaryDTO inventoryStatus;

    public DashboardMetricsResponse() {}

    public DashboardMetricsResponse(BigDecimal todaySales, BigDecimal totalRevenue, BigDecimal totalProfit, BigDecimal profitMarginPercentage, Long totalOrders, Long todayOrders, Long totalProducts, Long lowStockCount, Long expiringSoonCount, Long totalCustomers, BigDecimal pendingUdharTotal, List<SmartInsightDTO> insights, List<SalesTrendDTO> salesTrend, List<RevenueProfitDTO> revenueProfitTrend, List<TopProductDTO> topProducts, List<CategorySalesDTO> categorySales, InventoryStatusSummaryDTO inventoryStatus) {
        this.todaySales = todaySales;
        this.totalRevenue = totalRevenue;
        this.totalProfit = totalProfit;
        this.profitMarginPercentage = profitMarginPercentage;
        this.totalOrders = totalOrders;
        this.todayOrders = todayOrders;
        this.totalProducts = totalProducts;
        this.lowStockCount = lowStockCount;
        this.expiringSoonCount = expiringSoonCount;
        this.totalCustomers = totalCustomers;
        this.pendingUdharTotal = pendingUdharTotal;
        this.insights = (insights != null) ? insights : new ArrayList<>();
        this.salesTrend = (salesTrend != null) ? salesTrend : new ArrayList<>();
        this.revenueProfitTrend = (revenueProfitTrend != null) ? revenueProfitTrend : new ArrayList<>();
        this.topProducts = (topProducts != null) ? topProducts : new ArrayList<>();
        this.categorySales = (categorySales != null) ? categorySales : new ArrayList<>();
        this.inventoryStatus = inventoryStatus;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BigDecimal todaySales;
        private BigDecimal totalRevenue;
        private BigDecimal totalProfit;
        private BigDecimal profitMarginPercentage;
        private Long totalOrders;
        private Long todayOrders;
        private Long totalProducts;
        private Long lowStockCount;
        private Long expiringSoonCount;
        private Long totalCustomers;
        private BigDecimal pendingUdharTotal;
        private List<SmartInsightDTO> insights = new ArrayList<>();
        private List<SalesTrendDTO> salesTrend = new ArrayList<>();
        private List<RevenueProfitDTO> revenueProfitTrend = new ArrayList<>();
        private List<TopProductDTO> topProducts = new ArrayList<>();
        private List<CategorySalesDTO> categorySales = new ArrayList<>();
        private InventoryStatusSummaryDTO inventoryStatus;

        public Builder todaySales(BigDecimal todaySales) { this.todaySales = todaySales; return this; }
        public Builder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public Builder totalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; return this; }
        public Builder profitMarginPercentage(BigDecimal profitMarginPercentage) { this.profitMarginPercentage = profitMarginPercentage; return this; }
        public Builder totalOrders(Long totalOrders) { this.totalOrders = totalOrders; return this; }
        public Builder todayOrders(Long todayOrders) { this.todayOrders = todayOrders; return this; }
        public Builder totalProducts(Long totalProducts) { this.totalProducts = totalProducts; return this; }
        public Builder lowStockCount(Long lowStockCount) { this.lowStockCount = lowStockCount; return this; }
        public Builder expiringSoonCount(Long expiringSoonCount) { this.expiringSoonCount = expiringSoonCount; return this; }
        public Builder totalCustomers(Long totalCustomers) { this.totalCustomers = totalCustomers; return this; }
        public Builder pendingUdharTotal(BigDecimal pendingUdharTotal) { this.pendingUdharTotal = pendingUdharTotal; return this; }
        public Builder insights(List<SmartInsightDTO> insights) { this.insights = insights; return this; }
        public Builder salesTrend(List<SalesTrendDTO> salesTrend) { this.salesTrend = salesTrend; return this; }
        public Builder revenueProfitTrend(List<RevenueProfitDTO> revenueProfitTrend) { this.revenueProfitTrend = revenueProfitTrend; return this; }
        public Builder topProducts(List<TopProductDTO> topProducts) { this.topProducts = topProducts; return this; }
        public Builder categorySales(List<CategorySalesDTO> categorySales) { this.categorySales = categorySales; return this; }
        public Builder inventoryStatus(InventoryStatusSummaryDTO inventoryStatus) { this.inventoryStatus = inventoryStatus; return this; }

        public DashboardMetricsResponse build() {
            return new DashboardMetricsResponse(todaySales, totalRevenue, totalProfit, profitMarginPercentage, totalOrders, todayOrders, totalProducts, lowStockCount, expiringSoonCount, totalCustomers, pendingUdharTotal, insights, salesTrend, revenueProfitTrend, topProducts, categorySales, inventoryStatus);
        }
    }

    public BigDecimal getTodaySales() { return todaySales; }
    public void setTodaySales(BigDecimal todaySales) { this.todaySales = todaySales; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

    public BigDecimal getProfitMarginPercentage() { return profitMarginPercentage; }
    public void setProfitMarginPercentage(BigDecimal profitMarginPercentage) { this.profitMarginPercentage = profitMarginPercentage; }

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }

    public Long getTodayOrders() { return todayOrders; }
    public void setTodayOrders(Long todayOrders) { this.todayOrders = todayOrders; }

    public Long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }

    public Long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(Long lowStockCount) { this.lowStockCount = lowStockCount; }

    public Long getExpiringSoonCount() { return expiringSoonCount; }
    public void setExpiringSoonCount(Long expiringSoonCount) { this.expiringSoonCount = expiringSoonCount; }

    public Long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(Long totalCustomers) { this.totalCustomers = totalCustomers; }

    public BigDecimal getPendingUdharTotal() { return pendingUdharTotal; }
    public void setPendingUdharTotal(BigDecimal pendingUdharTotal) { this.pendingUdharTotal = pendingUdharTotal; }

    public List<SmartInsightDTO> getInsights() { return insights; }
    public void setInsights(List<SmartInsightDTO> insights) { this.insights = insights; }

    public List<SalesTrendDTO> getSalesTrend() { return salesTrend; }
    public void setSalesTrend(List<SalesTrendDTO> salesTrend) { this.salesTrend = salesTrend; }

    public List<RevenueProfitDTO> getRevenueProfitTrend() { return revenueProfitTrend; }
    public void setRevenueProfitTrend(List<RevenueProfitDTO> revenueProfitTrend) { this.revenueProfitTrend = revenueProfitTrend; }

    public List<TopProductDTO> getTopProducts() { return topProducts; }
    public void setTopProducts(List<TopProductDTO> topProducts) { this.topProducts = topProducts; }

    public List<CategorySalesDTO> getCategorySales() { return categorySales; }
    public void setCategorySales(List<CategorySalesDTO> categorySales) { this.categorySales = categorySales; }

    public InventoryStatusSummaryDTO getInventoryStatus() { return inventoryStatus; }
    public void setInventoryStatus(InventoryStatusSummaryDTO inventoryStatus) { this.inventoryStatus = inventoryStatus; }
}
