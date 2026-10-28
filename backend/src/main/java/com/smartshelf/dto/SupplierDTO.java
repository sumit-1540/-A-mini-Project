package com.smartshelf.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class SupplierDTO {
    private Long id;

    @NotBlank(message = "Supplier name is required")
    private String name;

    private String company;
    private String phone;
    private String email;
    private String address;
    private String gstNumber;
    private boolean active;
    private Long suppliedProductCount;
    private LocalDateTime createdAt;

    public SupplierDTO() {}

    public SupplierDTO(Long id, String name, String company, String phone, String email, String address, String gstNumber, boolean active, Long suppliedProductCount, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.gstNumber = gstNumber;
        this.active = active;
        this.suppliedProductCount = suppliedProductCount;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String company;
        private String phone;
        private String email;
        private String address;
        private String gstNumber;
        private boolean active;
        private Long suppliedProductCount;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder company(String company) { this.company = company; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder gstNumber(String gstNumber) { this.gstNumber = gstNumber; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder suppliedProductCount(Long suppliedProductCount) { this.suppliedProductCount = suppliedProductCount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public SupplierDTO build() {
            return new SupplierDTO(id, name, company, phone, email, address, gstNumber, active, suppliedProductCount, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Long getSuppliedProductCount() { return suppliedProductCount; }
    public void setSuppliedProductCount(Long suppliedProductCount) { this.suppliedProductCount = suppliedProductCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
