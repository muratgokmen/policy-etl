package com.etl.policy.entity.insurance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "offer_header",
        uniqueConstraints = @UniqueConstraint(columnNames = {"offer_no", "endorsement_no"}))
public class OfferHeader {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_id")
    private PdfStore pdf;
    private String insurerName;
    private String insurerAddress;
    private String agentName;
    private String agentRegistryNo;
    private String agentAddress;
    private String customerName;
    private String customerIdMasked;
    private String offerNo;
    private String endorsementNo;
    private LocalDate issueDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer dayCount;
    private Short sourceConfidence;
}