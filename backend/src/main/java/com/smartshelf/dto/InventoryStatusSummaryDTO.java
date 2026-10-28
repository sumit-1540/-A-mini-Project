package com.smartshelf.dto;

public class InventoryStatusSummaryDTO {
    private Long healthyCount;
    private Long lowStockCount;
    private Long criticalCount;
    private Long expiredCount;
    private Long totalProducts;

    public InventoryStatusSummaryDTO() {}

    public InventoryStatusSummaryDTO(Long healthyCount, Long lowStockCount, Long criticalCount, Long expiredCount, Long totalProducts) {
        this.healthyCount = healthyCount;
        this.lowStockCount = lowStockCount;
        this.criticalCount = criticalCount;
        this.expiredCount = expiredCount;
        this.totalProducts = totalProducts;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long healthyCount;
        private Long lowStockCount;
        private Long criticalCount;
        private Long expiredCount;
        private Long totalProducts;

        public Builder healthyCount(Long healthyCount) { this.healthyCount = healthyCount; return this; }
        public Builder lowStockCount(Long lowStockCount) { this.lowStockCount = lowStockCount; return this; }
        public Builder criticalCount(Long criticalCount) { this.criticalCount = criticalCount; return this; }
        public Builder expiredCount(Long expiredCount) { this.expiredCount = expiredCount; return this; }
        public Builder totalProducts(Long totalProducts) { this.totalProducts = totalProducts; return this; }

        public InventoryStatusSummaryDTO build() {
            return new InventoryStatusSummaryDTO(healthyCount, lowStockCount, criticalCount, expiredCount, totalProducts);
        }
    }

    public Long getHealthyCount() { return healthyCount; }
    public void setHealthyCount(Long healthyCount) { this.healthyCount = healthyCount; }

    public Long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(Long lowStockCount) { this.lowStockCount = lowStockCount; }

    public Long getCriticalCount() { return criticalCount; }
    public void setCriticalCount(Long criticalCount) { this.criticalCount = criticalCount; }

    public Long getExpiredCount() { return expiredCount; }
    public void setExpiredCount(Long expiredCount) { this.expiredCount = expiredCount; }

    public Long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Long totalProducts) { this.totalProducts = totalProducts; }
}
