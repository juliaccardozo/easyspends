package com.jcardozo.easyspends.controllers;

import com.jcardozo.easyspends.dtos.PurchaseReportDTO;
import com.jcardozo.easyspends.services.PurchaseReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Expenses Reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PurchaseReportController {
    private final PurchaseReportService purchaseReportService;

    public PurchaseReportController(PurchaseReportService purchaseReportService) {
        this.purchaseReportService = purchaseReportService;
    }

    @GetMapping("/purchases")
    public ResponseEntity<PurchaseReportDTO> getReport(
            @RequestParam(defaultValue = "last30d") String period
    ) {
        return ResponseEntity.ok(purchaseReportService.getReport(period));
    }
}
