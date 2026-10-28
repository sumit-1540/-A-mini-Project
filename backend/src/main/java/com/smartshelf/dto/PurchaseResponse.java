package com.smartshelf.dto;

import com.smartshelf.models.PurchaseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseResponse {
    private Long id;
    private String invoiceNumber;
    private Long supplierId;
    private String supplierName;
    private String supplierCompany;
    private LocalDate purchaseDate;
    private LocalDate receivedDate;
    private PurchaseStatus status;
    private BigDecimal totalAmount;
    private String notes;
    private List<PurchaseItemResponse> items = new ArrayList<>();
    private LocalDateTime createdAt;

    public PurchaseResponse() {}

    public PurchaseResponse(Long id, String invoiceNumber, Long supplierId, String supplierName, String supplierCompany, LocalDate purchaseDate, LocalDate receivedDate, PurchaseStatus status, BigDecimal totalAmount, String notes, List<PurchaseItemResponse> items, LocalDateTime createdAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierCompany = supplierCompany;
        this.purchaseDate = purchaseDate;
        this.receivedDate = receivedDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.notes = notes;
        this.items = (items != null) ? items : new ArrayList<>();
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String invoiceNumber;
        private Long supplierId;
        private String supplierName;
        private String supplierCompany;
        private LocalDate purchaseDate;
        private LocalDate receivedDate;
        private PurchaseStatus status;
        private BigDecimal totalAmount;
        private String notes;
        private List<PurchaseItemResponse> items = new ArrayList<>();
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }
        public Builder supplierCompany(String supplierCompany) { this.supplierCompany = supplierCompany; return this; }
        public Builder purchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public Builder receivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; return this; }
        public Builder status(PurchaseStatus status) { this.status = status; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder items(List<PurchaseItemResponse> items) { this.items = items; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PurchaseResponse build() {
            return new PurchaseResponse(id, invoiceNumber, supplierId, supplierName, supplierCompany, purchaseDate, receivedDate, status, totalAmount, notes, items, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSupplierCompany() { return supplierCompany; }
    public void setSupplierCompany(String supplierCompany) { this.supplierCompany = supplierCompany; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public PurchaseStatus getStatus() { return status; }
    public void setStatus(PurchaseStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<PurchaseItemResponse> getItems() { return items; }
    public void setItems(List<PurchaseItemResponse> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
