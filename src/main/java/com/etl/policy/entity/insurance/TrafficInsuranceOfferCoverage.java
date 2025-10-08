package com.etl.policy.entity.insurance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "TRAFFIC_INSURANCE_OFFER_COVERAGE",
        uniqueConstraints = @UniqueConstraint(columnNames = {"header_id", "coverage_key"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrafficInsuranceOfferCoverage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id")
    private TrafficInsuranceOffer header;

    private String coverageKey;

    private BigDecimal coverageValue;

}