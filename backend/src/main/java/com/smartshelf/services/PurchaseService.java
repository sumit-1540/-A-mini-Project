package com.smartshelf.services;

import com.smartshelf.dto.PurchaseItemRequest;
import com.smartshelf.dto.PurchaseItemResponse;
import com.smartshelf.dto.PurchaseRequest;
import com.smartshelf.dto.PurchaseResponse;
import com.smartshelf.exceptions.BadRequestException;
import com.smartshelf.exceptions.ResourceNotFoundException;
import com.smartshelf.models.*;
import com.smartshelf.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final StockAlertService stockAlertService;

    public PurchaseService(PurchaseRepository purchaseRepository, SupplierRepository supplierRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, InventoryTransactionRepository inventoryTransactionRepository, StockAlertService stockAlertService) {
        this.purchaseRepository = purchaseRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.stockAlertService = stockAlertService;
    }

    public List<PurchaseResponse> getAllPurchases() {
        return purchaseRepository.findAllByOrderByPurchaseDateDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PurchaseResponse getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
        return mapToResponse(purchase);
    }

    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId()));

        String invoiceNumber = request.getInvoiceNumber();
        if (invoiceNumber == null || invoiceNumber.trim().isEmpty()) {
            invoiceNumber = "PO-" + System.currentTimeMillis();
        }

        Purchase purchase = Purchase.builder()
                .invoiceNumber(invoiceNumber.trim())
                .supplier(supplier)
                .purchaseDate(request.getPurchaseDate() != null ? request.getPurchaseDate() : LocalDate.now())
                .status(PurchaseStatus.ORDERED)
                .notes(request.getNotes())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (PurchaseItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId()));

            BigDecimal totalCost = itemReq.getUnitCost().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(totalCost);

            PurchaseItem item = PurchaseItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitCost(itemReq.getUnitCost())
                    .totalCost(totalCost)
                    .build();

            purchase.addItem(item);
        }

        purchase.setTotalAmount(totalAmount);
        purchase = purchaseRepository.save(purchase);

        return mapToResponse(purchase);
    }

    @Transactional
    public PurchaseResponse markAsReceived(Long purchaseId, String receivedBy) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + purchaseId));

        if (purchase.getStatus() == PurchaseStatus.RECEIVED) {
            throw new BadRequestException("Purchase order is already marked as RECEIVED");
        }

        purchase.setStatus(PurchaseStatus.RECEIVED);
        purchase.setReceivedDate(LocalDate.now());

        for (PurchaseItem item : purchase.getItems()) {
            Product product = item.getProduct();
            int oldStock = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
            int newStock = oldStock + item.getQuantity();

            product.setCurrentStock(newStock);
            if (item.getUnitCost().compareTo(BigDecimal.ZERO) > 0) {
                product.setPurchasePrice(item.getUnitCost());
            }
            final Product savedProduct = productRepository.save(product);

            Inventory inventory = inventoryRepository.findByProductId(savedProduct.getId())
                    .orElseGet(() -> Inventory.builder().product(savedProduct).build());
            inventory.setCurrentStock(newStock);
            inventory.setLastRestockedAt(LocalDateTime.now());
            inventory.setStatus(savedProduct.getCurrentStock() <= savedProduct.getMinimumStock() ? InventoryStatus.LOW : InventoryStatus.HEALTHY);
            inventoryRepository.save(inventory);

            InventoryTransaction tx = InventoryTransaction.builder()
                    .product(savedProduct)
                    .type(TransactionType.RESTOCK)
                    .quantityChange(item.getQuantity())
                    .previousStock(oldStock)
                    .newStock(newStock)
                    .reason("Restocked via PO #" + purchase.getInvoiceNumber())
                    .referenceId(purchase.getInvoiceNumber())
                    .performedBy(receivedBy != null ? receivedBy : "ADMIN")
                    .build();
            inventoryTransactionRepository.save(tx);

            stockAlertService.checkAndTriggerStockAlert(savedProduct);
        }

        purchase = purchaseRepository.save(purchase);
        return mapToResponse(purchase);
    }

    public PurchaseResponse mapToResponse(Purchase purchase) {
        List<PurchaseItemResponse> itemResponses = purchase.getItems().stream()
                .map(i -> PurchaseItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .sku(i.getProduct().getSku())
                        .quantity(i.getQuantity())
                        .unitCost(i.getUnitCost())
                        .totalCost(i.getTotalCost())
                        .build())
                .collect(Collectors.toList());

        return PurchaseResponse.builder()
                .id(purchase.getId())
                .invoiceNumber(purchase.getInvoiceNumber())
                .supplierId(purchase.getSupplier().getId())
                .supplierName(purchase.getSupplier().getName())
                .supplierCompany(purchase.getSupplier().getCompany())
                .purchaseDate(purchase.getPurchaseDate())
                .receivedDate(purchase.getReceivedDate())
                .status(purchase.getStatus())
                .totalAmount(purchase.getTotalAmount())
                .notes(purchase.getNotes())
                .items(itemResponses)
                .createdAt(purchase.getCreatedAt())
                .build();
    }
}
