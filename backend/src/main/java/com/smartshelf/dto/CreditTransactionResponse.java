package com.smartshelf.dto;

import com.smartshelf.models.CreditType;
import com.smartshelf.models.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreditTransactionResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long orderId;
    private String orderNumber;
    private CreditType type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private PaymentMethod paymentMethod;
    private String notes;
    private String recordedBy;
    private LocalDateTime createdAt;

    public CreditTransactionResponse() {}

    public CreditTransactionResponse(Long id, Long customerId, String customerName, String customerPhone, Long orderId, String orderNumber, CreditType type, BigDecimal amount, BigDecimal balanceAfter, PaymentMethod paymentMethod, String notes, String recordedBy, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
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
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private Long orderId;
        private String orderNumber;
        private CreditType type;
        private BigDecimal amount;
        private BigDecimal balanceAfter;
        private PaymentMethod paymentMethod;
        private String notes;
        private String recordedBy;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder customerId(Long customerId) { this.customerId = customerId; return this; }
        public Builder customerName(String customerName) { this.customerName = customerName; return this; }
        public Builder customerPhone(String customerPhone) { this.customerPhone = customerPhone; return this; }
        public Builder orderId(Long orderId) { this.orderId = orderId; return this; }
        public Builder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public Builder type(CreditType type) { this.type = type; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder balanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder recordedBy(String recordedBy) { this.recordedBy = recordedBy; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public CreditTransactionResponse build() {
            return new CreditTransactionResponse(id, customerId, customerName, customerPhone, orderId, orderNumber, type, amount, balanceAfter, paymentMethod, notes, recordedBy, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

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
