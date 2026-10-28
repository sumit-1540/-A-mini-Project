package com.smartshelf.services;

import com.smartshelf.dto.CustomerDTO;
import com.smartshelf.dto.CustomerInsightResponse;
import com.smartshelf.dto.ProductResponse;
import com.smartshelf.exceptions.DuplicateResourceException;
import com.smartshelf.exceptions.ResourceNotFoundException;
import com.smartshelf.models.Customer;
import com.smartshelf.models.Order;
import com.smartshelf.models.Product;
import com.smartshelf.repositories.CustomerRepository;
import com.smartshelf.repositories.OrderItemRepository;
import com.smartshelf.repositories.OrderRepository;
import com.smartshelf.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository, ProductService productService) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    public Page<CustomerDTO> getCustomers(String query, Pageable pageable) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        Page<Customer> page = customerRepository.findFilteredCustomers(cleanQuery, pageable);
        return page.map(this::mapToDTO);
    }

    public List<CustomerDTO> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return customerRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
        }
        return customerRepository.searchCustomers(query.trim()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToDTO(customer);
    }

    public CustomerDTO getCustomerByPhone(String phone) {
        Customer customer = customerRepository.findByPhone(phone.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with phone: " + phone));
        return mapToDTO(customer);
    }

    @Transactional
    public CustomerDTO createCustomer(CustomerDTO request) {
        if (customerRepository.existsByPhone(request.getPhone().trim())) {
            throw new DuplicateResourceException("Customer with phone number '" + request.getPhone() + "' already exists");
        }

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .phone(request.getPhone().trim())
                .email(request.getEmail())
                .address(request.getAddress())
                .totalPurchases(BigDecimal.ZERO)
                .totalOrders(0)
                .creditBalance(BigDecimal.ZERO)
                .rewardPoints(0)
                .active(true)
                .build();

        customer = customerRepository.save(customer);
        return mapToDTO(customer);
    }

    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        if (!customer.getPhone().equals(request.getPhone().trim()) &&
                customerRepository.existsByPhone(request.getPhone().trim())) {
            throw new DuplicateResourceException("Customer with phone number '" + request.getPhone() + "' already exists");
        }

        customer.setName(request.getName().trim());
        customer.setPhone(request.getPhone().trim());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setActive(request.isActive());

        customer = customerRepository.save(customer);
        return mapToDTO(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customer.setActive(false);
        customerRepository.save(customer);
    }

    public CustomerInsightResponse getCustomerInsights(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        List<Order> orders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);

        BigDecimal avgOrderValue = BigDecimal.ZERO;
        if (customer.getTotalOrders() != null && customer.getTotalOrders() > 0) {
            avgOrderValue = customer.getTotalPurchases().divide(BigDecimal.valueOf(customer.getTotalOrders()), 2, RoundingMode.HALF_UP);
        }

        LocalDateTime lastPurchase = orders.isEmpty() ? null : orders.get(0).getCreatedAt();

        List<Object[]> topItems = orderItemRepository.findFavoriteProductsByCustomerId(customerId);
        List<ProductResponse> freqProducts = new ArrayList<>();
        for (Object[] row : topItems) {
            Long prodId = (Long) row[0];
            productRepository.findById(prodId).ifPresent(p -> freqProducts.add(productService.mapToResponse(p)));
            if (freqProducts.size() >= 5) break;
        }

        List<Object[]> favCats = orderItemRepository.findFavoriteCategoriesByCustomerId(customerId);
        List<String> categories = new ArrayList<>();
        for (Object[] row : favCats) {
            if (row[0] != null) {
                categories.add((String) row[0]);
            }
            if (categories.size() >= 3) break;
        }

        String preferredPayment = "CASH";
        if (!orders.isEmpty()) {
            preferredPayment = orders.stream()
                    .collect(Collectors.groupingBy(Order::getPaymentMethod, Collectors.counting()))
                    .entrySet().stream()
                    .max((a, b) -> a.getValue().compareTo(b.getValue()))
                    .map(e -> e.getKey().name())
                    .orElse("CASH");
        }

        return CustomerInsightResponse.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .phone(customer.getPhone())
                .totalSpending(customer.getTotalPurchases())
                .totalOrders(customer.getTotalOrders())
                .averageOrderValue(avgOrderValue)
                .outstandingUdhar(customer.getCreditBalance())
                .rewardPoints(customer.getRewardPoints())
                .lastPurchaseDate(lastPurchase)
                .preferredPaymentMethod(preferredPayment)
                .frequentlyPurchasedProducts(freqProducts)
                .favoriteCategories(categories)
                .build();
    }

    public List<CustomerDTO> getCustomersWithPendingCredit() {
        return customerRepository.findCustomersWithPendingCredit().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .totalPurchases(customer.getTotalPurchases() != null ? customer.getTotalPurchases() : BigDecimal.ZERO)
                .totalOrders(customer.getTotalOrders() != null ? customer.getTotalOrders() : 0)
                .creditBalance(customer.getCreditBalance() != null ? customer.getCreditBalance() : BigDecimal.ZERO)
                .rewardPoints(customer.getRewardPoints() != null ? customer.getRewardPoints() : 0)
                .active(customer.isActive())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
