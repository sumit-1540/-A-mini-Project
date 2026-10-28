package com.smartshelf.controllers;

import com.smartshelf.dto.InventoryResponse;
import com.smartshelf.dto.InventoryTransactionResponse;
import com.smartshelf.dto.StockAdjustmentRequest;
import com.smartshelf.services.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStockInventory() {
        return ResponseEntity.ok(inventoryService.getLowStockInventory());
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<InventoryResponse>> getExpiringInventory(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(inventoryService.getExpiringInventory(days));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryResponse> adjustStock(@Valid @RequestBody StockAdjustmentRequest request, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "ADMIN";
        return ResponseEntity.ok(inventoryService.adjustStock(request, username));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<InventoryTransactionResponse>> getStockTransactions(@RequestParam(required = false) Long productId) {
        return ResponseEntity.ok(inventoryService.getStockHistory(productId));
    }
}
