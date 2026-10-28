package com.smartshelf.repositories;

import com.smartshelf.models.Purchase;
import com.smartshelf.models.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByInvoiceNumber(String invoiceNumber);
    List<Purchase> findBySupplierIdOrderByPurchaseDateDesc(Long supplierId);
    List<Purchase> findByStatusOrderByPurchaseDateDesc(PurchaseStatus status);
    List<Purchase> findAllByOrderByPurchaseDateDesc();
}
