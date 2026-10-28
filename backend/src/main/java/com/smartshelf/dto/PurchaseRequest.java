package com.smartshelf.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class PurchaseRequest {

    private String invoiceNumber;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;

    private String notes;

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<PurchaseItemRequest> items;

    public PurchaseRequest() {}

    public PurchaseRequest(String invoiceNumber, Long supplierId, LocalDate purchaseDate, String notes, List<PurchaseItemRequest> items) {
        this.invoiceNumber = invoiceNumber;
        this.supplierId = supplierId;
        this.purchaseDate = purchaseDate;
        this.notes = notes;
        this.items = items;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String invoiceNumber;
        private Long supplierId;
        private LocalDate purchaseDate;
        private String notes;
        private List<PurchaseItemRequest> items;

        public Builder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder purchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder items(List<PurchaseItemRequest> items) { this.items = items; return this; }

        public PurchaseRequest build() {
            return new PurchaseRequest(invoiceNumber, supplierId, purchaseDate, notes, items);
        }
    }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<PurchaseItemRequest> getItems() { return items; }
    public void setItems(List<PurchaseItemRequest> items) { this.items = items; }
}
