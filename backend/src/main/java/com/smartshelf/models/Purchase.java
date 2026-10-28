package com.smartshelf.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchases", indexes = {
        @Index(name = "idx_purchases_invoice", columnList = "invoiceNumber"),
        @Index(name = "idx_purchases_supplier", columnList = "supplier_id")
})
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    private LocalDate receivedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseStatus status = PurchaseStatus.ORDERED;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<PurchaseItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Purchase() {}

    public Purchase(Long id, String invoiceNumber, Supplier supplier, LocalDate purchaseDate, LocalDate receivedDate, PurchaseStatus status, BigDecimal totalAmount, String notes, List<PurchaseItem> items, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.supplier = supplier;
        this.purchaseDate = purchaseDate;
        this.receivedDate = receivedDate;
        this.status = (status != null) ? status : PurchaseStatus.ORDERED;
        this.totalAmount = (totalAmount != null) ? totalAmount : BigDecimal.ZERO;
        this.notes = notes;
        this.items = (items != null) ? items : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String invoiceNumber;
        private Supplier supplier;
        private LocalDate purchaseDate;
        private LocalDate receivedDate;
        private PurchaseStatus status = PurchaseStatus.ORDERED;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private String notes;
        private List<PurchaseItem> items = new ArrayList<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder invoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; return this; }
        public Builder supplier(Supplier supplier) { this.supplier = supplier; return this; }
        public Builder purchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; return this; }
        public Builder receivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; return this; }
        public Builder status(PurchaseStatus status) { this.status = status; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder items(List<PurchaseItem> items) { this.items = items; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Purchase build() {
            return new Purchase(id, invoiceNumber, supplier, purchaseDate, receivedDate, status, totalAmount, notes, items, createdAt, updatedAt);
        }
    }

    public void addItem(PurchaseItem item) {
        items.add(item);
        item.setPurchase(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

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

    public List<PurchaseItem> getItems() { return items; }
    public void setItems(List<PurchaseItem> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
