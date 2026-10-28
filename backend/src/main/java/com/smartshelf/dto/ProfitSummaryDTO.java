package com.smartshelf.dto;

import java.math.BigDecimal;

public class ProfitSummaryDTO {
    private BigDecimal totalRevenue;
    private BigDecimal costOfGoodsSold;
    private BigDecimal grossProfit;
    private BigDecimal grossProfitMargin;
    private BigDecimal totalDiscountsGiven;
    private BigDecimal netEstimatedProfit;

    public ProfitSummaryDTO() {}

    public ProfitSummaryDTO(BigDecimal totalRevenue, BigDecimal costOfGoodsSold, BigDecimal grossProfit, BigDecimal grossProfitMargin, BigDecimal totalDiscountsGiven, BigDecimal netEstimatedProfit) {
        this.totalRevenue = totalRevenue;
        this.costOfGoodsSold = costOfGoodsSold;
        this.grossProfit = grossProfit;
        this.grossProfitMargin = grossProfitMargin;
        this.totalDiscountsGiven = totalDiscountsGiven;
        this.netEstimatedProfit = netEstimatedProfit;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private BigDecimal totalRevenue;
        private BigDecimal costOfGoodsSold;
        private BigDecimal grossProfit;
        private BigDecimal grossProfitMargin;
        private BigDecimal totalDiscountsGiven;
        private BigDecimal netEstimatedProfit;

        public Builder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public Builder costOfGoodsSold(BigDecimal costOfGoodsSold) { this.costOfGoodsSold = costOfGoodsSold; return this; }
        public Builder grossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; return this; }
        public Builder grossProfitMargin(BigDecimal grossProfitMargin) { this.grossProfitMargin = grossProfitMargin; return this; }
        public Builder totalDiscountsGiven(BigDecimal totalDiscountsGiven) { this.totalDiscountsGiven = totalDiscountsGiven; return this; }
        public Builder netEstimatedProfit(BigDecimal netEstimatedProfit) { this.netEstimatedProfit = netEstimatedProfit; return this; }

        public ProfitSummaryDTO build() {
            return new ProfitSummaryDTO(totalRevenue, costOfGoodsSold, grossProfit, grossProfitMargin, totalDiscountsGiven, netEstimatedProfit);
        }
    }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getCostOfGoodsSold() { return costOfGoodsSold; }
    public void setCostOfGoodsSold(BigDecimal costOfGoodsSold) { this.costOfGoodsSold = costOfGoodsSold; }

    public BigDecimal getGrossProfit() { return grossProfit; }
    public void setGrossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; }

    public BigDecimal getGrossProfitMargin() { return grossProfitMargin; }
    public void setGrossProfitMargin(BigDecimal grossProfitMargin) { this.grossProfitMargin = grossProfitMargin; }

    public BigDecimal getTotalDiscountsGiven() { return totalDiscountsGiven; }
    public void setTotalDiscountsGiven(BigDecimal totalDiscountsGiven) { this.totalDiscountsGiven = totalDiscountsGiven; }

    public BigDecimal getNetEstimatedProfit() { return netEstimatedProfit; }
    public void setNetEstimatedProfit(BigDecimal netEstimatedProfit) { this.netEstimatedProfit = netEstimatedProfit; }
}
