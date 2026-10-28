package com.smartshelf.services;

import com.smartshelf.dto.RecommendationDTO;
import com.smartshelf.models.Customer;
import com.smartshelf.models.Product;
import com.smartshelf.repositories.CustomerRepository;
import com.smartshelf.repositories.OrderItemRepository;
import com.smartshelf.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    public RecommendationService(ProductRepository productRepository, OrderItemRepository orderItemRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
    }

    public List<RecommendationDTO> getRecommendationsForCart(List<Long> currentCartProductIds, Long customerId) {
        List<RecommendationDTO> recommendations = new ArrayList<>();

        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer != null) {
                List<Object[]> favProducts = orderItemRepository.findFavoriteProductsByCustomerId(customerId);
                for (Object[] row : favProducts) {
                    Long prodId = (Long) row[0];
                    if (currentCartProductIds == null || !currentCartProductIds.contains(prodId)) {
                        productRepository.findById(prodId).ifPresent(p -> {
                            if (p.isActive() && p.getCurrentStock() > 0) {
                                recommendations.add(RecommendationDTO.builder()
                                        .productId(p.getId())
                                        .productName(p.getName())
                                        .sku(p.getSku())
                                        .categoryName(p.getCategory() != null ? p.getCategory().getName() : "")
                                        .sellingPrice(p.getSellingPrice())
                                        .reason("Frequently bought by " + customer.getName())
                                        .confidenceScore(0.92)
                                        .imageUrl(p.getImageUrl())
                                        .build());
                            }
                        });
                    }
                    if (recommendations.size() >= 3) break;
                }
            }
        }

        if (currentCartProductIds != null && !currentCartProductIds.isEmpty()) {
            for (Long prodId : currentCartProductIds) {
                List<Product> coPurchased = getCoPurchasedProducts(prodId);
                for (Product p : coPurchased) {
                    if (!currentCartProductIds.contains(p.getId()) &&
                            recommendations.stream().noneMatch(r -> r.getProductId().equals(p.getId()))) {
                        recommendations.add(RecommendationDTO.builder()
                                .productId(p.getId())
                                .productName(p.getName())
                                .sku(p.getSku())
                                .categoryName(p.getCategory() != null ? p.getCategory().getName() : "")
                                .sellingPrice(p.getSellingPrice())
                                .reason("Frequently bought together with cart items")
                                .confidenceScore(0.85)
                                .imageUrl(p.getImageUrl())
                                .build());
                    }
                    if (recommendations.size() >= 5) break;
                }
            }
        }

        if (recommendations.size() < 4) {
            List<Object[]> topSelling = orderItemRepository.findTopSellingProducts();
            for (Object[] row : topSelling) {
                Long prodId = (Long) row[0];
                if ((currentCartProductIds == null || !currentCartProductIds.contains(prodId)) &&
                        recommendations.stream().noneMatch(r -> r.getProductId().equals(prodId))) {
                    productRepository.findById(prodId).ifPresent(p -> {
                        if (p.isActive() && p.getCurrentStock() > 0) {
                            recommendations.add(RecommendationDTO.builder()
                                    .productId(p.getId())
                                    .productName(p.getName())
                                    .sku(p.getSku())
                                    .categoryName(p.getCategory() != null ? p.getCategory().getName() : "")
                                    .sellingPrice(p.getSellingPrice())
                                    .reason("Popular store best-seller")
                                    .confidenceScore(0.78)
                                    .imageUrl(p.getImageUrl())
                                    .build());
                        }
                    });
                }
                if (recommendations.size() >= 5) break;
            }
        }

        return recommendations;
    }

    private List<Product> getCoPurchasedProducts(Long productId) {
        Product p = productRepository.findById(productId).orElse(null);
        if (p == null || p.getCategory() == null) return Collections.emptyList();

        return productRepository.findByCategoryIdAndActiveTrue(p.getCategory().getId()).stream()
                .filter(other -> !other.getId().equals(productId) && other.getCurrentStock() > 0)
                .limit(3)
                .collect(Collectors.toList());
    }
}
