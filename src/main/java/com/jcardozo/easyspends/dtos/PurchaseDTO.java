package com.jcardozo.easyspends.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseDTO(
        Long id,
        String accessKey,
        String stablishmentName,
        String cnpj,
        LocalDateTime issueDate,
        BigDecimal totalPayment
) {}

