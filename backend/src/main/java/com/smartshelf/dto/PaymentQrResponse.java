package com.smartshelf.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentQrResponse {
    private String upiUri;
    private String payeeVpa;
    private String payeeName;
    private BigDecimal amount;
    private String transactionReference;
    private String orderNumber;
    private int expiresInSeconds;
    private String status;
    private LocalDateTime generatedAt;

    public PaymentQrResponse() {}

    public PaymentQrResponse(String upiUri, String payeeVpa, String payeeName, BigDecimal amount, String transactionReference, String orderNumber, int expiresInSeconds, String status, LocalDateTime generatedAt) {
        this.upiUri = upiUri;
        this.payeeVpa = payeeVpa;
        this.payeeName = payeeName;
        this.amount = amount;
        this.transactionReference = transactionReference;
        this.orderNumber = orderNumber;
        this.expiresInSeconds = expiresInSeconds;
        this.status = status;
        this.generatedAt = generatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String upiUri;
        private String payeeVpa;
        private String payeeName;
        private BigDecimal amount;
        private String transactionReference;
        private String orderNumber;
        private int expiresInSeconds = 300;
        private String status = "PENDING";
        private LocalDateTime generatedAt = LocalDateTime.now();

        public Builder upiUri(String upiUri) { this.upiUri = upiUri; return this; }
        public Builder payeeVpa(String payeeVpa) { this.payeeVpa = payeeVpa; return this; }
        public Builder payeeName(String payeeName) { this.payeeName = payeeName; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder transactionReference(String transactionReference) { this.transactionReference = transactionReference; return this; }
        public Builder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public Builder expiresInSeconds(int expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }

        public PaymentQrResponse build() {
            return new PaymentQrResponse(upiUri, payeeVpa, payeeName, amount, transactionReference, orderNumber, expiresInSeconds, status, generatedAt);
        }
    }

    public String getUpiUri() { return upiUri; }
    public void setUpiUri(String upiUri) { this.upiUri = upiUri; }

    public String getPayeeVpa() { return payeeVpa; }
    public void setPayeeVpa(String payeeVpa) { this.payeeVpa = payeeVpa; }

    public String getPayeeName() { return payeeName; }
    public void setPayeeName(String payeeName) { this.payeeName = payeeName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public int getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(int expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
