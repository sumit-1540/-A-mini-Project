package com.smartshelf.controllers;

import com.smartshelf.dto.CreditTransactionResponse;
import com.smartshelf.dto.RecordCreditPaymentRequest;
import com.smartshelf.services.CreditService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credits")
@CrossOrigin(origins = "*")
public class CreditController {

    private final CreditService creditService;

    public CreditController(CreditService creditService) {
        this.creditService = creditService;
    }

    @GetMapping
    public ResponseEntity<List<CreditTransactionResponse>> getAllCreditTransactions() {
        return ResponseEntity.ok(creditService.getAllCreditTransactions());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CreditTransactionResponse>> getCustomerCreditTransactions(@PathVariable Long customerId) {
        return ResponseEntity.ok(creditService.getTransactionsByCustomer(customerId));
    }

    @PostMapping("/payment")
    public ResponseEntity<CreditTransactionResponse> recordPayment(@Valid @RequestBody RecordCreditPaymentRequest request, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "cashier";
        return new ResponseEntity<>(creditService.recordPayment(request, username), HttpStatus.CREATED);
    }
}
