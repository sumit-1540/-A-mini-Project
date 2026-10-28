package com.smartshelf.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HandwrittenListParseResponse {
    private String originalFileName;
    private String rawExtractedText;
    private int totalExtractedItems;
    private int successfullyMatchedItems;
    private BigDecimal estimatedTotal;
    private List<ParsedListItemDTO> items = new ArrayList<>();
    private List<ParsedListItemDTO> matchedItems = new ArrayList<>();
    private List<String> unmatchedLines = new ArrayList<>();
    private String aiModelUsed;

    public HandwrittenListParseResponse() {}

    public HandwrittenListParseResponse(String originalFileName, String rawExtractedText, int totalExtractedItems, int successfullyMatchedItems, BigDecimal estimatedTotal, List<ParsedListItemDTO> items, List<String> unmatchedLines, String aiModelUsed) {
        this.originalFileName = originalFileName;
        this.rawExtractedText = rawExtractedText;
        this.totalExtractedItems = totalExtractedItems;
        this.successfullyMatchedItems = successfullyMatchedItems;
        this.estimatedTotal = estimatedTotal;
        this.items = (items != null) ? items : new ArrayList<>();
        this.matchedItems = this.items;
        this.unmatchedLines = (unmatchedLines != null) ? unmatchedLines : new ArrayList<>();
        this.aiModelUsed = aiModelUsed != null ? aiModelUsed : "Google Gemini 1.5 Flash Vision AI";
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String originalFileName;
        private String rawExtractedText;
        private int totalExtractedItems;
        private int successfullyMatchedItems;
        private BigDecimal estimatedTotal;
        private List<ParsedListItemDTO> items = new ArrayList<>();
        private List<String> unmatchedLines = new ArrayList<>();
        private String aiModelUsed;

        public Builder originalFileName(String originalFileName) { this.originalFileName = originalFileName; return this; }
        public Builder rawExtractedText(String rawExtractedText) { this.rawExtractedText = rawExtractedText; return this; }
        public Builder totalExtractedItems(int totalExtractedItems) { this.totalExtractedItems = totalExtractedItems; return this; }
        public Builder successfullyMatchedItems(int successfullyMatchedItems) { this.successfullyMatchedItems = successfullyMatchedItems; return this; }
        public Builder estimatedTotal(BigDecimal estimatedTotal) { this.estimatedTotal = estimatedTotal; return this; }
        public Builder items(List<ParsedListItemDTO> items) { this.items = items; return this; }
        public Builder unmatchedLines(List<String> unmatchedLines) { this.unmatchedLines = unmatchedLines; return this; }
        public Builder aiModelUsed(String aiModelUsed) { this.aiModelUsed = aiModelUsed; return this; }

        public HandwrittenListParseResponse build() {
            return new HandwrittenListParseResponse(originalFileName, rawExtractedText, totalExtractedItems, successfullyMatchedItems, estimatedTotal, items, unmatchedLines, aiModelUsed);
        }
    }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getRawExtractedText() { return rawExtractedText; }
    public void setRawExtractedText(String rawExtractedText) { this.rawExtractedText = rawExtractedText; }

    public int getTotalExtractedItems() { return totalExtractedItems; }
    public void setTotalExtractedItems(int totalExtractedItems) { this.totalExtractedItems = totalExtractedItems; }

    public int getSuccessfullyMatchedItems() { return successfullyMatchedItems; }
    public void setSuccessfullyMatchedItems(int successfullyMatchedItems) { this.successfullyMatchedItems = successfullyMatchedItems; }

    public BigDecimal getEstimatedTotal() { return estimatedTotal; }
    public void setEstimatedTotal(BigDecimal estimatedTotal) { this.estimatedTotal = estimatedTotal; }

    public List<ParsedListItemDTO> getItems() { return items; }
    public void setItems(List<ParsedListItemDTO> items) {
        this.items = items;
        this.matchedItems = items;
    }

    public List<ParsedListItemDTO> getMatchedItems() { return matchedItems != null ? matchedItems : items; }
    public void setMatchedItems(List<ParsedListItemDTO> matchedItems) {
        this.matchedItems = matchedItems;
        this.items = matchedItems;
    }

    public List<String> getUnmatchedLines() { return unmatchedLines; }
    public void setUnmatchedLines(List<String> unmatchedLines) { this.unmatchedLines = unmatchedLines; }

    public String getAiModelUsed() { return aiModelUsed; }
    public void setAiModelUsed(String aiModelUsed) { this.aiModelUsed = aiModelUsed; }
}
