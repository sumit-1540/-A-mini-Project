package com.smartshelf.dto;

import java.math.BigDecimal;

public class RevenueProfitDTO {
    private String period;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal grossProfit;
    private BigDecimal profitMargin;

    public RevenueProfitDTO() {}

    public RevenueProfitDTO(String period, BigDecimal revenue, BigDecimal cost, BigDecimal grossProfit, BigDecimal profitMargin) {
        this.period = period;
        this.revenue = revenue;
        this.cost = cost;
        this.grossProfit = grossProfit;
        this.profitMargin = profitMargin;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String period;
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal grossProfit;
        private BigDecimal profitMargin;

        public Builder period(String period) { this.period = period; return this; }
        public Builder revenue(BigDecimal revenue) { this.revenue = revenue; return this; }
        public Builder cost(BigDecimal cost) { this.cost = cost; return this; }
        public Builder grossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; return this; }
        public Builder profitMargin(BigDecimal profitMargin) { this.profitMargin = profitMargin; return this; }

        public RevenueProfitDTO build() {
            return new RevenueProfitDTO(period, revenue, cost, grossProfit, profitMargin);
        }
    }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public BigDecimal getGrossProfit() { return grossProfit; }
    public void setGrossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; }

    public BigDecimal getProfitMargin() { return profitMargin; }
    public void setProfitMargin(BigDecimal profitMargin) { this.profitMargin = profitMargin; }
}
