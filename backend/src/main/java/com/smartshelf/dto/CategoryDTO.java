package com.smartshelf.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String icon;
    private boolean active;
    private Long productCount;
    private LocalDateTime createdAt;

    public CategoryDTO() {}

    public CategoryDTO(Long id, String name, String description, String icon, boolean active, Long productCount, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.active = active;
        this.productCount = productCount;
        this.createdAt = createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String icon;
        private boolean active;
        private Long productCount;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder icon(String icon) { this.icon = icon; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder productCount(Long productCount) { this.productCount = productCount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public CategoryDTO build() {
            return new CategoryDTO(id, name, description, icon, active, productCount, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Long getProductCount() { return productCount; }
    public void setProductCount(Long productCount) { this.productCount = productCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
