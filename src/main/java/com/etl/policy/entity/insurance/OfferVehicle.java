package com.etl.policy.entity.insurance;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "offer_vehicle", uniqueConstraints = @UniqueConstraint(columnNames = "chassis_no"))
public class OfferVehicle {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "header_id")
    private OfferHeader header;
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