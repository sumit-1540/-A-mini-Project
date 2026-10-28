package com.smartshelf.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class PaymentVerificationRequest {

    @NotBlank(message = "Transaction reference is required")
    private String transactionReference;

    private String utrNumber;
    private BigDecimal amount;
    private String paymentMethod = "UPI_QR";

    public PaymentVerificationRequest() {}

    public PaymentVerificationRequest(String transactionReference, String utrNumber, BigDecimal amount, String paymentMethod) {
        this.transactionReference = transactionReference;
        this.utrNumber = utrNumber;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getUtrNumber() { return utrNumber; }
    public void setUtrNumber(String utrNumber) { this.utrNumber = utrNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
