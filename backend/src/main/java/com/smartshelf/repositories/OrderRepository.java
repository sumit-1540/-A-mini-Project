package com.smartshelf.repositories;

import com.smartshelf.models.Order;
import com.smartshelf.models.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
           "(:query IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "(o.customer IS NOT NULL AND (LOWER(o.customer.name) LIKE LOWER(CONCAT('%', :query, '%')) OR o.customer.phone LIKE CONCAT('%', :query, '%')))) AND " +
           "(:paymentMethod IS NULL OR o.paymentMethod = :paymentMethod) AND " +
           "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR o.createdAt <= :endDate) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findFilteredOrders(@Param("query") String query,
                                  @Param("paymentMethod") PaymentMethod paymentMethod,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :since")
    Long countOrdersSince(@Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createdAt >= :since")
    BigDecimal sumTotalRevenueSince(@Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(o.grossProfit), 0) FROM Order o WHERE o.createdAt >= :since")
    BigDecimal sumGrossProfitSince(@Param("since") LocalDateTime since);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :since ORDER BY o.createdAt ASC")
    List<Order> findOrdersSince(@Param("since") LocalDateTime since);
}
