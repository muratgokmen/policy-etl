package com.etl.policy.entity.insurance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "offer_coverages",
        uniqueConstraints = @UniqueConstraint(columnNames = {"header_id", "coverage_key"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfferCoverage {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id")
    private OfferHeader header;
    private String coverageKey;
    private BigDecimal coverageValue;
}