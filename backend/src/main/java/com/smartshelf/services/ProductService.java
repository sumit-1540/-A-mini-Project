package com.smartshelf.services;

import com.opencsv.CSVReader;
import com.smartshelf.dto.CsvProductImportResult;
import com.smartshelf.dto.ProductRequest;
import com.smartshelf.dto.ProductResponse;
import com.smartshelf.exceptions.DuplicateResourceException;
import com.smartshelf.exceptions.ResourceNotFoundException;
import com.smartshelf.models.*;
import com.smartshelf.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final StockAlertService stockAlertService;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, SupplierRepository supplierRepository, InventoryRepository inventoryRepository, InventoryTransactionRepository inventoryTransactionRepository, StockAlertService stockAlertService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.stockAlertService = stockAlertService;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(String query, Long categoryId, Pageable pageable) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        Page<Product> productPage;
        if (cleanQuery == null && categoryId == null) {
            productPage = productRepository.findByActiveTrue(pageable);
        } else if (cleanQuery == null) {
            productPage = productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        } else if (categoryId == null) {
            productPage = productRepository.searchActiveProducts(cleanQuery, pageable);
        } else {
            productPage = productRepository.searchActiveProductsByCategory(cleanQuery, categoryId, pageable);
        }
        return productPage.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchActiveProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return productRepository.findByActiveTrue().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        return productRepository.searchActiveProducts(query.trim()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcode));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku().trim())) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }

        if (request.getBarcode() != null && !request.getBarcode().trim().isEmpty() &&
                productRepository.existsByBarcode(request.getBarcode().trim())) {
            throw new DuplicateResourceException("Product with Barcode '" + request.getBarcode() + "' already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId()).orElse(null);
        }

        Product product = Product.builder()
                .name(request.getName().trim())
                .sku(request.getSku().trim().toUpperCase())
                .barcode(request.getBarcode() != null && !request.getBarcode().trim().isEmpty() ? request.getBarcode().trim() : null)
                .category(category)
                .brand(request.getBrand())
                .unit(request.getUnit() != null ? request.getUnit() : "pcs")
                .purchasePrice(request.getPurchasePrice())
                .sellingPrice(request.getSellingPrice())
                .currentStock(request.getInitialStock() != null ? request.getInitialStock() : 0)
                .minimumStock(request.getMinimumStock() != null ? request.getMinimumStock() : 5)
                .expiryDate(request.getExpiryDate())
                .supplier(supplier)
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .active(true)
                .build();

        product = productRepository.save(product);

        // Create Inventory record
        Inventory inventory = Inventory.builder()
                .product(product)
                .currentStock(product.getCurrentStock())
                .minimumStock(product.getMinimumStock())
                .status(calculateStatus(product.getCurrentStock(), product.getMinimumStock(), product.getExpiryDate()))
                .aisle(request.getAisle())
                .shelf(request.getShelf())
                .build();
        inventoryRepository.save(inventory);

        // Record Initial Stock Transaction
        if (product.getCurrentStock() > 0) {
            InventoryTransaction tx = InventoryTransaction.builder()
                    .product(product)
                    .type(TransactionType.RESTOCK)
                    .quantityChange(product.getCurrentStock())
                    .previousStock(0)
                    .newStock(product.getCurrentStock())
                    .reason("Initial stock addition")
                    .performedBy("ADMIN")
                    .build();
            inventoryTransactionRepository.save(tx);
        }

        // Trigger Alerts if needed
        stockAlertService.checkAndTriggerStockAlert(product);
        stockAlertService.checkAndTriggerExpiryAlert(product);

        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getSku().equalsIgnoreCase(request.getSku().trim()) &&
                productRepository.existsBySku(request.getSku().trim())) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }

        if (request.getBarcode() != null && !request.getBarcode().trim().isEmpty() &&
                !request.getBarcode().trim().equals(product.getBarcode()) &&
                productRepository.existsByBarcode(request.getBarcode().trim())) {
            throw new DuplicateResourceException("Product with Barcode '" + request.getBarcode() + "' already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId()).orElse(null);
        }

        product.setName(request.getName().trim());
        product.setSku(request.getSku().trim().toUpperCase());
        product.setBarcode(request.getBarcode() != null && !request.getBarcode().trim().isEmpty() ? request.getBarcode().trim() : null);
        product.setCategory(category);
        product.setBrand(request.getBrand());
        product.setUnit(request.getUnit() != null ? request.getUnit() : "pcs");
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setMinimumStock(request.getMinimumStock());
        product.setExpiryDate(request.getExpiryDate());
        product.setSupplier(supplier);
        product.setDescription(request.getDescription());
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        product = productRepository.save(product);

        // Update Inventory record
        Optional<Inventory> optInv = inventoryRepository.findByProductId(product.getId());
        if (optInv.isPresent()) {
            Inventory inv = optInv.get();
            inv.setMinimumStock(product.getMinimumStock());
            inv.setStatus(calculateStatus(product.getCurrentStock(), product.getMinimumStock(), product.getExpiryDate()));
            if (request.getAisle() != null) inv.setAisle(request.getAisle());
            if (request.getShelf() != null) inv.setShelf(request.getShelf());
            inventoryRepository.save(inv);
        }

        stockAlertService.checkAndTriggerStockAlert(product);
        stockAlertService.checkAndTriggerExpiryAlert(product);

        return mapToResponse(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public CsvProductImportResult importProductsFromCsv(MultipartFile file) {
        CsvProductImportResult result = new CsvProductImportResult();
        if (file.isEmpty()) {
            result.getErrors().add("Uploaded file is empty");
            return result;
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = reader.readNext();
            if (headers == null) {
                result.getErrors().add("CSV file has no header row");
                return result;
            }

            String[] line;
            int rowNum = 1;
            while ((line = reader.readNext()) != null) {
                rowNum++;
                result.setTotalRows(result.getTotalRows() + 1);
                try {
                    if (line.length < 5 || line[0].trim().isEmpty()) {
                        result.setSkippedCount(result.getSkippedCount() + 1);
                        continue;
                    }

                    String name = line[0].trim();
                    String categoryName = line.length > 1 ? line[1].trim() : "Grocery";
                    String sku = line.length > 2 && !line[2].trim().isEmpty() ? line[2].trim() : "SKU-" + System.currentTimeMillis() + "-" + rowNum;
                    String barcode = line.length > 3 && !line[3].trim().isEmpty() ? line[3].trim() : null;
                    BigDecimal purchasePrice = line.length > 4 && !line[4].trim().isEmpty() ? new BigDecimal(line[4].trim()) : BigDecimal.ZERO;
                    BigDecimal sellingPrice = line.length > 5 && !line[5].trim().isEmpty() ? new BigDecimal(line[5].trim()) : purchasePrice.multiply(BigDecimal.valueOf(1.2));
                    int stock = line.length > 6 && !line[6].trim().isEmpty() ? Integer.parseInt(line[6].trim()) : 0;
                    int minStock = line.length > 7 && !line[7].trim().isEmpty() ? Integer.parseInt(line[7].trim()) : 5;
                    
                    LocalDate expiry = null;
                    if (line.length > 8 && !line[8].trim().isEmpty()) {
                        try {
                            expiry = LocalDate.parse(line[8].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                        } catch (Exception ignored) {}
                    }

                    String supplierName = line.length > 9 ? line[9].trim() : null;
                    String unit = line.length > 10 && !line[10].trim().isEmpty() ? line[10].trim() : "pcs";
                    String brand = line.length > 11 ? line[11].trim() : "";

                    Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                            .orElseGet(() -> categoryRepository.save(Category.builder()
                                    .name(categoryName)
                                    .description("Auto-created from CSV import")
                                    .active(true)
                                    .build()));

                    Supplier supplier = null;
                    if (supplierName != null && !supplierName.isEmpty()) {
                        supplier = supplierRepository.findByNameIgnoreCase(supplierName)
                                .orElseGet(() -> supplierRepository.save(Supplier.builder()
                                        .name(supplierName)
                                        .company(supplierName)
                                        .active(true)
                                        .build()));
                    }

                    Optional<Product> existingProduct = productRepository.findBySku(sku);
                    Product product;
                    if (existingProduct.isPresent()) {
                        product = existingProduct.get();
                        product.setCurrentStock(product.getCurrentStock() + stock);
                        product.setPurchasePrice(purchasePrice);
                        product.setSellingPrice(sellingPrice);
                        product = productRepository.save(product);
                    } else {
                        product = Product.builder()
                                .name(name)
                                .sku(sku.toUpperCase())
                                .barcode(barcode)
                                .category(category)
                                .brand(brand)
                                .unit(unit)
                                .purchasePrice(purchasePrice)
                                .sellingPrice(sellingPrice)
                                .currentStock(stock)
                                .minimumStock(minStock)
                                .expiryDate(expiry)
                                .supplier(supplier)
                                .active(true)
                                .build();
                        product = productRepository.save(product);

                        Inventory inv = Inventory.builder()
                                .product(product)
                                .currentStock(product.getCurrentStock())
                                .minimumStock(product.getMinimumStock())
                                .status(calculateStatus(product.getCurrentStock(), product.getMinimumStock(), product.getExpiryDate()))
                                .build();
                        inventoryRepository.save(inv);
                    }

                    result.setImportedCount(result.getImportedCount() + 1);
                    result.getImportedProducts().add(mapToResponse(product));

                } catch (Exception e) {
                    result.setSkippedCount(result.getSkippedCount() + 1);
                    result.getErrors().add("Row " + rowNum + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            result.getErrors().add("Failed to process CSV file: " + e.getMessage());
        }

        return result;
    }

    public InventoryStatus calculateStatus(Integer currentStock, Integer minStock, LocalDate expiryDate) {
        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            return InventoryStatus.EXPIRED;
        }
        if (currentStock == null || currentStock <= 0) {
            return InventoryStatus.CRITICAL;
        }
        if (minStock != null && currentStock <= minStock) {
            return InventoryStatus.LOW;
        }
        return InventoryStatus.HEALTHY;
    }

    public ProductResponse mapToResponse(Product product) {
        Long daysUntilExpiry = null;
        if (product.getExpiryDate() != null) {
            daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpiryDate());
        }

        String aisle = null;
        String shelf = null;
        Optional<Inventory> inv = inventoryRepository.findByProductId(product.getId());
        if (inv.isPresent()) {
            aisle = inv.get().getAisle();
            shelf = inv.get().getShelf();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .brand(product.getBrand())
                .unit(product.getUnit())
                .purchasePrice(product.getPurchasePrice())
                .sellingPrice(product.getSellingPrice())
                .currentStock(product.getCurrentStock())
                .minimumStock(product.getMinimumStock())
                .stockStatus(calculateStatus(product.getCurrentStock(), product.getMinimumStock(), product.getExpiryDate()))
                .expiryDate(product.getExpiryDate())
                .daysUntilExpiry(daysUntilExpiry)
                .supplierId(product.getSupplier() != null ? product.getSupplier().getId() : null)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getName() : null)
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .aisle(aisle)
                .shelf(shelf)
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
