package com.smartshelf.dto;

import com.smartshelf.models.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class CreateOrderRequest {

    private Long customerId;

    @NotEmpty(message = "Cart cannot be empty")
    @Valid
    private List<OrderItemRequest> items;

    private BigDecimal discountAmount;
    private BigDecimal taxAmount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private BigDecimal amountPaid;
    private String transactionReference;
    private String notes;
    private Integer redeemRewardPoints;

    public CreateOrderRequest() {}

    public CreateOrderRequest(Long customerId, List<OrderItemRequest> items, BigDecimal discountAmount, BigDecimal taxAmount, PaymentMethod paymentMethod, BigDecimal amountPaid, String transactionReference, String notes, Integer redeemRewardPoints) {
        this.customerId = customerId;
        this.items = items;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.transactionReference = transactionReference;
        this.notes = notes;
        this.redeemRewardPoints = redeemRewardPoints;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long customerId;
        private List<OrderItemRequest> items;
        private BigDecimal discountAmount;
        private BigDecimal taxAmount;
        private PaymentMethod paymentMethod;
        private BigDecimal amountPaid;
        private String transactionReference;
        private String notes;
        private Integer redeemRewardPoints;

        public Builder customerId(Long customerId) { this.customerId = customerId; return this; }
        public Builder items(List<OrderItemRequest> items) { this.items = items; return this; }
        public Builder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
        public Builder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder amountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; return this; }
        public Builder transactionReference(String transactionReference) { this.transactionReference = transactionReference; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder redeemRewardPoints(Integer redeemRewardPoints) { this.redeemRewardPoints = redeemRewardPoints; return this; }

        public CreateOrderRequest build() {
            return new CreateOrderRequest(customerId, items, discountAmount, taxAmount, paymentMethod, amountPaid, transactionReference, notes, redeemRewardPoints);
        }
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getRedeemRewardPoints() { return redeemRewardPoints; }
    public void setRedeemRewardPoints(Integer redeemRewardPoints) { this.redeemRewardPoints = redeemRewardPoints; }
}
