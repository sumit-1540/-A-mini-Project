package com.smartshelf.repositories;

import com.smartshelf.models.CreditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Long> {

    List<CreditTransaction> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<CreditTransaction> findByOrderId(Long orderId);

    @Query("SELECT ct FROM CreditTransaction ct ORDER BY ct.createdAt DESC")
    List<CreditTransaction> findRecentTransactions();
}
