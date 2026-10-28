package com.smartshelf.services;

import com.smartshelf.dto.InventoryResponse;
import com.smartshelf.dto.InventoryTransactionResponse;
import com.smartshelf.dto.StockAdjustmentRequest;
import com.smartshelf.exceptions.BadRequestException;
import com.smartshelf.exceptions.ResourceNotFoundException;
import com.smartshelf.models.*;
import com.smartshelf.repositories.InventoryRepository;
import com.smartshelf.repositories.InventoryTransactionRepository;
import com.smartshelf.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final StockAlertService stockAlertService;

    public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository, InventoryTransactionRepository inventoryTransactionRepository, StockAlertService stockAlertService) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.stockAlertService = stockAlertService;
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAllByOrderByCurrentStockAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockInventory() {
        return productRepository.findLowStockProducts().stream()
                .map(p -> inventoryRepository.findByProductId(p.getId()).orElse(null))
                .filter(inv -> inv != null)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getExpiringInventory(int days) {
        LocalDate threshold = LocalDate.now().plusDays(days);
        return productRepository.findExpiringProducts(threshold).stream()
                .map(p -> inventoryRepository.findByProductId(p.getId()).orElse(null))
                .filter(inv -> inv != null)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
        return mapToResponse(inventory);
    }

    @Transactional
    public InventoryResponse adjustStock(StockAdjustmentRequest request, String performedBy) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        int oldStock = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
        int newStock = oldStock + request.getQuantityChange();

        if (newStock < 0) {
            throw new BadRequestException("Stock adjustment cannot result in negative stock (Current: " + oldStock + ", Adjustment: " + request.getQuantityChange() + ")");
        }

        product.setCurrentStock(newStock);
        final Product savedProduct = productRepository.save(product);

        Inventory inventory = inventoryRepository.findByProductId(savedProduct.getId())
                .orElseGet(() -> Inventory.builder().product(savedProduct).build());

        inventory.setCurrentStock(newStock);
        inventory.setMinimumStock(savedProduct.getMinimumStock());
        inventory.setStatus(calculateStatus(newStock, savedProduct.getMinimumStock(), savedProduct.getExpiryDate()));
        inventory = inventoryRepository.save(inventory);

        // Record transaction
        InventoryTransaction tx = InventoryTransaction.builder()
                .product(savedProduct)
                .type(request.getType() != null ? request.getType() : TransactionType.ADJUSTMENT_MANUAL)
                .quantityChange(request.getQuantityChange())
                .previousStock(oldStock)
                .newStock(newStock)
                .reason(request.getReason() != null ? request.getReason() : "Manual stock adjustment")
                .performedBy(performedBy != null ? performedBy : "ADMIN")
                .build();
        inventoryTransactionRepository.save(tx);

        stockAlertService.checkAndTriggerStockAlert(savedProduct);
        stockAlertService.checkAndTriggerExpiryAlert(savedProduct);

        return mapToResponse(inventory);
    }

    public List<InventoryTransactionResponse> getStockHistory(Long productId) {
        if (productId != null) {
            return inventoryTransactionRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                    .map(this::mapTransactionToResponse)
                    .collect(Collectors.toList());
        }
        return inventoryTransactionRepository.findTop50ByOrderByCreatedAtDesc().stream()
                .map(this::mapTransactionToResponse)
                .collect(Collectors.toList());
    }

    private InventoryStatus calculateStatus(Integer currentStock, Integer minStock, LocalDate expiryDate) {
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

    public InventoryResponse mapToResponse(Inventory inventory) {
        Product product = inventory.getProduct();
        Long daysUntilExpiry = null;
        if (product.getExpiryDate() != null) {
            daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpiryDate());
        }

        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(product.getId())
                .productName(product.getName())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .unit(product.getUnit())
                .purchasePrice(product.getPurchasePrice())
                .sellingPrice(product.getSellingPrice())
                .currentStock(product.getCurrentStock())
                .minimumStock(product.getMinimumStock())
                .status(calculateStatus(product.getCurrentStock(), product.getMinimumStock(), product.getExpiryDate()))
                .aisle(inventory.getAisle())
                .shelf(inventory.getShelf())
                .expiryDate(product.getExpiryDate())
                .daysUntilExpiry(daysUntilExpiry)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getName() : null)
                .lastRestockedAt(inventory.getLastRestockedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    public InventoryTransactionResponse mapTransactionToResponse(InventoryTransaction tx) {
        return InventoryTransactionResponse.builder()
                .id(tx.getId())
                .productId(tx.getProduct().getId())
                .productName(tx.getProduct().getName())
                .sku(tx.getProduct().getSku())
                .type(tx.getType())
                .quantityChange(tx.getQuantityChange())
                .previousStock(tx.getPreviousStock())
                .newStock(tx.getNewStock())
                .reason(tx.getReason())
                .referenceId(tx.getReferenceId())
                .performedBy(tx.getPerformedBy())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
