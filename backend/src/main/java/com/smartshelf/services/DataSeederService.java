package com.smartshelf.services;

import com.smartshelf.models.*;
import com.smartshelf.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DataSeederService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeederService.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final BillRepository billRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final SalesRecordRepository salesRecordRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeederService(UserRepository userRepository, CategoryRepository categoryRepository, SupplierRepository supplierRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, CustomerRepository customerRepository, OrderRepository orderRepository, BillRepository billRepository, CreditTransactionRepository creditTransactionRepository, LoyaltyTransactionRepository loyaltyTransactionRepository, SalesRecordRepository salesRecordRepository, NotificationRepository notificationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.billRepository = billRepository;
        this.creditTransactionRepository = creditTransactionRepository;
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
        this.salesRecordRepository = salesRecordRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            enrichExistingProductsWithImagesAndMoreData();
            log.info("Database already seeded. Enriched existing products with images and catalog items.");
            return;
        }

        log.info("Starting SmartShelf demo data seeding...");

        // 1. Users
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Rajesh Sharma (Owner)")
                .email("admin@smartshelf.io")
                .phone("+91 98200 12345")
                .role(Role.ROLE_ADMIN)
                .active(true)
                .build();
        userRepository.save(admin);

        User cashier = User.builder()
                .username("cashier")
                .password(passwordEncoder.encode("cashier123"))
                .fullName("Amit Kumar (Cashier)")
                .email("amit@smartshelf.io")
                .phone("+91 98300 54321")
                .role(Role.ROLE_CASHIER)
                .active(true)
                .build();
        userRepository.save(cashier);

        // 2. Categories
        Category grains = categoryRepository.save(Category.builder().name("Grains & Flours").description("Rice, wheat flour, pulses and lentils").icon("wheat").active(true).build());
        Category dairy = categoryRepository.save(Category.builder().name("Dairy & Eggs").description("Milk, butter, curd, paneer, and farm eggs").icon("milk").active(true).build());
        Category spices = categoryRepository.save(Category.builder().name("Spices & Masala").description("Pure and blended whole/powdered spices").icon("sparkles").active(true).build());
        Category oils = categoryRepository.save(Category.builder().name("Cooking Oils & Ghee").description("Mustard oil, sunflower oil, and pure desi ghee").icon("droplet").active(true).build());
        Category snacks = categoryRepository.save(Category.builder().name("Snacks & Biscuits").description("Cookies, wafers, chips, and namkeen").icon("cookie").active(true).build());
        Category beverages = categoryRepository.save(Category.builder().name("Beverages & Tea").description("Premium tea, coffee, juices, and soft drinks").icon("coffee").active(true).build());
        Category personalCare = categoryRepository.save(Category.builder().name("Personal Care").description("Soaps, shampoos, toothpaste, and hygiene").icon("heart").active(true).build());
        Category cleaning = categoryRepository.save(Category.builder().name("Cleaning & Household").description("Detergents, floor cleaners, dishwash bars").icon("shield").active(true).build());

        // 3. Suppliers
        Supplier s1 = supplierRepository.save(Supplier.builder()
                .name("Ramesh Wholesale Distributors")
                .company("Ramesh Agro Traders Pvt Ltd")
                .phone("+91 98450 11223")
                .email("orders@rameshagro.com")
                .address("Plot 45, APMC Market Yard, Navi Mumbai")
                .gstNumber("27AABCR1234F1Z8")
                .active(true)
                .build());

        Supplier s2 = supplierRepository.save(Supplier.builder()
                .name("Amul Dairy Cooperative Agency")
                .company("Gujarat Cooperative Milk Marketing Federation")
                .phone("+91 98765 99887")
                .email("supply@amuldairy.coop")
                .address("Amul House, Anand, Gujarat")
                .gstNumber("24AAACG1234A1Z5")
                .active(true)
                .build());

        Supplier s3 = supplierRepository.save(Supplier.builder()
                .name("ITC FMCG Supply Hub")
                .company("ITC Consumer Goods Ltd")
                .phone("+91 98111 22334")
                .email("fmcg-dist@itc.in")
                .address("Virginia House, Kolkata, West Bengal")
                .gstNumber("19AAACI1234D1Z2")
                .active(true)
                .build());

        // 4. Products & Inventory
        LocalDate today = LocalDate.now();

        Product p1 = createProductHelper("India Gate Basmati Rice 5kg", "SKU-RICE-001", "890103001", grains, "India Gate", "bag",
                new BigDecimal("420.00"), new BigDecimal("510.00"), 45, 10, today.plusMonths(12), s1, "Aisle 1", "Shelf A1");

        Product p2 = createProductHelper("Aashirvaad Superior MP Sharbati Atta 10kg", "SKU-ATTA-002", "890103002", grains, "Aashirvaad", "bag",
                new BigDecimal("380.00"), new BigDecimal("460.00"), 30, 8, today.plusMonths(6), s3, "Aisle 1", "Shelf A2");

        Product p3 = createProductHelper("Tata Salt Vacuum Evaporated 1kg", "SKU-SALT-003", "890103003", spices, "Tata", "pkt",
                new BigDecimal("22.00"), new BigDecimal("28.00"), 120, 20, today.plusMonths(24), s1, "Aisle 2", "Shelf B1");

        Product p4 = createProductHelper("Amul Butter Pasteurized 500g", "SKU-BTR-004", "890103004", dairy, "Amul", "pack",
                new BigDecimal("230.00"), new BigDecimal("275.00"), 4, 10, today.plusDays(4), s2, "Chiller 1", "Rack 2"); // Low stock & expiring soon

        Product p5 = createProductHelper("Amul Taaza Homogenised Toned Milk 1L", "SKU-MILK-005", "890103005", dairy, "Amul", "tetra",
                new BigDecimal("62.00"), new BigDecimal("74.00"), 2, 15, today.plusDays(2), s2, "Chiller 1", "Rack 1"); // Critical stock & expiring

        Product p6 = createProductHelper("Fortune Sunlite Refined Sunflower Oil 1L", "SKU-OIL-006", "890103006", oils, "Fortune", "pouch",
                new BigDecimal("125.00"), new BigDecimal("155.00"), 50, 12, today.plusMonths(8), s1, "Aisle 3", "Shelf C1");

        Product p7 = createProductHelper("Red Label Strong Tea 500g", "SKU-TEA-007", "890103007", beverages, "Brooke Bond", "box",
                new BigDecimal("240.00"), new BigDecimal("295.00"), 25, 8, today.plusMonths(18), s3, "Aisle 2", "Shelf B3");

        Product p8 = createProductHelper("Parle-G Gold Biscuits 1kg Family Pack", "SKU-PARLE-008", "890103008", snacks, "Parle", "pack",
                new BigDecimal("90.00"), new BigDecimal("120.00"), 60, 15, today.plusMonths(5), s1, "Aisle 4", "Shelf D1");

        Product p9 = createProductHelper("Everest Garam Masala 100g", "SKU-MAS-009", "890103009", spices, "Everest", "box",
                new BigDecimal("68.00"), new BigDecimal("88.00"), 40, 10, today.plusMonths(10), s1, "Aisle 2", "Shelf B2");

        Product p10 = createProductHelper("Surf Excel Easy Wash Detergent Powder 1kg", "SKU-SURF-010", "890103010", cleaning, "Surf Excel", "bag",
                new BigDecimal("115.00"), new BigDecimal("145.00"), 35, 10, today.plusMonths(36), s3, "Aisle 5", "Shelf E1");

        Product p11 = createProductHelper("Dettol Original Bathing Soap 125g (Buy 3 Get 1)", "SKU-DET-011", "890103011", personalCare, "Dettol", "bundle",
                new BigDecimal("170.00"), new BigDecimal("210.00"), 18, 5, today.plusMonths(24), s3, "Aisle 5", "Shelf E2");

        Product p12 = createProductHelper("Maggi 2-Minute Masala Noodles (Pack of 12)", "SKU-MAG-012", "890103012", snacks, "Nestle", "pack",
                new BigDecimal("150.00"), new BigDecimal("180.00"), 3, 15, today.plusMonths(4), s1, "Aisle 4", "Shelf D2"); // Low stock

        // 5. Customers
        Customer c1 = customerRepository.save(Customer.builder()
                .name("Suresh Patel")
                .phone("9820198201")
                .email("suresh.patel@gmail.com")
                .address("Flat 302, Gokul Heights, Market Road")
                .totalPurchases(new BigDecimal("14500.00"))
                .totalOrders(8)
                .creditBalance(new BigDecimal("1250.00"))
                .rewardPoints(145)
                .active(true)
                .build());

        Customer c2 = customerRepository.save(Customer.builder()
                .name("Pooja Sharma")
                .phone("9833445566")
                .email("pooja.sharma@yahoo.com")
                .address("B-12, Sundar Nagar, Station Road")
                .totalPurchases(new BigDecimal("8200.00"))
                .totalOrders(5)
                .creditBalance(BigDecimal.ZERO)
                .rewardPoints(82)
                .active(true)
                .build());

        Customer c3 = customerRepository.save(Customer.builder()
                .name("Vikram Deshmukh")
                .phone("9899001122")
                .email("vikram.deshmukh@outlook.com")
                .address("House 14, Shanti Colony")
                .totalPurchases(new BigDecimal("21400.00"))
                .totalOrders(12)
                .creditBalance(new BigDecimal("2800.00"))
                .rewardPoints(214)
                .active(true)
                .build());

        Customer c4 = customerRepository.save(Customer.builder()
                .name("Sunita Verma")
                .phone("9765432109")
                .email("sunita.verma@gmail.com")
                .address("C-401, Galaxy Enclave")
                .totalPurchases(new BigDecimal("5600.00"))
                .totalOrders(3)
                .creditBalance(BigDecimal.ZERO)
                .rewardPoints(56)
                .active(true)
                .build());

        // 6. Historical Orders & Bills
        createDemoOrder(c1, cashier, List.of(p1, p3, p6), PaymentMethod.CASH, LocalDateTime.now().minusDays(5));
        createDemoOrder(c2, cashier, List.of(p2, p7, p8, p9), PaymentMethod.UPI_QR, LocalDateTime.now().minusDays(3));
        createDemoOrder(c3, cashier, List.of(p1, p2, p4, p5, p6, p10), PaymentMethod.CREDIT_UDHAR, LocalDateTime.now().minusDays(1));
        createDemoOrder(c4, cashier, List.of(p7, p8, p11, p12), PaymentMethod.CARD, LocalDateTime.now().minusHours(4));

        // 7. Seed Past 7 Days Sales Record
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            BigDecimal rev = new BigDecimal(4500 + (i * 750) + (int)(Math.random() * 500));
            BigDecimal cost = rev.multiply(new BigDecimal("0.78"));
            BigDecimal profit = rev.subtract(cost);
            int orders = 12 + (i * 2);
            int items = orders * 4;

            salesRecordRepository.save(SalesRecord.builder()
                    .recordDate(d)
                    .totalRevenue(rev)
                    .totalCost(cost)
                    .grossProfit(profit)
                    .totalOrders(orders)
                    .totalItemsSold(items)
                    .build());
        }

        // 8. Notifications
        notificationRepository.save(Notification.builder()
                .type(NotificationType.CRITICAL_STOCK)
                .title("Critical Stock: Amul Taaza Milk 1L")
                .message("Only 2 units remaining in stock. Restock immediately to prevent lost sales.")
                .entityType(EntityType.PRODUCT)
                .entityId(p5.getId())
                .isRead(false)
                .build());

        notificationRepository.save(Notification.builder()
                .type(NotificationType.EXPIRING_SOON)
                .title("Expiring Soon (4 Days): Amul Butter 500g")
                .message("4 units of Amul Butter expire on " + today.plusDays(4) + ". Recommended: Apply 30% Smart Markdown.")
                .entityType(EntityType.PRODUCT)
                .entityId(p4.getId())
                .isRead(false)
                .build());

        notificationRepository.save(Notification.builder()
                .type(NotificationType.CREDIT_OVERDUE)
                .title("Pending Udhar: Vikram Deshmukh (₹2,800)")
                .message("Vikram Deshmukh has an outstanding credit balance of ₹2,800.00 pending for over 15 days.")
                .entityType(EntityType.CUSTOMER)
                .entityId(c3.getId())
                .isRead(false)
                .build());

        log.info("SmartShelf demo data seeding completed successfully!");
    }

    private Product createProductHelper(String name, String sku, String barcode, Category cat, String brand, String unit,
                                        BigDecimal cost, BigDecimal price, int stock, int minStock, LocalDate expiry,
                                        Supplier supplier, String aisle, String shelf) {
        Product p = Product.builder()
                .name(name)
                .sku(sku)
                .barcode(barcode)
                .category(cat)
                .brand(brand)
                .unit(unit)
                .purchasePrice(cost)
                .sellingPrice(price)
                .currentStock(stock)
                .minimumStock(minStock)
                .expiryDate(expiry)
                .supplier(supplier)
                .imageUrl(null)
                .active(true)
                .build();
        p = productRepository.save(p);

        Inventory inv = Inventory.builder()
                .product(p)
                .currentStock(stock)
                .minimumStock(minStock)
                .status(stock <= 0 ? InventoryStatus.CRITICAL : (stock <= minStock ? InventoryStatus.LOW : InventoryStatus.HEALTHY))
                .aisle(aisle)
                .shelf(shelf)
                .build();
        inventoryRepository.save(inv);

        return p;
    }

    private void enrichExistingProductsWithImagesAndMoreData() {
        // 1. Remove assigned images from all existing products
        List<Product> existing = productRepository.findAll();
        for (Product p : existing) {
            if (p.getImageUrl() != null) {
                p.setImageUrl(null);
                productRepository.save(p);
            }
        }

        // 2. Add extra popular products if catalog is small
        if (existing.size() < 25) {
            Category grains = categoryRepository.findByName("Grains & Flours").orElse(null);
            Category dairy = categoryRepository.findByName("Dairy & Eggs").orElse(null);
            Category oils = categoryRepository.findByName("Cooking Oils & Ghee").orElse(null);
            Category snacks = categoryRepository.findByName("Snacks & Biscuits").orElse(null);
            Category beverages = categoryRepository.findByName("Beverages & Tea").orElse(null);
            Category personalCare = categoryRepository.findByName("Personal Care").orElse(null);
            Category cleaning = categoryRepository.findByName("Cleaning & Household").orElse(null);
            Supplier supplier = supplierRepository.findAll().stream().findFirst().orElse(null);

            LocalDate today = LocalDate.now();

            if (dairy != null && !productRepository.existsBySku("SKU-PAN-013")) {
                createProductHelper("Amul Fresh Malai Paneer 200g", "SKU-PAN-013", "890103013", dairy, "Amul", "pack",
                        new BigDecimal("75.00"), new BigDecimal("95.00"), 28, 5, today.plusDays(10), supplier, "Chiller 1", "Rack 3");
            }
            if (dairy != null && !productRepository.existsBySku("SKU-EGG-014")) {
                createProductHelper("Farm Fresh Brown Eggs (Pack of 12)", "SKU-EGG-014", "890103014", dairy, "FarmFresh", "tray",
                        new BigDecimal("70.00"), new BigDecimal("92.00"), 40, 10, today.plusDays(14), supplier, "Chiller 2", "Rack 1");
            }
            if (oils != null && !productRepository.existsBySku("SKU-GHEE-015")) {
                createProductHelper("Amul Pure Desi Ghee 1L Tin", "SKU-GHEE-015", "890103015", oils, "Amul", "tin",
                        new BigDecimal("530.00"), new BigDecimal("630.00"), 22, 6, today.plusMonths(12), supplier, "Aisle 3", "Shelf C2");
            }
            if (snacks != null && !productRepository.existsBySku("SKU-GOOD-016")) {
                createProductHelper("Britannia Good Day Butter Cookies 600g", "SKU-GOOD-016", "890103016", snacks, "Britannia", "pack",
                        new BigDecimal("80.00"), new BigDecimal("105.00"), 50, 10, today.plusMonths(6), supplier, "Aisle 4", "Shelf D3");
            }
            if (snacks != null && !productRepository.existsBySku("SKU-LAYS-017")) {
                createProductHelper("Lay's India's Magic Masala Chips 115g", "SKU-LAYS-017", "890103017", snacks, "Lay's", "pkt",
                        new BigDecimal("38.00"), new BigDecimal("50.00"), 65, 15, today.plusMonths(4), supplier, "Aisle 4", "Shelf D4");
            }
            if (snacks != null && !productRepository.existsBySku("SKU-KURK-018")) {
                createProductHelper("Kurkure Masala Munch Crunchy Snacks 135g", "SKU-KURK-018", "890103018", snacks, "Kurkure", "pkt",
                        new BigDecimal("35.00"), new BigDecimal("48.00"), 70, 15, today.plusMonths(5), supplier, "Aisle 4", "Shelf D4");
            }
            if (beverages != null && !productRepository.existsBySku("SKU-COFF-019")) {
                createProductHelper("Nescafe Classic Instant Coffee Jar 100g", "SKU-COFF-019", "890103019", beverages, "Nescafe", "jar",
                        new BigDecimal("260.00"), new BigDecimal("325.00"), 30, 8, today.plusMonths(24), supplier, "Aisle 2", "Shelf B4");
            }
            if (beverages != null && !productRepository.existsBySku("SKU-COKE-020")) {
                createProductHelper("Coca-Cola Original Taste Can 300ml", "SKU-COKE-020", "890103020", beverages, "Coca-Cola", "can",
                        new BigDecimal("30.00"), new BigDecimal("40.00"), 85, 20, today.plusMonths(6), supplier, "Aisle 2", "Shelf B5");
            }
            if (personalCare != null && !productRepository.existsBySku("SKU-COLG-021")) {
                createProductHelper("Colgate Total Dental Cavity Toothpaste 150g", "SKU-COLG-021", "890103021", personalCare, "Colgate", "tube",
                        new BigDecimal("95.00"), new BigDecimal("125.00"), 42, 10, today.plusMonths(24), supplier, "Aisle 5", "Shelf E3");
            }
            if (cleaning != null && !productRepository.existsBySku("SKU-VIM-022")) {
                createProductHelper("Vim Lemon Dishwash Gel Bottle 750ml", "SKU-VIM-022", "890103022", cleaning, "Vim", "bottle",
                        new BigDecimal("120.00"), new BigDecimal("155.00"), 38, 8, today.plusMonths(36), supplier, "Aisle 5", "Shelf E4");
            }
            if (grains != null && !productRepository.existsBySku("SKU-DAL-023")) {
                createProductHelper("Tata Sampann Unpolished Toor Dal 1kg", "SKU-DAL-023", "890103023", grains, "Tata Sampann", "pkt",
                        new BigDecimal("145.00"), new BigDecimal("185.00"), 55, 12, today.plusMonths(12), supplier, "Aisle 1", "Shelf A3");
            }
            if (grains != null && !productRepository.existsBySku("SKU-OAT-024")) {
                createProductHelper("Quaker Rolled Wholegrain Oats 1kg", "SKU-OAT-024", "890103024", grains, "Quaker", "pack",
                        new BigDecimal("160.00"), new BigDecimal("205.00"), 34, 8, today.plusMonths(12), supplier, "Aisle 1", "Shelf A4");
            }
        }
    }

    private void createDemoOrder(Customer customer, User cashier, List<Product> products, PaymentMethod method, LocalDateTime orderTime) {
        String ordNum = "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 900 + 100);

        Order order = Order.builder()
                .orderNumber(ordNum)
                .customer(customer)
                .cashier(cashier)
                .paymentMethod(method)
                .status(OrderStatus.COMPLETED)
                .createdAt(orderTime)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Product p : products) {
            int qty = (int)(Math.random() * 2 + 1);
            BigDecimal linePrice = p.getSellingPrice().multiply(BigDecimal.valueOf(qty));
            BigDecimal lineCost = p.getPurchasePrice().multiply(BigDecimal.valueOf(qty));
            subtotal = subtotal.add(linePrice);
            totalCost = totalCost.add(lineCost);

            OrderItem item = OrderItem.builder()
                    .product(p)
                    .productName(p.getName())
                    .quantity(qty)
                    .unitPrice(p.getSellingPrice())
                    .unitCost(p.getPurchasePrice())
                    .discount(BigDecimal.ZERO)
                    .totalPrice(linePrice)
                    .build();
            order.addItem(item);
        }

        order.setSubtotal(subtotal);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTaxAmount(BigDecimal.ZERO);
        order.setTotalAmount(subtotal);
        order.setTotalCost(totalCost);
        order.setGrossProfit(subtotal.subtract(totalCost));

        order = orderRepository.save(order);

        Bill bill = Bill.builder()
                .billNumber("BILL-" + System.currentTimeMillis())
                .order(order)
                .amountPaid(method == PaymentMethod.CREDIT_UDHAR ? BigDecimal.ZERO : subtotal)
                .balanceDue(method == PaymentMethod.CREDIT_UDHAR ? subtotal : BigDecimal.ZERO)
                .changeReturned(BigDecimal.ZERO)
                .paymentMethod(method)
                .createdAt(orderTime)
                .build();
        billRepository.save(bill);
    }
}
