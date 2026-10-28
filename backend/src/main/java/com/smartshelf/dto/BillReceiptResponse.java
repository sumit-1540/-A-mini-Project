package com.smartshelf.dto;

import com.smartshelf.models.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillReceiptResponse {
    private String shopName;
    private String shopAddress;
    private String shopPhone;
    private String shopGstin;

    private String billNumber;
    private String orderNumber;
    private LocalDateTime dateTime;

    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String cashierName;

    private List<OrderItemResponse> items = new ArrayList<>();

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    private PaymentMethod paymentMethod;
    private BigDecimal amountPaid;
    private BigDecimal changeReturned;
    private BigDecimal balanceDue;

    private Integer earnedLoyaltyPoints;
    private Integer totalCustomerLoyaltyPoints;
    private BigDecimal customerOutstandingUdhar;
    private String transactionReference;

    public BillReceiptResponse() {}

    public BillReceiptResponse(String shopName, String shopAddress, String shopPhone, String shopGstin, String billNumber, String orderNumber, LocalDateTime dateTime, Long customerId, String customerName, String customerPhone, String cashierName, List<OrderItemResponse> items, BigDecimal subtotal, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal totalAmount, PaymentMethod paymentMethod, BigDecimal amountPaid, BigDecimal changeReturned, BigDecimal balanceDue, Integer earnedLoyaltyPoints, Integer totalCustomerLoyaltyPoints, BigDecimal customerOutstandingUdhar) {
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopPhone = shopPhone;
        this.shopGstin = shopGstin;
        this.billNumber = billNumber;
        this.orderNumber = orderNumber;
        this.dateTime = dateTime;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.cashierName = cashierName;
        this.items = (items != null) ? items : new ArrayList<>();
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeReturned = changeReturned;
        this.balanceDue = balanceDue;
        this.earnedLoyaltyPoints = earnedLoyaltyPoints;
        this.totalCustomerLoyaltyPoints = totalCustomerLoyaltyPoints;
        this.customerOutstandingUdhar = customerOutstandingUdhar;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String shopName;
        private String shopAddress;
        private String shopPhone;
        private String shopGstin;
        private String billNumber;
        private String orderNumber;
        private LocalDateTime dateTime;
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private String cashierName;
        private List<OrderItemResponse> items = new ArrayList<>();
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private PaymentMethod paymentMethod;
        private BigDecimal amountPaid;
        private BigDecimal changeReturned;
        private BigDecimal balanceDue;
        private Integer earnedLoyaltyPoints;
        private Integer totalCustomerLoyaltyPoints;
        private BigDecimal customerOutstandingUdhar;
        private String transactionReference;

        public Builder shopName(String shopName) { this.shopName = shopName; return this; }
        public Builder shopAddress(String shopAddress) { this.shopAddress = shopAddress; return this; }
        public Builder shopPhone(String shopPhone) { this.shopPhone = shopPhone; return this; }
        public Builder shopGstin(String shopGstin) { this.shopGstin = shopGstin; return this; }
        public Builder billNumber(String billNumber) { this.billNumber = billNumber; return this; }
        public Builder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public Builder dateTime(LocalDateTime dateTime) { this.dateTime = dateTime; return this; }
        public Builder customerId(Long customerId) { this.customerId = customerId; return this; }
        public Builder customerName(String customerName) { this.customerName = customerName; return this; }
        public Builder customerPhone(String customerPhone) { this.customerPhone = customerPhone; return this; }
        public Builder cashierName(String cashierName) { this.cashierName = cashierName; return this; }
        public Builder items(List<OrderItemResponse> items) { this.items = items; return this; }
        public Builder subtotal(BigDecimal subtotal) { this.subtotal = subtotal; return this; }
        public Builder discountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; return this; }
        public Builder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder amountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; return this; }
        public Builder changeReturned(BigDecimal changeReturned) { this.changeReturned = changeReturned; return this; }
        public Builder balanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; return this; }
        public Builder earnedLoyaltyPoints(Integer earnedLoyaltyPoints) { this.earnedLoyaltyPoints = earnedLoyaltyPoints; return this; }
        public Builder totalCustomerLoyaltyPoints(Integer totalCustomerLoyaltyPoints) { this.totalCustomerLoyaltyPoints = totalCustomerLoyaltyPoints; return this; }
        public Builder customerOutstandingUdhar(BigDecimal customerOutstandingUdhar) { this.customerOutstandingUdhar = customerOutstandingUdhar; return this; }
        public Builder transactionReference(String transactionReference) { this.transactionReference = transactionReference; return this; }

        public BillReceiptResponse build() {
            BillReceiptResponse resp = new BillReceiptResponse(shopName, shopAddress, shopPhone, shopGstin, billNumber, orderNumber, dateTime, customerId, customerName, customerPhone, cashierName, items, subtotal, discountAmount, taxAmount, totalAmount, paymentMethod, amountPaid, changeReturned, balanceDue, earnedLoyaltyPoints, totalCustomerLoyaltyPoints, customerOutstandingUdhar);
            resp.setTransactionReference(transactionReference);
            return resp;
        }
    }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getShopAddress() { return shopAddress; }
    public void setShopAddress(String shopAddress) { this.shopAddress = shopAddress; }

    public String getShopPhone() { return shopPhone; }
    public void setShopPhone(String shopPhone) { this.shopPhone = shopPhone; }

    public String getShopGstin() { return shopGstin; }
    public void setShopGstin(String shopGstin) { this.shopGstin = shopGstin; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmountPaid() { return amountPaid; }
    public void setAmountPaid(BigDecimal amountPaid) { this.amountPaid = amountPaid; }

    public BigDecimal getChangeReturned() { return changeReturned; }
    public void setChangeReturned(BigDecimal changeReturned) { this.changeReturned = changeReturned; }

    public BigDecimal getBalanceDue() { return balanceDue; }
    public void setBalanceDue(BigDecimal balanceDue) { this.balanceDue = balanceDue; }

    public Integer getEarnedLoyaltyPoints() { return earnedLoyaltyPoints; }
    public void setEarnedLoyaltyPoints(Integer earnedLoyaltyPoints) { this.earnedLoyaltyPoints = earnedLoyaltyPoints; }

    public Integer getTotalCustomerLoyaltyPoints() { return totalCustomerLoyaltyPoints; }
    public void setTotalCustomerLoyaltyPoints(Integer totalCustomerLoyaltyPoints) { this.totalCustomerLoyaltyPoints = totalCustomerLoyaltyPoints; }

    public BigDecimal getCustomerOutstandingUdhar() { return customerOutstandingUdhar; }
    public void setCustomerOutstandingUdhar(BigDecimal customerOutstandingUdhar) { this.customerOutstandingUdhar = customerOutstandingUdhar; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
}
