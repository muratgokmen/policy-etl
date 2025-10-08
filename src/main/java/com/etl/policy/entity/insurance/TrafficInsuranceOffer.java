package com.etl.policy.entity.insurance;

import com.etl.policy.entity.document.PdfStore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TRAFFIC_INSURANCE_OFFER",
        uniqueConstraints = @UniqueConstraint(columnNames = {"offer_no", "endorsement_no"}))
public class TrafficInsuranceOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrafficInsuranceOfferVehicle> vehicles = new ArrayList<>();

    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrafficInsuranceOfferCoverage> coverages = new ArrayList<>();

    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrafficInsuranceOfferPremium> premiums = new ArrayList<>();

    // Helper methods for bidirectional relationship management
    public void addVehicle(TrafficInsuranceOfferVehicle vehicle) {
        vehicles.add(vehicle);
        vehicle.setHeader(this);
    }

    public void addCoverage(TrafficInsuranceOfferCoverage coverage) {
        coverages.add(coverage);
        coverage.setHeader(this);
    }

    public void addPremium(TrafficInsuranceOfferPremium premium) {
        premiums.add(premium);
        premium.setHeader(this);
    }

}