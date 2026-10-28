package com.smartshelf.services;

import com.opencsv.CSVWriter;
import com.smartshelf.models.Customer;
import com.smartshelf.models.Order;
import com.smartshelf.models.Product;
import com.smartshelf.repositories.CustomerRepository;
import com.smartshelf.repositories.OrderRepository;
import com.smartshelf.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ReportService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public ReportService(ProductRepository productRepository, OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public byte[] generateInventoryCsvReport() {
        List<Product> products = productRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            writer.writeNext(new String[]{"ID", "Product Name", "SKU", "Barcode", "Category", "Brand", "Unit", "Purchase Price", "Selling Price", "Current Stock", "Min Stock", "Expiry Date", "Status"});

            for (Product p : products) {
                writer.writeNext(new String[]{
                        String.valueOf(p.getId()),
                        p.getName(),
                        p.getSku(),
                        p.getBarcode() != null ? p.getBarcode() : "",
                        p.getCategory() != null ? p.getCategory().getName() : "",
                        p.getBrand() != null ? p.getBrand() : "",
                        p.getUnit() != null ? p.getUnit() : "",
                        p.getPurchasePrice() != null ? p.getPurchasePrice().toString() : "0.00",
                        p.getSellingPrice() != null ? p.getSellingPrice().toString() : "0.00",
                        String.valueOf(p.getCurrentStock()),
                        String.valueOf(p.getMinimumStock()),
                        p.getExpiryDate() != null ? p.getExpiryDate().toString() : "",
                        p.isActive() ? "ACTIVE" : "INACTIVE"
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating Inventory CSV: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }

    public byte[] generateSalesReport() {
        List<Order> orders = orderRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            writer.writeNext(new String[]{"Order ID", "Order Number", "Date Time", "Customer Name", "Customer Phone", "Cashier", "Items Count", "Subtotal", "Discount", "Tax", "Total Amount", "Gross Profit", "Payment Method", "Status"});

            for (Order o : orders) {
                writer.writeNext(new String[]{
                        String.valueOf(o.getId()),
                        o.getOrderNumber(),
                        o.getCreatedAt() != null ? o.getCreatedAt().toString() : "",
                        o.getCustomer() != null ? o.getCustomer().getName() : "Walk-in",
                        o.getCustomer() != null ? o.getCustomer().getPhone() : "",
                        o.getCashier() != null ? o.getCashier().getFullName() : "",
                        String.valueOf(o.getItems().size()),
                        o.getSubtotal() != null ? o.getSubtotal().toString() : "0.00",
                        o.getDiscountAmount() != null ? o.getDiscountAmount().toString() : "0.00",
                        o.getTaxAmount() != null ? o.getTaxAmount().toString() : "0.00",
                        o.getTotalAmount() != null ? o.getTotalAmount().toString() : "0.00",
                        o.getGrossProfit() != null ? o.getGrossProfit().toString() : "0.00",
                        o.getPaymentMethod() != null ? o.getPaymentMethod().name() : "",
                        o.getStatus() != null ? o.getStatus().name() : ""
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating Sales CSV: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }

    public byte[] generateCreditRegisterReport() {
        List<Customer> customers = customerRepository.findCustomersWithPendingCredit();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            writer.writeNext(new String[]{"Customer ID", "Customer Name", "Phone Number", "Email", "Total Purchases", "Total Orders", "Outstanding Credit (Udhar)", "Reward Points"});

            for (Customer c : customers) {
                writer.writeNext(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getPhone(),
                        c.getEmail() != null ? c.getEmail() : "",
                        c.getTotalPurchases() != null ? c.getTotalPurchases().toString() : "0.00",
                        String.valueOf(c.getTotalOrders()),
                        c.getCreditBalance() != null ? c.getCreditBalance().toString() : "0.00",
                        String.valueOf(c.getRewardPoints())
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating Credit Register CSV: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }
}
