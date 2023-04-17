package com.app.springapp2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "PRODUCT_TBL")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique=true)
    private String name;

    private Double price;

    private Double b;

    private Double j;

    private Double u;

    private Double kkl;

    private LocalDateTime created;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime lastUpdateDate;

    private Integer version;
}
