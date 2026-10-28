package com.smartshelf.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_transactions", indexes = {
        @Index(name = "idx_loyalty_customer", columnList = "customer_id"),
        @Index(name = "idx_loyalty_created", columnList = "createdAt")
})
public class LoyaltyTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoyaltyType type;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private Integer balanceAfter;

    @Column(length = 255)
    private String reason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public LoyaltyTransaction() {}

    public LoyaltyTransaction(Long id, Customer customer, Order order, LoyaltyType type, Integer points, Integer balanceAfter, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.customer = customer;
        this.order = order;
        this.type = type;
        this.points = points;
        this.balanceAfter = balanceAfter;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Customer customer;
        private Order order;
        private LoyaltyType type;
        private Integer points;
        private Integer balanceAfter;
        private String reason;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder customer(Customer customer) { this.customer = customer; return this; }
        public Builder order(Order order) { this.order = order; return this; }
        public Builder type(LoyaltyType type) { this.type = type; return this; }
        public Builder points(Integer points) { this.points = points; return this; }
        public Builder balanceAfter(Integer balanceAfter) { this.balanceAfter = balanceAfter; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public LoyaltyTransaction build() {
            return new LoyaltyTransaction(id, customer, order, type, points, balanceAfter, reason, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public LoyaltyType getType() { return type; }
    public void setType(LoyaltyType type) { this.type = type; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(Integer balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
