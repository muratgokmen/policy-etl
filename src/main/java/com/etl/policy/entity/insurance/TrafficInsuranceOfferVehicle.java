package com.etl.policy.entity.insurance;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "TRAFFIC_INSURANCE_OFFER_VEHICLE", uniqueConstraints = @UniqueConstraint(columnNames = "chassis_no"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrafficInsuranceOfferVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id")
    private TrafficInsuranceOffer header;

    private String plate;

    private String brand;

    private String type;

    private String engineNo;

    @Column(name = "chassis_no")
    private String chassisNo;

    private Integer modelYear;

    private LocalDate registrationDate;

    private String usageType;

    private Integer seatCount;

    private String step;

}