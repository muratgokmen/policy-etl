package com.etl.policy.dto.insurance;

import java.time.LocalDate;

/**
 * Araç bilgileri DTO - immutable record.
 */
public record TrafficInsuranceOfferVehicleDto(
  String plate, String brand, String type,
  String engineNo, String chassisNo,
  Integer modelYear, LocalDate registrationDate,
  String usageType, Integer seatCount, String step
) {}