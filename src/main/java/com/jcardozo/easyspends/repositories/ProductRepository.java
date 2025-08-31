package com.jcardozo.easyspends.repositories;

import com.jcardozo.easyspends.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCodeOrName(String code, String name);

    List<Product> findByCategoryNameIn(List<String> categories);

    @Modifying
    @Query(value = "UPDATE product SET category_id = :categoryId WHERE id = :id", nativeQuery = true)
    Integer updateProductCategory(@Param("id") Long id, @Param("categoryId") Long categoryId);

}
