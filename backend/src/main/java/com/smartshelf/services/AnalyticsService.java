package com.smartshelf.services;

import com.smartshelf.dto.*;
import com.smartshelf.models.EntityType;
import com.smartshelf.models.Product;
import com.smartshelf.models.SalesRecord;
import com.smartshelf.repositories.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final SalesRecordRepository salesRecordRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;

    public AnalyticsService(SalesRecordRepository salesRecordRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, CustomerRepository customerRepository, InventoryRepository inventoryRepository) {
        this.salesRecordRepository = salesRecordRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public DashboardMetricsResponse getDashboardMetrics() {
        LocalDate today = LocalDate.now();
        SalesRecord todayRecord = salesRecordRepository.findByRecordDate(today).orElse(null);

        BigDecimal todaySales = (todayRecord != null) ? todayRecord.getTotalRevenue() : BigDecimal.ZERO;
        long todayOrders = (todayRecord != null) ? todayRecord.getTotalOrders() : 0L;

        BigDecimal totalRevenue = salesRecordRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        BigDecimal totalProfit = salesRecordRepository.getTotalProfit();
        if (totalProfit == null) totalProfit = BigDecimal.ZERO;

        BigDecimal margin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            margin = totalProfit.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 2, RoundingMode.HALF_UP);
        }

        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.countByActiveTrue();
        long lowStockCount = productRepository.findLowStockProducts().size();
        long expiringCount = productRepository.findExpiringProducts(today.plusDays(7)).size();
        long totalCustomers = customerRepository.countByActiveTrue();

        BigDecimal pendingUdhar = customerRepository.getTotalOutstandingCredit();
        if (pendingUdhar == null) pendingUdhar = BigDecimal.ZERO;

        // Inventory summary counts
        long criticalStock = productRepository.findOutOfStockProducts().size();
        long healthyCount = totalProducts - (lowStockCount + criticalStock);
        if (healthyCount < 0) healthyCount = 0;

        InventoryStatusSummaryDTO invSummary = InventoryStatusSummaryDTO.builder()
                .healthyCount(healthyCount)
                .lowStockCount(lowStockCount)
                .criticalCount(criticalStock)
                .expiredCount(expiringCount)
                .totalProducts(totalProducts)
                .build();

        // 7-day Sales trend
        List<SalesTrendDTO> salesTrend = new ArrayList<>();
        List<RevenueProfitDTO> revProfitTrend = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            SalesRecord rec = salesRecordRepository.findByRecordDate(date).orElse(null);
            BigDecimal daySales = (rec != null) ? rec.getTotalRevenue() : BigDecimal.ZERO;
            BigDecimal dayCost = (rec != null) ? rec.getTotalCost() : BigDecimal.ZERO;
            BigDecimal dayProfit = (rec != null) ? rec.getGrossProfit() : BigDecimal.ZERO;
            int orderCnt = (rec != null) ? rec.getTotalOrders() : 0;

            salesTrend.add(SalesTrendDTO.builder()
                    .date(date.toString())
                    .sales(daySales)
                    .orderCount(orderCnt)
                    .build());

            BigDecimal dayMargin = daySales.compareTo(BigDecimal.ZERO) > 0
                    ? dayProfit.multiply(BigDecimal.valueOf(100)).divide(daySales, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            revProfitTrend.add(RevenueProfitDTO.builder()
                    .period(date.getMonth().toString().substring(0, 3) + " " + date.getDayOfMonth())
                    .revenue(daySales)
                    .cost(dayCost)
                    .grossProfit(dayProfit)
                    .profitMargin(dayMargin)
                    .build());
        }

        // Top 5 Products
        List<TopProductDTO> topProducts = getTopSellingProducts(5);

        // Category Sales
        List<CategorySalesDTO> catSales = getCategorySalesDistribution();

        // Actionable Smart Insights
        List<SmartInsightDTO> smartInsights = generateDashboardSmartInsights(today);

        return DashboardMetricsResponse.builder()
                .todaySales(todaySales)
                .totalRevenue(totalRevenue)
                .totalProfit(totalProfit)
                .profitMarginPercentage(margin)
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .totalProducts(totalProducts)
                .lowStockCount(lowStockCount)
                .expiringSoonCount(expiringCount)
                .totalCustomers(totalCustomers)
                .pendingUdharTotal(pendingUdhar)
                .insights(smartInsights)
                .salesTrend(salesTrend)
                .revenueProfitTrend(revProfitTrend)
                .topProducts(topProducts)
                .categorySales(catSales)
                .inventoryStatus(invSummary)
                .build();
    }

    public List<TopProductDTO> getTopSellingProducts(int limit) {
        List<Object[]> rawList = orderItemRepository.findTopSellingProducts();
        List<TopProductDTO> result = new ArrayList<>();
        int count = 0;

        for (Object[] row : rawList) {
            if (count >= limit) break;
            Long prodId = (Long) row[0];
            String prodName = (String) row[1];
            Long units = ((Number) row[2]).longValue();
            BigDecimal rev = (BigDecimal) row[3];

            Product p = productRepository.findById(prodId).orElse(null);
            String catName = (p != null && p.getCategory() != null) ? p.getCategory().getName() : "General";
            BigDecimal cost = (p != null && p.getPurchasePrice() != null) ? p.getPurchasePrice().multiply(BigDecimal.valueOf(units)) : BigDecimal.ZERO;
            BigDecimal profit = rev.subtract(cost);

            result.add(TopProductDTO.builder()
                    .productId(prodId)
                    .productName(prodName)
                    .categoryName(catName)
                    .unitsSold(units)
                    .totalRevenue(rev)
                    .grossProfit(profit)
                    .build());
            count++;
        }
        return result;
    }

    public List<CategorySalesDTO> getCategorySalesDistribution() {
        List<Object[]> rawList = orderItemRepository.findSalesByCategory();
        List<CategorySalesDTO> list = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Object[] row : rawList) {
            BigDecimal rev = (BigDecimal) row[1];
            if (rev != null) grandTotal = grandTotal.add(rev);
        }

        for (Object[] row : rawList) {
            String catName = (String) row[0];
            BigDecimal rev = (BigDecimal) row[1];
            Long qty = ((Number) row[2]).longValue();

            double pct = 0.0;
            if (grandTotal.compareTo(BigDecimal.ZERO) > 0 && rev != null) {
                pct = rev.multiply(BigDecimal.valueOf(100)).divide(grandTotal, 2, RoundingMode.HALF_UP).doubleValue();
            }

            list.add(CategorySalesDTO.builder()
                    .categoryName(catName != null ? catName : "Uncategorized")
                    .totalSales(rev != null ? rev : BigDecimal.ZERO)
                    .totalQuantitySold(qty)
                    .percentageShare(pct)
                    .build());
        }
        return list;
    }

    public ProfitSummaryDTO getProfitSummary() {
        BigDecimal revenue = salesRecordRepository.getTotalRevenue();
        if (revenue == null) revenue = BigDecimal.ZERO;

        BigDecimal profit = salesRecordRepository.getTotalProfit();
        if (profit == null) profit = BigDecimal.ZERO;

        BigDecimal cogs = revenue.subtract(profit);
        BigDecimal margin = revenue.compareTo(BigDecimal.ZERO) > 0
                ? profit.multiply(BigDecimal.valueOf(100)).divide(revenue, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return ProfitSummaryDTO.builder()
                .totalRevenue(revenue)
                .costOfGoodsSold(cogs)
                .grossProfit(profit)
                .grossProfitMargin(margin)
                .totalDiscountsGiven(BigDecimal.valueOf(150.00))
                .netEstimatedProfit(profit.subtract(BigDecimal.valueOf(150.00)))
                .build();
    }

    private List<SmartInsightDTO> generateDashboardSmartInsights(LocalDate today) {
        List<SmartInsightDTO> insights = new ArrayList<>();

        List<Product> lowStockList = productRepository.findLowStockProducts();
        if (!lowStockList.isEmpty()) {
            Product p = lowStockList.get(0);
            insights.add(SmartInsightDTO.builder()
                    .id("INSIGHT-LOW-STOCK")
                    .title("Reorder Alert: " + p.getName())
                    .description(p.getName() + " only has " + p.getCurrentStock() + " units left (Min: " + p.getMinimumStock() + "). High risk of stockout during peak hours.")
                    .category("INVENTORY")
                    .impact("HIGH")
                    .actionLabel("Order Stock")
                    .actionLink("/purchases/new?productId=" + p.getId())
                    .entityType(EntityType.PRODUCT)
                    .entityId(p.getId())
                    .build());
        }

        List<Product> expiringList = productRepository.findExpiringProducts(today.plusDays(10));
        if (!expiringList.isEmpty()) {
            Product p = expiringList.get(0);
            insights.add(SmartInsightDTO.builder()
                    .id("INSIGHT-EXPIRY")
                    .title("Expiry Clearance: " + p.getName())
                    .description(p.getName() + " expires on " + p.getExpiryDate() + ". Apply a 15% discount to clear remaining " + p.getCurrentStock() + " units.")
                    .category("DISCOUNT")
                    .impact("MEDIUM")
                    .actionLabel("Apply Discount")
                    .actionLink("/ai/discounts")
                    .entityType(EntityType.PRODUCT)
                    .entityId(p.getId())
                    .build());
        }

        BigDecimal pendingUdhar = customerRepository.getTotalOutstandingCredit();
        if (pendingUdhar != null && pendingUdhar.compareTo(BigDecimal.valueOf(1000)) > 0) {
            insights.add(SmartInsightDTO.builder()
                    .id("INSIGHT-UDHAR")
                    .title("Outstanding Udhar Total ₹" + pendingUdhar)
                    .description("Multiple customers have pending balances over 15 days. Send payment reminders or view credit register.")
                    .category("CREDIT")
                    .impact("HIGH")
                    .actionLabel("View Udhar Register")
                    .actionLink("/credit")
                    .entityType(EntityType.CREDIT_RECORD)
                    .build());
        }

        return insights;
    }
}
