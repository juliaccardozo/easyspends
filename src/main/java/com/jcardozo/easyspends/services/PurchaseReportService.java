package com.jcardozo.easyspends.services;

import com.jcardozo.easyspends.dtos.PurchaseDTO;
import com.jcardozo.easyspends.dtos.PurchaseReportDTO;
import com.jcardozo.easyspends.entities.Purchase;
import com.jcardozo.easyspends.exceptions.PurchaseException;
import com.jcardozo.easyspends.repositories.PurchaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseReportService {
    private final PurchaseRepository purchaseRepository;

    public PurchaseReportService(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public PurchaseReportDTO getReport(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;

        switch (period.toLowerCase()) {
            case "last24h" -> start = now.minusHours(24);
            case "last7d"  -> start = now.minusDays(7);
            case "last30d" -> start = now.minusDays(30);
            default -> throw new PurchaseException(HttpStatus.BAD_REQUEST, "Invalid period: " + period);
        }

        List<Purchase> purchases = purchaseRepository.findByIssueDateBetween(start, now);

        BigDecimal total = purchases.stream().map(Purchase::getTotalPayment).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PurchaseDTO> summaries = purchases.stream().map(p -> new PurchaseDTO(
                p.getId(),
                p.getAccessKey(),
                p.getStablishmentName(),
                p.getCnpj(),
                p.getIssueDate(),
                p.getTotalPayment()
        )).toList();

        return new PurchaseReportDTO(total, purchases.size(), summaries);
    }
}
