package com.etl.policy.dto.insurance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record TrafficOfferDto(
        // Üst bilgiler
        String insurerName,
        String insurerAddress,
        String agentName,
        String agentRegistryNo,
        String agentAddress,
        String customerName,
        String customerIdMasked,

        // Poliçe
        String offerNo,
        String endorsementNo,
        LocalDate issueDate,
        LocalDate startDate,
        LocalDate endDate,
        Integer dayCount,

        // Araç
        String plate,
        String brand,
        String type,
        String engineNo,
        String chassisNo,
        Integer modelYear,
        LocalDate registrationDate,
        String usageType,
        Integer seatCount,
        String step,

        // Prim & Teminatlar
        Map<String, BigDecimal> coverages,   // "Araç Başına Maddi" -> 300000.00
        Map<String, BigDecimal> premiums     // "Brüt Prim" -> 11585.14 vb.
) {
}
