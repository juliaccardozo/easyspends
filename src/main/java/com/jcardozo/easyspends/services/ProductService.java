package com.jcardozo.easyspends.services;

import com.jcardozo.easyspends.entities.Category;
import com.jcardozo.easyspends.entities.Product;
import com.jcardozo.easyspends.exceptions.CategoryException;
import com.jcardozo.easyspends.exceptions.ProductException;
import com.jcardozo.easyspends.repositories.CategoryRepository;
import com.jcardozo.easyspends.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Integer updateProductCategory(Long id, Category category) {
        if (categoryRepository.findById(category.getId()).isEmpty()) {
            throw new CategoryException(HttpStatus.NOT_FOUND, "Category not found.");
        }

        if (productRepository.findById(id).isEmpty()) {
            throw new ProductException(HttpStatus.NOT_FOUND, "Product not found.");
        }

        return productRepository.updateProductCategory(id,  category.getId());
    }

    public List<Product> getProducts(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return productRepository.findAll();
        }

        return productRepository.findByCategoryNameIn(categories);
    }
}
