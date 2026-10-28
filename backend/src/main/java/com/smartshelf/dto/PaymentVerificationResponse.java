package com.smartshelf.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentVerificationResponse {
    private String status; // "SUCCESS", "PENDING", "FAILED"
    private String transactionReference;
    private String utrNumber;
    private BigDecimal amount;
    private LocalDateTime verifiedAt;
    private String message;

    public PaymentVerificationResponse() {}

    public PaymentVerificationResponse(String status, String transactionReference, String utrNumber, BigDecimal amount, LocalDateTime verifiedAt, String message) {
        this.status = status;
        this.transactionReference = transactionReference;
        this.utrNumber = utrNumber;
        this.amount = amount;
        this.verifiedAt = verifiedAt;
        this.message = message;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String status = "SUCCESS";
        private String transactionReference;
        private String utrNumber;
        private BigDecimal amount;
        private LocalDateTime verifiedAt = LocalDateTime.now();
        private String message;

        public Builder status(String status) { this.status = status; return this; }
        public Builder transactionReference(String transactionReference) { this.transactionReference = transactionReference; return this; }
        public Builder utrNumber(String utrNumber) { this.utrNumber = utrNumber; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder verifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; return this; }
        public Builder message(String message) { this.message = message; return this; }

        public PaymentVerificationResponse build() {
            return new PaymentVerificationResponse(status, transactionReference, utrNumber, amount, verifiedAt, message);
        }
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getUtrNumber() { return utrNumber; }
    public void setUtrNumber(String utrNumber) { this.utrNumber = utrNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
