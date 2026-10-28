package com.smartshelf.services;

import com.smartshelf.dto.*;
import com.smartshelf.exceptions.BadRequestException;
import com.smartshelf.exceptions.InsufficientStockException;
import com.smartshelf.exceptions.ResourceNotFoundException;
import com.smartshelf.models.*;
import com.smartshelf.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderBillingService {

    private static final Logger log = LoggerFactory.getLogger(OrderBillingService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final SalesRecordRepository salesRecordRepository;
    private final StockAlertService stockAlertService;

    public OrderBillingService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, BillRepository billRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, InventoryTransactionRepository inventoryTransactionRepository, CustomerRepository customerRepository, UserRepository userRepository, CreditTransactionRepository creditTransactionRepository, LoyaltyTransactionRepository loyaltyTransactionRepository, SalesRecordRepository salesRecordRepository, StockAlertService stockAlertService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.billRepository = billRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.creditTransactionRepository = creditTransactionRepository;
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
        this.salesRecordRepository = salesRecordRepository;
        this.stockAlertService = stockAlertService;
    }

    public Page<OrderResponse> getOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToResponse);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return mapToResponse(order);
    }

    public BillReceiptResponse getBillReceipt(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return generateReceipt(order);
    }

    @Transactional
    public BillReceiptResponse createOrder(CreateOrderRequest request, String username) {
        User cashier = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Cashier user not found: " + username));

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId()).orElse(null);
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order cart cannot be empty");
        }

        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 900 + 100);

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .cashier(cashier)
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.COMPLETED)
                .notes(request.getNotes())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalQuantitySold = 0;

        List<OrderItem> orderItems = new ArrayList<>();
        List<Product> productsToAlert = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId()));

            int currentStock = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
            if (currentStock < itemReq.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product '" + product.getName() + "'. Available: " + currentStock + ", Requested: " + itemReq.getQuantity());
            }

            BigDecimal unitPrice = itemReq.getCustomPrice() != null ? itemReq.getCustomPrice() : product.getSellingPrice();
            BigDecimal discount = itemReq.getDiscount() != null ? itemReq.getDiscount() : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity())).subtract(discount);
            if (lineTotal.compareTo(BigDecimal.ZERO) < 0) {
                lineTotal = BigDecimal.ZERO;
            }

            BigDecimal lineCost = (product.getPurchasePrice() != null ? product.getPurchasePrice() : BigDecimal.ZERO)
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            subtotal = subtotal.add(lineTotal);
            totalCost = totalCost.add(lineCost);
            totalQuantitySold += itemReq.getQuantity();

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .unitCost(product.getPurchasePrice() != null ? product.getPurchasePrice() : BigDecimal.ZERO)
                    .discount(discount)
                    .totalPrice(lineTotal)
                    .build();

            order.addItem(item);
            orderItems.add(item);

            // Deduct inventory
            int newStock = currentStock - itemReq.getQuantity();
            product.setCurrentStock(newStock);
            productRepository.save(product);

            inventoryRepository.findByProductId(product.getId()).ifPresent(inv -> {
                inv.setCurrentStock(newStock);
                if (newStock <= 0) inv.setStatus(InventoryStatus.CRITICAL);
                else if (newStock <= product.getMinimumStock()) inv.setStatus(InventoryStatus.LOW);
                else inv.setStatus(InventoryStatus.HEALTHY);
                inventoryRepository.save(inv);
            });

            // Record inventory transaction
            InventoryTransaction invTx = InventoryTransaction.builder()
                    .product(product)
                    .type(TransactionType.SALE)
                    .quantityChange(-itemReq.getQuantity())
                    .previousStock(currentStock)
                    .newStock(newStock)
                    .reason("Sale - Order #" + orderNumber)
                    .referenceId(orderNumber)
                    .performedBy(cashier.getUsername())
                    .build();
            inventoryTransactionRepository.save(invTx);

            productsToAlert.add(product);
        }

        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal taxAmount = request.getTaxAmount() != null ? request.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(taxAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        BigDecimal grossProfit = totalAmount.subtract(totalCost);

        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);
        order.setTotalCost(totalCost);
        order.setGrossProfit(grossProfit);

        order = orderRepository.save(order);

        // Bill creation
        BigDecimal amountPaid = request.getAmountPaid() != null ? request.getAmountPaid() : totalAmount;
        BigDecimal changeReturned = BigDecimal.ZERO;
        BigDecimal balanceDue = BigDecimal.ZERO;

        if (request.getPaymentMethod() == PaymentMethod.CREDIT_UDHAR) {
            balanceDue = totalAmount.subtract(amountPaid);
            if (balanceDue.compareTo(BigDecimal.ZERO) < 0) balanceDue = BigDecimal.ZERO;
        } else {
            if (amountPaid.compareTo(totalAmount) > 0) {
                changeReturned = amountPaid.subtract(totalAmount);
            } else if (amountPaid.compareTo(totalAmount) < 0) {
                balanceDue = totalAmount.subtract(amountPaid);
            }
        }

        String billNumber = "BILL-" + System.currentTimeMillis();
        Bill bill = Bill.builder()
                .billNumber(billNumber)
                .order(order)
                .amountPaid(amountPaid)
                .changeReturned(changeReturned)
                .balanceDue(balanceDue)
                .paymentMethod(request.getPaymentMethod())
                .transactionReference(request.getTransactionReference())
                .build();
        billRepository.save(bill);

        // Customer loyalty and Udhar updates
        int earnedLoyaltyPoints = 0;
        if (customer != null) {
            customer.setTotalPurchases(customer.getTotalPurchases().add(totalAmount));
            customer.setTotalOrders(customer.getTotalOrders() + 1);

            // Earn 1 point per 100 Rs spent
            earnedLoyaltyPoints = totalAmount.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).intValue();
            if (earnedLoyaltyPoints > 0) {
                int newPoints = customer.getRewardPoints() + earnedLoyaltyPoints;
                customer.setRewardPoints(newPoints);

                LoyaltyTransaction loyaltyTx = LoyaltyTransaction.builder()
                        .customer(customer)
                        .order(order)
                        .type(LoyaltyType.EARNED)
                        .points(earnedLoyaltyPoints)
                        .balanceAfter(newPoints)
                        .reason("Points earned on Order #" + orderNumber)
                        .build();
                loyaltyTransactionRepository.save(loyaltyTx);
            }

            // If Udhar used
            if (request.getPaymentMethod() == PaymentMethod.CREDIT_UDHAR || balanceDue.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal addedCredit = balanceDue.compareTo(BigDecimal.ZERO) > 0 ? balanceDue : totalAmount;
                BigDecimal newCreditBal = customer.getCreditBalance().add(addedCredit);
                customer.setCreditBalance(newCreditBal);

                CreditTransaction creditTx = CreditTransaction.builder()
                        .customer(customer)
                        .order(order)
                        .type(CreditType.CREDIT_GIVEN)
                        .amount(addedCredit)
                        .balanceAfter(newCreditBal)
                        .paymentMethod(request.getPaymentMethod())
                        .notes("Udhar given on Order #" + orderNumber)
                        .recordedBy(cashier.getUsername())
                        .build();
                creditTransactionRepository.save(creditTx);
            }

            customerRepository.save(customer);
        }

        // Daily Sales aggregation
        LocalDate today = LocalDate.now();
        SalesRecord salesRecord = salesRecordRepository.findByRecordDate(today)
                .orElseGet(() -> SalesRecord.builder()
                        .recordDate(today)
                        .totalRevenue(BigDecimal.ZERO)
                        .totalCost(BigDecimal.ZERO)
                        .grossProfit(BigDecimal.ZERO)
                        .totalOrders(0)
                        .totalItemsSold(0)
                        .build());

        salesRecord.setTotalRevenue(salesRecord.getTotalRevenue().add(totalAmount));
        salesRecord.setTotalCost(salesRecord.getTotalCost().add(totalCost));
        salesRecord.setGrossProfit(salesRecord.getGrossProfit().add(grossProfit));
        salesRecord.setTotalOrders(salesRecord.getTotalOrders() + 1);
        salesRecord.setTotalItemsSold(salesRecord.getTotalItemsSold() + totalQuantitySold);
        salesRecordRepository.save(salesRecord);

        // Check alerts
        for (Product p : productsToAlert) {
            stockAlertService.checkAndTriggerStockAlert(p);
        }

        return generateReceipt(order);
    }

    public OrderResponse mapToResponse(Order order) {
        Bill bill = billRepository.findByOrderId(order.getId()).orElse(null);

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .productName(i.getProductName())
                        .sku(i.getProduct().getSku())
                        .barcode(i.getProduct().getBarcode())
                        .unit(i.getProduct().getUnit())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .discount(i.getDiscount())
                        .totalPrice(i.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getName() : "Walk-in Customer")
                .customerPhone(order.getCustomer() != null ? order.getCustomer().getPhone() : null)
                .cashierId(order.getCashier().getId())
                .cashierName(order.getCashier().getFullName())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .grossProfit(order.getGrossProfit())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .notes(order.getNotes())
                .billNumber(bill != null ? bill.getBillNumber() : null)
                .amountPaid(bill != null ? bill.getAmountPaid() : order.getTotalAmount())
                .changeReturned(bill != null ? bill.getChangeReturned() : BigDecimal.ZERO)
                .balanceDue(bill != null ? bill.getBalanceDue() : BigDecimal.ZERO)
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }

    public BillReceiptResponse generateReceipt(Order order) {
        Bill bill = billRepository.findByOrderId(order.getId()).orElse(null);

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(i -> OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .productName(i.getProductName())
                        .sku(i.getProduct().getSku())
                        .barcode(i.getProduct().getBarcode())
                        .unit(i.getProduct().getUnit())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .discount(i.getDiscount())
                        .totalPrice(i.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        int earnedPoints = order.getTotalAmount().divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).intValue();

        return BillReceiptResponse.builder()
                .shopName("SmartShelf Supermarket")
                .shopAddress("123 Market Street, City Center, PIN - 400001")
                .shopPhone("+91 98200 12345")
                .shopGstin("27AABCU9603R1ZM")
                .billNumber(bill != null ? bill.getBillNumber() : "BILL-N/A")
                .orderNumber(order.getOrderNumber())
                .dateTime(order.getCreatedAt() != null ? order.getCreatedAt() : LocalDateTime.now())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerName(order.getCustomer() != null ? order.getCustomer().getName() : "Walk-in Customer")
                .customerPhone(order.getCustomer() != null ? order.getCustomer().getPhone() : null)
                .cashierName(order.getCashier() != null ? order.getCashier().getFullName() : "Staff Cashier")
                .items(itemResponses)
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .amountPaid(bill != null ? bill.getAmountPaid() : order.getTotalAmount())
                .changeReturned(bill != null ? bill.getChangeReturned() : BigDecimal.ZERO)
                .balanceDue(bill != null ? bill.getBalanceDue() : BigDecimal.ZERO)
                .earnedLoyaltyPoints(earnedPoints)
                .totalCustomerLoyaltyPoints(order.getCustomer() != null ? order.getCustomer().getRewardPoints() : 0)
                .customerOutstandingUdhar(order.getCustomer() != null ? order.getCustomer().getCreditBalance() : BigDecimal.ZERO)
                .transactionReference(bill != null ? bill.getTransactionReference() : null)
                .build();
    }
}
