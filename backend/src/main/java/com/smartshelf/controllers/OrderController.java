package com.smartshelf.controllers;

import com.smartshelf.dto.BillReceiptResponse;
import com.smartshelf.dto.CreateOrderRequest;
import com.smartshelf.dto.OrderResponse;
import com.smartshelf.services.OrderBillingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderBillingService orderBillingService;

    public OrderController(OrderBillingService orderBillingService) {
        this.orderBillingService = orderBillingService;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(orderBillingService.getOrders(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderBillingService.getOrderById(id));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByOrderNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(orderBillingService.getOrderByOrderNumber(orderNumber));
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<BillReceiptResponse> getBillReceipt(@PathVariable Long id) {
        return ResponseEntity.ok(orderBillingService.getBillReceipt(id));
    }

    @PostMapping
    public ResponseEntity<BillReceiptResponse> createOrder(@Valid @RequestBody CreateOrderRequest request, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "cashier";
        return new ResponseEntity<>(orderBillingService.createOrder(request, username), HttpStatus.CREATED);
    }
}
