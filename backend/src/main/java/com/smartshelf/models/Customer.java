package com.smartshelf.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_phone", columnList = "phone"),
        @Index(name = "idx_customer_name", columnList = "name")
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPurchases = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer totalOrders = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal creditBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer rewardPoints = 0;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Customer() {}

    public Customer(Long id, String name, String phone, String email, String address, BigDecimal totalPurchases, Integer totalOrders, BigDecimal creditBalance, Integer rewardPoints, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.totalPurchases = totalPurchases != null ? totalPurchases : BigDecimal.ZERO;
        this.totalOrders = totalOrders != null ? totalOrders : 0;
        this.creditBalance = creditBalance != null ? creditBalance : BigDecimal.ZERO;
        this.rewardPoints = rewardPoints != null ? rewardPoints : 0;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private String address;
        private BigDecimal totalPurchases = BigDecimal.ZERO;
        private Integer totalOrders = 0;
        private BigDecimal creditBalance = BigDecimal.ZERO;
        private Integer rewardPoints = 0;
        private boolean active = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder totalPurchases(BigDecimal totalPurchases) { this.totalPurchases = totalPurchases; return this; }
        public Builder totalOrders(Integer totalOrders) { this.totalOrders = totalOrders; return this; }
        public Builder creditBalance(BigDecimal creditBalance) { this.creditBalance = creditBalance; return this; }
        public Builder rewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Customer build() {
            return new Customer(id, name, phone, email, address, totalPurchases, totalOrders, creditBalance, rewardPoints, active, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getTotalPurchases() { return totalPurchases; }
    public void setTotalPurchases(BigDecimal totalPurchases) { this.totalPurchases = totalPurchases; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public BigDecimal getCreditBalance() { return creditBalance; }
    public void setCreditBalance(BigDecimal creditBalance) { this.creditBalance = creditBalance; }

    public Integer getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
