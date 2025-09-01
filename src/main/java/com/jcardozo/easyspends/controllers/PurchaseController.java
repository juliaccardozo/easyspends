package com.jcardozo.easyspends.controllers;

import com.jcardozo.easyspends.dtos.PurchaseDTO;
import com.jcardozo.easyspends.entities.Purchase;
import com.jcardozo.easyspends.exceptions.PurchaseException;
import com.jcardozo.easyspends.services.NfceScraperService;
import com.jcardozo.easyspends.services.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/purchases")
@Tag(name = "Purchases", description = "Purchase Management")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PurchaseController {
    public final PurchaseService purchaseService;
    public final NfceScraperService nfceScraperService;

    public PurchaseController(PurchaseService purchaseService, NfceScraperService nfceScraperService) {
        this.purchaseService = purchaseService;
        this.nfceScraperService = nfceScraperService;
    }

    @PostMapping("/import")
    @Operation(
            summary = "Import purchase",
            description = "Import purchases from the NFC-e URL. The url attribute in the request body is used to import."
    )
    public ResponseEntity<Object> importPurchase(@RequestBody String jsonBody) {
        var purchase = nfceScraperService.extract(jsonBody);
        if (purchaseService.isPurchaseRegistered(purchase.getAccessKey())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Purchase already registered!");
        }
        purchaseService.savePurchase(purchase);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PurchaseDTO(purchase.getId(), purchase.getAccessKey(), purchase.getStablishmentName(), purchase.getCnpj(), purchase.getIssueDate(), purchase.getTotalPayment()));
    }

    @GetMapping()
    @Operation(
            summary = "Get purchases",
            description = "List purchases filtered by period."
    )
    public ResponseEntity<List<PurchaseDTO>> getPurchases(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(purchaseService.getPurchasesByPeriod(startDate, endDate));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get details about a specific purchase.",
            description = "Returns the purchase of the Id passed as a parameter."
    )
    public ResponseEntity<Object> getPurchaseById(@PathVariable Long id) {
        Optional<Purchase> purchaseOptional = purchaseService.getPurchaseById(id);
        return purchaseOptional
                .<ResponseEntity<Object>>map(p -> ResponseEntity.status(HttpStatus.OK)
                        .body(purchaseOptional))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PurchaseException(HttpStatus.NOT_FOUND, "Purchase not found.")));
    }
}
