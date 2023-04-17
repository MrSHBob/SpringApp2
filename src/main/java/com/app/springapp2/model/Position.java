package com.app.springapp2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "POSITION_TBL")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
//    @Column(name = "OWNER_ID")
    private User owner;

    @ManyToOne
//    @Column(name = "PRODUCT_ID")
    private Product product;

    private LocalDate date;

    private Double mass;

    private Double sum;// (mass*product.price);

    @Column(name = "TOTAL_B")
    private Double calcB;// (mass*product.b);

    @Column(name = "TOTAL_J")
    private Double calcJ;// (mass*product.j);

    @Column(name = "TOTAL_U")
    private Double calcU;// (mass*product.u);

    @Column(name = "TOTAL_KKL")
    private Double totalenergy;// (mass*product.kkl);

    private LocalDateTime created;

    @Column(name = "LAST_UPDATE_DATE")
    private LocalDateTime lastUpdateDate;

    private Integer version;
}
