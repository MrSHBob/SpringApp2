package com.app.springapp2.model;

import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PositionRequest {
    private Long id;
    private Long product;
    private Double mass;
    private LocalDate date;
}
