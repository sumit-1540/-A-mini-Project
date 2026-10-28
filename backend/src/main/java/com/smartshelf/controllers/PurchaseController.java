package com.smartshelf.controllers;

import com.smartshelf.dto.PurchaseRequest;
import com.smartshelf.dto.PurchaseResponse;
import com.smartshelf.services.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseResponse>> getAllPurchases() {
        return ResponseEntity.ok(purchaseService.getAllPurchases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getPurchaseById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        return new ResponseEntity<>(purchaseService.createPurchase(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/receive")
    public ResponseEntity<PurchaseResponse> markAsReceived(@PathVariable Long id, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "ADMIN";
        return ResponseEntity.ok(purchaseService.markAsReceived(id, username));
    }
}
