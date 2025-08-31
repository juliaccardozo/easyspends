package com.jcardozo.easyspends.services;

import com.jcardozo.easyspends.dtos.PurchaseDTO;
import com.jcardozo.easyspends.entities.Product;
import com.jcardozo.easyspends.entities.Purchase;
import com.jcardozo.easyspends.entities.PurchaseItem;
import com.jcardozo.easyspends.repositories.ProductRepository;
import com.jcardozo.easyspends.repositories.PurchaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    public final PurchaseRepository purchaseRepository;
    public final ProductRepository productRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, ProductRepository productRepository) {
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void savePurchase(Purchase purchase) {
        for (PurchaseItem purchaseItem : purchase.getItems()) {
            Product product = productRepository
                    .findByCodeOrName(purchaseItem.getProduct().getCode(), purchaseItem.getProduct().getName())
                    .orElseGet(() -> productRepository.save(purchaseItem.getProduct()));

            purchaseItem.setProduct(product);
            purchaseItem.setPurchase(purchase);
        }

        purchaseRepository.save(purchase);
    }

    public boolean isPurchaseRegistered(String accessKey) {
        return purchaseRepository.findByAccessKey(accessKey).isPresent();
    }

    public List<PurchaseDTO> getPurchasesByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return purchaseRepository.findPurchasesByPeriod(startDate, endDate);
    }

    public Optional<Purchase> getPurchaseById(Long purchaseId) {
        return purchaseRepository.findById(purchaseId);
    }
}
