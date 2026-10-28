package com.smartshelf.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_transactions", indexes = {
        @Index(name = "idx_credit_customer", columnList = "customer_id"),
        @Index(name = "idx_credit_created", columnList = "createdAt")
})
public class CreditTransaction {

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
    @Column(nullable = false, length = 30)
    private CreditType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private PaymentMethod paymentMethod;

    @Column(length = 255)
    private String notes;

    @Column(length = 100)
    private String recordedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public CreditTransaction() {}

    public CreditTransaction(Long id, Customer customer, Order order, CreditType type, BigDecimal amount, BigDecimal balanceAfter, PaymentMethod paymentMethod, String notes, String recordedBy, LocalDateTime createdAt) {
        this.id = id;
        this.customer = customer;
        this.order = order;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        this.recordedBy = recordedBy;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Customer customer;
        private Order order;
        private CreditType type;
        private BigDecimal amount;
        private BigDecimal balanceAfter;
        private PaymentMethod paymentMethod;
        private String notes;
        private String recordedBy;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder customer(Customer customer) { this.customer = customer; return this; }
        public Builder order(Order order) { this.order = order; return this; }
        public Builder type(CreditType type) { this.type = type; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder balanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder recordedBy(String recordedBy) { this.recordedBy = recordedBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public CreditTransaction build() {
            return new CreditTransaction(id, customer, order, type, amount, balanceAfter, paymentMethod, notes, recordedBy, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public CreditType getType() { return type; }
    public void setType(CreditType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
