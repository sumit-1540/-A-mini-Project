package com.smartshelf.dto;

import java.util.ArrayList;
import java.util.List;

public class CsvProductImportResult {
    private int totalRows;
    private int importedCount;
    private int skippedCount;
    private List<String> errors = new ArrayList<>();
    private List<ProductResponse> importedProducts = new ArrayList<>();

    public CsvProductImportResult() {}

    public CsvProductImportResult(int totalRows, int importedCount, int skippedCount, List<String> errors, List<ProductResponse> importedProducts) {
        this.totalRows = totalRows;
        this.importedCount = importedCount;
        this.skippedCount = skippedCount;
        this.errors = (errors != null) ? errors : new ArrayList<>();
        this.importedProducts = (importedProducts != null) ? importedProducts : new ArrayList<>();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private int totalRows;
        private int importedCount;
        private int skippedCount;
        private List<String> errors = new ArrayList<>();
        private List<ProductResponse> importedProducts = new ArrayList<>();

        public Builder totalRows(int totalRows) { this.totalRows = totalRows; return this; }
        public Builder importedCount(int importedCount) { this.importedCount = importedCount; return this; }
        public Builder skippedCount(int skippedCount) { this.skippedCount = skippedCount; return this; }
        public Builder errors(List<String> errors) { this.errors = errors; return this; }
        public Builder importedProducts(List<ProductResponse> importedProducts) { this.importedProducts = importedProducts; return this; }

        public CsvProductImportResult build() {
            return new CsvProductImportResult(totalRows, importedCount, skippedCount, errors, importedProducts);
        }
    }

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }

    public int getImportedCount() { return importedCount; }
    public void setImportedCount(int importedCount) { this.importedCount = importedCount; }

    public int getSkippedCount() { return skippedCount; }
    public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public List<ProductResponse> getImportedProducts() { return importedProducts; }
    public void setImportedProducts(List<ProductResponse> importedProducts) { this.importedProducts = importedProducts; }
}
