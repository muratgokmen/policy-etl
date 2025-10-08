package com.etl.policy.entity.insurance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "TRAFFIC_INSURANCE_OFFER_PREMIUM",
        uniqueConstraints = @UniqueConstraint(columnNames = {"header_id", "premium_key"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrafficInsuranceOfferPremium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id")
    private TrafficInsuranceOffer header;

    private String premiumKey;

    private BigDecimal premiumValue;

}