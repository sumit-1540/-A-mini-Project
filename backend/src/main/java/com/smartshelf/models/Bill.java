package com.smartshelf.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills", indexes = {
        @Index(name = "idx_bills_number", columnList = "billNumber")
})
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String billNumber;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal changeReturned = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceDue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private PaymentMethod paymentMethod;

    @Column(length = 100)
    private String transactionReference;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Bill() {}

    public Bill(Long id, String billNumber, Order order, BigDecimal amountPaid, BigDecimal changeReturned, BigDecimal balanceDue, PaymentMethod paymentMethod, String transactionReference, LocalDateTime createdAt) {
        this.id = id;
        this.billNumber = billNumber;
        this.order = order;
        this.amountPaid = amountPaid;
        this.changeReturned = (changeReturned != null) ? changeReturned : BigDecimal.ZERO;
        this.balanceDue = (balanceDue != null) ? balanceDue : BigDecimal.ZERO;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String billNumber;
        private Order order;
        private BigDecimal amountPaid;
        private BigDecimal changeReturned = BigDecimal.ZERO;
        private BigDecimal balanceDue = BigDecimal.ZERO;
        private PaymentMethod paymentMethod;
        private String transactionReference;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder billNumber(String billNumber) { this.billNumber = billNumber; return this; }
        public Builder order(Order order) { this.order = order; return this; }
        public Builder amountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; return this; }
        public Builder changeReturned(BigDecimal changeReturned) { this.changeReturned = changeReturned; return this; }
        public Builder balanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder transactionReference(String transactionReference) { this.transactionReference = transactionReference; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Bill build() {
            return new Bill(id, billNumber, order, amountPaid, changeReturned, balanceDue, paymentMethod, transactionReference, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getChangeReturned() { return changeReturned; }
    public void setChangeReturned(BigDecimal changeReturned) { this.changeReturned = changeReturned; }

    public BigDecimal getBalanceDue() { return balanceDue; }
    public void setBalanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
