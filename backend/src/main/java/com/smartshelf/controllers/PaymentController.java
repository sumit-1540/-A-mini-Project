package com.smartshelf.controllers;

import com.smartshelf.dto.PaymentQrRequest;
import com.smartshelf.dto.PaymentQrResponse;
import com.smartshelf.dto.PaymentVerificationRequest;
import com.smartshelf.dto.PaymentVerificationResponse;
import com.smartshelf.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${smartshelf.upi.vpa:farandesumit@oksbi}")
    private String merchantVpa;

    @Value("${smartshelf.upi.merchant-name:Sumit Farande}")
    private String merchantName;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/generate-upi-qr")
    public ResponseEntity<PaymentQrResponse> generateUpiQr(@Valid @RequestBody PaymentQrRequest request) {
        PaymentQrResponse response = paymentService.generateUpiQr(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-upi")
    public ResponseEntity<PaymentVerificationResponse> verifyUpiPayment(@Valid @RequestBody PaymentVerificationRequest request) {
        PaymentVerificationResponse response = paymentService.verifyPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getPaymentConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("merchantVpa", merchantVpa);
        config.put("merchantName", merchantName);
        config.put("currency", "INR");
        config.put("supportedApps", new String[]{"Google Pay", "PhonePe", "Paytm", "BHIM", "Amazon Pay", "Cred"});
        config.put("systemStatus", "OPERATIONAL");
        return ResponseEntity.ok(config);
    }
}
