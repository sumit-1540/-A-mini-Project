package com.smartshelf.dto;

import com.smartshelf.models.OrderStatus;
import com.smartshelf.models.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long cashierId;
    private String cashierName;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal grossProfit;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private String notes;
    private String billNumber;
    private BigDecimal amountPaid;
    private BigDecimal changeReturned;
    private BigDecimal balanceDue;
    private List<OrderItemResponse> items = new ArrayList<>();
    private LocalDateTime createdAt;

    public OrderResponse() {}

    public OrderResponse(Long id, String orderNumber, Long customerId, String customerName, String customerPhone, Long cashierId, String cashierName, BigDecimal subtotal, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal totalAmount, BigDecimal grossProfit, PaymentMethod paymentMethod, OrderStatus status, String notes, String billNumber, BigDecimal amountPaid, BigDecimal changeReturned, BigDecimal balanceDue, List<OrderItemResponse> items, LocalDateTime createdAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.cashierId = cashierId;
        this.cashierName = cashierName;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.grossProfit = grossProfit;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.notes = notes;
        this.billNumber = billNumber;
        this.amountPaid = amountPaid;
        this.changeReturned = changeReturned;
        this.balanceDue = balanceDue;
        this.items = (items != null) ? items : new ArrayList<>();
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String orderNumber;
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private Long cashierId;
        private String cashierName;
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private BigDecimal grossProfit;
        private PaymentMethod paymentMethod;
        private OrderStatus status;
        private String notes;
        private String billNumber;
        private BigDecimal amountPaid;
        private BigDecimal changeReturned;
        private BigDecimal balanceDue;
        private List<OrderItemResponse> items = new ArrayList<>();
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public Builder customerId(Long customerId) { this.customerId = customerId; return this; }
        public Builder customerName(String customerName) { this.customerName = customerName; return this; }
        public Builder customerPhone(String customerPhone) { this.customerPhone = customerPhone; return this; }
        public Builder cashierId(Long cashierId) { this.cashierId = cashierId; return this; }
        public Builder cashierName(String cashierName) { this.cashierName = cashierName; return this; }
        public Builder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public Builder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
        public Builder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder grossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder status(OrderStatus status) { this.status = status; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder billNumber(String billNumber) { this.billNumber = billNumber; return this; }
        public Builder amountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; return this; }
        public Builder changeReturned(BigDecimal changeReturned) { this.changeReturned = changeReturned; return this; }
        public Builder balanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; return this; }
        public Builder items(List<OrderItemResponse> items) { this.items = items; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public OrderResponse build() {
            return new OrderResponse(id, orderNumber, customerId, customerName, customerPhone, cashierId, cashierName, subtotal, discountAmount, taxAmount, totalAmount, grossProfit, paymentMethod, status, notes, billNumber, amountPaid, changeReturned, balanceDue, items, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Long getCashierId() { return cashierId; }
    public void setCashierId(Long cashierId) { this.cashierId = cashierId; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getGrossProfit() { return grossProfit; }
    public void setGrossProfit(BigDecimal grossProfit) { this.grossProfit = grossProfit; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getChangeReturned() { return changeReturned; }
    public void setChangeReturned(BigDecimal changeReturned) { this.changeReturned = changeReturned; }

    public BigDecimal getBalanceDue() { return balanceDue; }
    public void setBalanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
