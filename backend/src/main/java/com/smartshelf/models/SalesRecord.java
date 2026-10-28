package com.smartshelf.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales_records", indexes = {
        @Index(name = "idx_sales_rec_date", columnList = "recordDate", unique = true)
})
public class SalesRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate recordDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal grossProfit = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer totalOrders = 0;

    @Column(nullable = false)
    private Integer totalItemsSold = 0;

    public SalesRecord() {}

    public SalesRecord(Long id, LocalDate recordDate, BigDecimal totalRevenue, BigDecimal totalCost, BigDecimal grossProfit, Integer totalOrders, Integer totalItemsSold) {
        this.id = id;
        this.recordDate = recordDate;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
        this.grossProfit = grossProfit != null ? grossProfit : BigDecimal.ZERO;
        this.totalOrders = totalOrders != null ? totalOrders : 0;
        this.totalItemsSold = totalItemsSold != null ? totalItemsSold : 0;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private LocalDate recordDate;
        private BigDecimal totalRevenue = BigDecimal.ZERO;
        private BigDecimal totalCost = BigDecimal.ZERO;
        private BigDecimal grossProfit = BigDecimal.ZERO;
        private Integer totalOrders = 0;
        private Integer totalItemsSold = 0;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder recordDate(LocalDate recordDate) { this.recordDate = recordDate; return this; }
        public Builder totalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; return this; }
        public Builder totalCost(BigDecimal totalCost) { this.totalCost = totalCost; return this; }
        public Builder grossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; return this; }
        public Builder totalOrders(Integer totalOrders) { this.totalOrders = totalOrders; return this; }
        public Builder totalItemsSold(Integer totalItemsSold) { this.totalItemsSold = totalItemsSold; return this; }

        public SalesRecord build() {
            return new SalesRecord(id, recordDate, totalRevenue, totalCost, grossProfit, totalOrders, totalItemsSold);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public BigDecimal getGrossProfit() { return grossProfit; }
    public void setGrossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public Integer getTotalItemsSold() { return totalItemsSold; }
    public void setTotalItemsSold(Integer totalItemsSold) { this.totalItemsSold = totalItemsSold; }
}
