package com.smartshelf.dto;

import java.math.BigDecimal;

public class SimulationRequest {
    private String productName;
    private String categoryName;
    private Integer currentStock;
    private Integer reorderLevel;
    private Integer reorderQuantity;
    private BigDecimal unitPrice;
    private Integer turnoverRate;
    private Integer shelfLifeDays;

    public SimulationRequest() {}

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public Integer getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(Integer reorderQuantity) { this.reorderQuantity = reorderQuantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getTurnoverRate() { return turnoverRate; }
    public void setTurnoverRate(Integer turnoverRate) { this.turnoverRate = turnoverRate; }

    public Integer getShelfLifeDays() { return shelfLifeDays; }
    public void setShelfLifeDays(Integer shelfLifeDays) { this.shelfLifeDays = shelfLifeDays; }
}
