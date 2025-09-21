package com.etl.policy.entity.insurance;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "offer_premiums",
        uniqueConstraints = @UniqueConstraint(columnNames = {"header_id", "premium_key"}))
public class OfferPremium {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id")
    private OfferHeader header;
    private String premiumKey;
    private BigDecimal premiumValue;
}