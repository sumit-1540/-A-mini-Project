package com.smartshelf.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerDTO {
    private Long id;

    @NotBlank(message = "Customer name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String email;
    private String address;
    private BigDecimal totalPurchases;
    private Integer totalOrders;
    private BigDecimal creditBalance;
    private Integer rewardPoints;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CustomerDTO() {}

    public CustomerDTO(Long id, String name, String phone, String email, String address, BigDecimal totalPurchases, Integer totalOrders, BigDecimal creditBalance, Integer rewardPoints, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.totalPurchases = totalPurchases;
        this.totalOrders = totalOrders;
        this.creditBalance = creditBalance;
        this.rewardPoints = rewardPoints;
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
        private BigDecimal totalPurchases;
        private Integer totalOrders;
        private BigDecimal creditBalance;
        private Integer rewardPoints;
        private boolean active;
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

        public CustomerDTO build() {
            return new CustomerDTO(id, name, phone, email, address, totalPurchases, totalOrders, creditBalance, rewardPoints, active, createdAt, updatedAt);
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
