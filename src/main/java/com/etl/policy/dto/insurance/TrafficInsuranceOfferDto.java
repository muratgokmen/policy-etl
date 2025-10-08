package com.etl.policy.dto.insurance;

import java.time.LocalDate;
import java.util.List;

/**
 * Trafik sigortası teklifi aggregate root DTO.
 * Entity yapısıyla uyumlu olarak vehicle, coverages ve premiums nested içerir.
 * 
 * Record kullanımı - immutable, compact constructor ile instance oluşturulur.
 * MapStruct otomatik olarak canonical constructor'ı kullanır.
 */
public record TrafficInsuranceOfferDto(
        // core
        String offerNo,
        String endorsementNo,
        LocalDate issueDate,
        LocalDate startDate,
        LocalDate endDate,
        Integer dayCount,
        Short sourceConfidence,
        // snapshots / parties
        String insurerName,
        String insurerAddress,
        String agentName,
        String agentRegistryNo,
        String agentAddress,
        String customerName,
        String customerIdMasked,
        // nested children (aggregate pattern)
        TrafficInsuranceOfferVehicleDto vehicle,
        List<TrafficInsuranceCoverageDto> coverages,
        List<TrafficInsurancePremiumDto> premiums
) {}
