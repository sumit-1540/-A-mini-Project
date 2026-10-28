package com.smartshelf.repositories;

import com.smartshelf.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    @Query("SELECT oi.product.id, oi.productName, SUM(oi.quantity), SUM(oi.totalPrice) " +
           "FROM OrderItem oi " +
           "GROUP BY oi.product.id, oi.productName " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts();

    @Query("SELECT oi.product.category.name, SUM(oi.totalPrice), SUM(oi.quantity) " +
           "FROM OrderItem oi " +
           "WHERE oi.product.category IS NOT NULL " +
           "GROUP BY oi.product.category.name " +
           "ORDER BY SUM(oi.totalPrice) DESC")
    List<Object[]> findSalesByCategory();

    @Query("SELECT oi.product.id, oi.productName, SUM(oi.quantity) " +
           "FROM OrderItem oi " +
           "WHERE oi.order.customer.id = :customerId " +
           "GROUP BY oi.product.id, oi.productName " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findFavoriteProductsByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT oi.product.category.name, COUNT(oi) " +
           "FROM OrderItem oi " +
           "WHERE oi.order.customer.id = :customerId AND oi.product.category IS NOT NULL " +
           "GROUP BY oi.product.category.name " +
           "ORDER BY COUNT(oi) DESC")
    List<Object[]> findFavoriteCategoriesByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi " +
           "WHERE oi.product.id = :productId AND oi.order.createdAt >= :sinceDate")
    Integer getQuantitySoldSince(@Param("productId") Long productId, @Param("sinceDate") LocalDateTime sinceDate);
}
