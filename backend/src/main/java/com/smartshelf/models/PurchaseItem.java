package com.smartshelf.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_items")
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    @JsonBackReference
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalCost;

    public PurchaseItem() {}

    public PurchaseItem(Long id, Purchase purchase, Product product, Integer quantity, BigDecimal unitCost, BigDecimal totalCost) {
        this.id = id;
        this.purchase = purchase;
        this.product = product;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = totalCost;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Purchase purchase;
        private Product product;
        private Integer quantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder purchase(Purchase purchase) { this.purchase = purchase; return this; }
        public Builder product(Product product) { this.product = product; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder unitCost(BigDecimal unitCost) { this.unitCost = unitCost; return this; }
        public Builder totalCost(BigDecimal totalCost) { this.totalCost = totalCost; return this; }

        public PurchaseItem build() {
            return new PurchaseItem(id, purchase, product, quantity, unitCost, totalCost);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Purchase getPurchase() { return purchase; }
    public void setPurchase(Purchase purchase) { this.purchase = purchase; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
}
