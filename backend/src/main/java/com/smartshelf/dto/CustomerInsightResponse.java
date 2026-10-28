package com.smartshelf.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerInsightResponse {
    private Long customerId;
    private String customerName;
    private String phone;
    private BigDecimal totalSpending;
    private Integer totalOrders;
    private BigDecimal averageOrderValue;
    private BigDecimal outstandingUdhar;
    private Integer rewardPoints;
    private LocalDateTime lastPurchaseDate;
    private String preferredPaymentMethod;
    private List<ProductResponse> frequentlyPurchasedProducts = new ArrayList<>();
    private List<String> favoriteCategories = new ArrayList<>();

    public CustomerInsightResponse() {}

    public CustomerInsightResponse(Long customerId, String customerName, String phone, BigDecimal totalSpending, Integer totalOrders, BigDecimal averageOrderValue, BigDecimal outstandingUdhar, Integer rewardPoints, LocalDateTime lastPurchaseDate, String preferredPaymentMethod, List<ProductResponse> frequentlyPurchasedProducts, List<String> favoriteCategories) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phone = phone;
        this.totalSpending = totalSpending;
        this.totalOrders = totalOrders;
        this.averageOrderValue = averageOrderValue;
        this.outstandingUdhar = outstandingUdhar;
        this.rewardPoints = rewardPoints;
        this.lastPurchaseDate = lastPurchaseDate;
        this.preferredPaymentMethod = preferredPaymentMethod;
        this.frequentlyPurchasedProducts = (frequentlyPurchasedProducts != null) ? frequentlyPurchasedProducts : new ArrayList<>();
        this.favoriteCategories = (favoriteCategories != null) ? favoriteCategories : new ArrayList<>();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long customerId;
        private String customerName;
        private String phone;
        private BigDecimal totalSpending;
        private Integer totalOrders;
        private BigDecimal averageOrderValue;
        private BigDecimal outstandingUdhar;
        private Integer rewardPoints;
        private LocalDateTime lastPurchaseDate;
        private String preferredPaymentMethod;
        private List<ProductResponse> frequentlyPurchasedProducts = new ArrayList<>();
        private List<String> favoriteCategories = new ArrayList<>();

        public Builder customerId(Long customerId) { this.customerId = customerId; return this; }
        public Builder customerName(String customerName) { this.customerName = customerName; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder totalSpending(BigDecimal totalSpending) { this.totalSpending = totalSpending; return this; }
        public Builder totalOrders(Integer totalOrders) { this.totalOrders = totalOrders; return this; }
        public Builder averageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; return this; }
        public Builder outstandingUdhar(BigDecimal outstandingUdhar) { this.outstandingUdhar = outstandingUdhar; return this; }
        public Builder rewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; return this; }
        public Builder lastPurchaseDate(LocalDateTime lastPurchaseDate) { this.lastPurchaseDate = lastPurchaseDate; return this; }
        public Builder preferredPaymentMethod(String preferredPaymentMethod) { this.preferredPaymentMethod = preferredPaymentMethod; return this; }
        public Builder frequentlyPurchasedProducts(List<ProductResponse> frequentlyPurchasedProducts) { this.frequentlyPurchasedProducts = frequentlyPurchasedProducts; return this; }
        public Builder favoriteCategories(List<String> favoriteCategories) { this.favoriteCategories = favoriteCategories; return this; }

        public CustomerInsightResponse build() {
            return new CustomerInsightResponse(customerId, customerName, phone, totalSpending, totalOrders, averageOrderValue, outstandingUdhar, rewardPoints, lastPurchaseDate, preferredPaymentMethod, frequentlyPurchasedProducts, favoriteCategories);
        }
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public BigDecimal getTotalSpending() { return totalSpending; }
    public void setTotalSpending(BigDecimal totalSpending) { this.totalSpending = totalSpending; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }

    public BigDecimal getOutstandingUdhar() { return outstandingUdhar; }
    public void setOutstandingUdhar(BigDecimal outstandingUdhar) { this.outstandingUdhar = outstandingUdhar; }

    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }

    public LocalDateTime getLastPurchaseDate() { return lastPurchaseDate; }
    public void setLastPurchaseDate(LocalDateTime lastPurchaseDate) { this.lastPurchaseDate = lastPurchaseDate; }

    public String getPreferredPaymentMethod() { return preferredPaymentMethod; }
    public void setPreferredPaymentMethod(String preferredPaymentMethod) { this.preferredPaymentMethod = preferredPaymentMethod; }

    public List<ProductResponse> getFrequentlyPurchasedProducts() { return frequentlyPurchasedProducts; }
    public void setFrequentlyPurchasedProducts(List<ProductResponse> frequentlyPurchasedProducts) { this.frequentlyPurchasedProducts = frequentlyPurchasedProducts; }

    public List<String> getFavoriteCategories() { return favoriteCategories; }
    public void setFavoriteCategories(List<String> favoriteCategories) { this.favoriteCategories = favoriteCategories; }
}
