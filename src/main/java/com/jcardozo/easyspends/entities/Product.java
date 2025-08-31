package com.jcardozo.easyspends.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "packaging_unit", length = 50, nullable = false)
    private String packagingUnit;

    @Column(name = "code", nullable = false)
    private String code;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;
}
