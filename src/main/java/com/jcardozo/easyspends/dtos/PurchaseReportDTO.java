package com.jcardozo.easyspends.dtos;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseReportDTO(
        BigDecimal totalSpent,
        long totalPurchases,
        List<PurchaseDTO> purchases
) {}
