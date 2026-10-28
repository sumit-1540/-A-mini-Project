package com.smartshelf.dto;

import java.math.BigDecimal;

public class ParsedListItemDTO {
    private String rawText;
    private String rawInputLine;
    private String extractedProductName;
    private Integer extractedQuantity;
    private Integer requestedQuantity;
    private String extractedUnit;

    private Long matchedProductId;
    private String matchedProductName;
    private String matchedSku;
    private BigDecimal matchedPrice;
    private BigDecimal estimatedLineTotal;
    private Integer availableStock;
    private Double matchConfidence;
    private boolean found;

    public ParsedListItemDTO() {}

    public ParsedListItemDTO(String rawText, String rawInputLine, String extractedProductName, Integer extractedQuantity, Integer requestedQuantity, String extractedUnit, Long matchedProductId, String matchedProductName, String matchedSku, BigDecimal matchedPrice, BigDecimal estimatedLineTotal, Integer availableStock, Double matchConfidence, boolean found) {
        this.rawText = rawText;
        this.rawInputLine = (rawInputLine != null) ? rawInputLine : rawText;
        this.extractedProductName = extractedProductName;
        this.extractedQuantity = extractedQuantity;
        this.requestedQuantity = (requestedQuantity != null) ? requestedQuantity : extractedQuantity;
        this.extractedUnit = extractedUnit;
        this.matchedProductId = matchedProductId;
        this.matchedProductName = matchedProductName;
        this.matchedSku = matchedSku;
        this.matchedPrice = matchedPrice;
        this.estimatedLineTotal = estimatedLineTotal;
        this.availableStock = availableStock;
        this.matchConfidence = matchConfidence;
        this.found = found;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String rawText;
        private String rawInputLine;
        private String extractedProductName;
        private Integer extractedQuantity;
        private Integer requestedQuantity;
        private String extractedUnit;
        private Long matchedProductId;
        private String matchedProductName;
        private String matchedSku;
        private BigDecimal matchedPrice;
        private BigDecimal estimatedLineTotal;
        private Integer availableStock;
        private Double matchConfidence;
        private boolean found;

        public Builder rawText(String rawText) { this.rawText = rawText; this.rawInputLine = rawText; return this; }
        public Builder rawInputLine(String rawInputLine) { this.rawInputLine = rawInputLine; return this; }
        public Builder extractedProductName(String extractedProductName) { this.extractedProductName = extractedProductName; return this; }
        public Builder extractedQuantity(Integer extractedQuantity) { this.extractedQuantity = extractedQuantity; this.requestedQuantity = extractedQuantity; return this; }
        public Builder requestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; return this; }
        public Builder extractedUnit(String extractedUnit) { this.extractedUnit = extractedUnit; return this; }
        public Builder matchedProductId(Long matchedProductId) { this.matchedProductId = matchedProductId; return this; }
        public Builder matchedProductName(String matchedProductName) { this.matchedProductName = matchedProductName; return this; }
        public Builder matchedSku(String matchedSku) { this.matchedSku = matchedSku; return this; }
        public Builder matchedPrice(BigDecimal matchedPrice) { this.matchedPrice = matchedPrice; return this; }
        public Builder estimatedLineTotal(BigDecimal estimatedLineTotal) { this.estimatedLineTotal = estimatedLineTotal; return this; }
        public Builder availableStock(Integer availableStock) { this.availableStock = availableStock; return this; }
        public Builder matchConfidence(Double matchConfidence) { this.matchConfidence = matchConfidence; return this; }
        public Builder found(boolean found) { this.found = found; return this; }

        public ParsedListItemDTO build() {
            return new ParsedListItemDTO(rawText, rawInputLine, extractedProductName, extractedQuantity, requestedQuantity, extractedUnit, matchedProductId, matchedProductName, matchedSku, matchedPrice, estimatedLineTotal, availableStock, matchConfidence, found);
        }
    }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; this.rawInputLine = rawText; }

    public String getRawInputLine() { return rawInputLine != null ? rawInputLine : rawText; }
    public void setRawInputLine(String rawInputLine) { this.rawInputLine = rawInputLine; }

    public String getExtractedProductName() { return extractedProductName; }
    public void setExtractedProductName(String extractedProductName) { this.extractedProductName = extractedProductName; }

    public Integer getExtractedQuantity() { return extractedQuantity; }
    public void setExtractedQuantity(Integer extractedQuantity) { this.extractedQuantity = extractedQuantity; this.requestedQuantity = extractedQuantity; }

    public Integer getRequestedQuantity() { return requestedQuantity != null ? requestedQuantity : extractedQuantity; }
    public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public String getExtractedUnit() { return extractedUnit; }
    public void setExtractedUnit(String extractedUnit) { this.extractedUnit = extractedUnit; }

    public Long getMatchedProductId() { return matchedProductId; }
    public void setMatchedProductId(Long matchedProductId) { this.matchedProductId = matchedProductId; }

    public String getMatchedProductName() { return matchedProductName; }
    public void setMatchedProductName(String matchedProductName) { this.matchedProductName = matchedProductName; }

    public String getMatchedSku() { return matchedSku; }
    public void setMatchedSku(String matchedSku) { this.matchedSku = matchedSku; }

    public BigDecimal getMatchedPrice() { return matchedPrice; }
    public void setMatchedPrice(BigDecimal matchedPrice) { this.matchedPrice = matchedPrice; }

    public BigDecimal getEstimatedLineTotal() { return estimatedLineTotal; }
    public void setEstimatedLineTotal(BigDecimal estimatedLineTotal) { this.estimatedLineTotal = estimatedLineTotal; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public Double getMatchConfidence() { return matchConfidence; }
    public void setMatchConfidence(Double matchConfidence) { this.matchConfidence = matchConfidence; }

    public boolean isFound() { return found; }
    public void setFound(boolean found) { this.found = found; }
}
