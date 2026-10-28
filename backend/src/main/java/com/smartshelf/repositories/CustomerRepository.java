package com.smartshelf.repositories;

import com.smartshelf.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhone(String phone);

    List<Customer> findByActiveTrue();

    long countByActiveTrue();

    @Query("SELECT c FROM Customer c WHERE c.active = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "c.phone LIKE CONCAT('%', :query, '%'))")
    List<Customer> searchCustomers(@Param("query") String query);

    @Query("SELECT c FROM Customer c WHERE c.active = true AND " +
           "(:query IS NULL OR (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR c.phone LIKE CONCAT('%', :query, '%')))")
    Page<Customer> findFilteredCustomers(@Param("query") String query, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.active = true AND c.creditBalance > 0 ORDER BY c.creditBalance DESC")
    List<Customer> findCustomersWithPendingCredit();

    @Query("SELECT COALESCE(SUM(c.creditBalance), 0) FROM Customer c WHERE c.active = true")
    BigDecimal getTotalOutstandingCredit();
}
