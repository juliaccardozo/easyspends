package com.jcardozo.easyspends.controllers;

import com.jcardozo.easyspends.entities.Category;
import com.jcardozo.easyspends.entities.Product;
import com.jcardozo.easyspends.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Gerenciamento de Produtos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {
    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(
            summary = "Get products",
            description = "List all registered products, being able to filter by one or more categories."
    )
    public ResponseEntity<List<Product>> getProducts(@RequestParam(name = "category", required = false) List<String> categories) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProducts(categories));
    }

    @PutMapping("/{id}/category")
    @Operation(
            summary = "Update product category",
            description = "Updates the category of a product that is passed as a parameter."
    )
    public ResponseEntity<Integer> updateCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProductCategory(id, category));
    }
}
