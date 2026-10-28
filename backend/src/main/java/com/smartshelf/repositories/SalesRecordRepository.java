package com.smartshelf.repositories;

import com.smartshelf.models.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesRecordRepository extends JpaRepository<SalesRecord, Long> {

    Optional<SalesRecord> findByRecordDate(LocalDate recordDate);

    List<SalesRecord> findByRecordDateBetweenOrderByRecordDateAsc(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(sr.totalRevenue), 0) FROM SalesRecord sr")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(sr.grossProfit), 0) FROM SalesRecord sr")
    BigDecimal getTotalProfit();

    @Query("SELECT sr FROM SalesRecord sr ORDER BY sr.recordDate DESC")
    List<SalesRecord> findRecentSalesRecords();
}
