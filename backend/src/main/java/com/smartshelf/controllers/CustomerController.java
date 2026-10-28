package com.smartshelf.controllers;

import com.smartshelf.dto.CustomerDTO;
import com.smartshelf.dto.CustomerInsightResponse;
import com.smartshelf.dto.OrderResponse;
import com.smartshelf.services.CustomerService;
import com.smartshelf.services.OrderBillingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;
    private final OrderBillingService orderBillingService;

    public CustomerController(CustomerService customerService, OrderBillingService orderBillingService) {
        this.customerService = customerService;
        this.orderBillingService = orderBillingService;
    }

    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(customerService.getCustomers(search, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(customerService.searchCustomers(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<CustomerDTO> getCustomerByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(customerService.getCustomerByPhone(phone));
    }

    @GetMapping("/{id}/insights")
    public ResponseEntity<CustomerInsightResponse> getCustomerInsights(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerInsights(id));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@PathVariable Long id) {
        return ResponseEntity.ok(orderBillingService.getOrdersByCustomerId(id));
    }

    @GetMapping("/pending-credit")
    public ResponseEntity<List<CustomerDTO>> getCustomersWithPendingCredit() {
        return ResponseEntity.ok(customerService.getCustomersWithPendingCredit());
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return new ResponseEntity<>(customerService.createCustomer(customerDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
