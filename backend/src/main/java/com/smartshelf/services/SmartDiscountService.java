package com.smartshelf.services;

import com.smartshelf.dto.SmartDiscountDTO;
import com.smartshelf.models.Product;
import com.smartshelf.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmartDiscountService {

    private final ProductRepository productRepository;

    public SmartDiscountService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<SmartDiscountDTO> getDiscountRecommendations() {
        LocalDate today = LocalDate.now();
        List<Product> expiringProducts = productRepository.findExpiringProducts(today.plusDays(30));
        List<SmartDiscountDTO> list = new ArrayList<>();

        for (Product product : expiringProducts) {
            long daysUntil = ChronoUnit.DAYS.between(today, product.getExpiryDate());
            int discountPct;
            String reason;

            if (daysUntil <= 0) {
                discountPct = 70;
                reason = "Expired or expiring today! Clear immediately.";
            } else if (daysUntil <= 3) {
                discountPct = 50;
                reason = "Expiring in " + daysUntil + " days. High markdown to avoid total write-off.";
            } else if (daysUntil <= 7) {
                discountPct = 30;
                reason = "Expiring within a week. Markdown to accelerate clearance.";
            } else if (daysUntil <= 15) {
                discountPct = 15;
                reason = "Expiring in 2 weeks. Mild discount to lift velocity.";
            } else {
                discountPct = 10;
                reason = "Expiring within a month. Early promotional discount.";
            }

            BigDecimal price = product.getSellingPrice() != null ? product.getSellingPrice() : BigDecimal.ZERO;
            BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discountPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal discountedPrice = price.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);

            BigDecimal potentialLoss = (product.getPurchasePrice() != null ? product.getPurchasePrice() : BigDecimal.ZERO)
                    .multiply(BigDecimal.valueOf(product.getCurrentStock() != null ? product.getCurrentStock() : 0));

            list.add(SmartDiscountDTO.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .sku(product.getSku())
                    .categoryName(product.getCategory() != null ? product.getCategory().getName() : "")
                    .currentStock(product.getCurrentStock())
                    .currentPrice(price)
                    .expiryDate(product.getExpiryDate())
                    .daysUntilExpiry(daysUntil)
                    .recommendedDiscountPercent(discountPct)
                    .discountedPrice(discountedPrice)
                    .reason(reason)
                    .potentialLossPrevented("₹" + potentialLoss.setScale(2, RoundingMode.HALF_UP))
                    .build());
        }

        list.sort((a, b) -> Long.compare(a.getDaysUntilExpiry(), b.getDaysUntilExpiry()));
        return list;
    }
}
