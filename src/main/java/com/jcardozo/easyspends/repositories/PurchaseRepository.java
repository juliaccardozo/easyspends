package com.jcardozo.easyspends.repositories;

import com.jcardozo.easyspends.dtos.PurchaseDTO;
import com.jcardozo.easyspends.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByAccessKey(String accessKey);

    @Query("""
        SELECT new com.jcardozo.easyspends.dtos.PurchaseDTO(
                p.id, p.accessKey, p.stablishmentName, p.cnpj, p.issueDate, p.totalPayment
            )
            FROM Purchase p
            WHERE (:startDate IS NULL OR p.issueDate >= :startDate)
              AND (:endDate IS NULL OR p.issueDate <= :endDate)
            ORDER BY p.issueDate DESC
    """)
    List<PurchaseDTO> findPurchasesByPeriod(LocalDateTime startDate, LocalDateTime endDate);

    List<Purchase> findByIssueDateBetween(LocalDateTime start, LocalDateTime end);
}