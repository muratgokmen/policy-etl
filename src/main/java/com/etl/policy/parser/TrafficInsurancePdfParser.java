package com.etl.policy.parser;

import com.etl.policy.dto.insurance.TrafficInsuranceCoverageDto;
import com.etl.policy.dto.insurance.TrafficInsuranceOfferDto;
import com.etl.policy.dto.insurance.TrafficInsuranceOfferVehicleDto;
import com.etl.policy.dto.insurance.TrafficInsurancePremiumDto;
import com.etl.policy.enums.CoverageKey;
import com.etl.policy.enums.PremiumKey;
import com.etl.policy.enums.InsuranceOfferDocumentLabel;
import com.etl.policy.parser.extraction.FieldExtractionService;
import com.etl.policy.parser.section.OfferHeaderParser;
import com.etl.policy.parser.section.PartyInfoParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Architecture:
 * - Orchestrates parsing across multiple specialized parsers
 * - Each section (header, parties, vehicle, etc.) has dedicated parser
 * - Field extraction abstracted to reusable service
 */
@Component
@RequiredArgsConstructor
public class TrafficInsurancePdfParser implements PdfParser<TrafficInsuranceOfferDto> {

    private final OfferHeaderParser offerHeaderParser;
    private final PartyInfoParser partyInfoParser;
    private final FieldExtractionService extractionService;
    
    private static final short DEFAULT_CONFIDENCE = 100;

    /**
     * Main parsing method - orchestrates section parsers
     * 
     * @param text extracted PDF text
     * @return fully populated TrafficInsuranceOfferDto with nested children
     */
    @Override
    public TrafficInsuranceOfferDto parse(String text) {
        var header = offerHeaderParser.parse(text);
        var parties = partyInfoParser.parse(text);
        var vehicle = parseVehicle(text);
        var coverages = parseCoverages(text);
        var premiums = parsePremiums(text);
        
        return new TrafficInsuranceOfferDto(
                // header
                header.offerNo(),
                header.endorsementNo(),
                header.issueDate(),
                header.startDate(),
                header.endDate(),
                header.dayCount(),
                DEFAULT_CONFIDENCE,
                parties.insurerName(),
                parties.insurerAddress(),
                parties.agentName(),
                parties.agentRegistryNo(),
                parties.agentAddress(),
                parties.customerName(),
                parties.customerIdMasked(),
                vehicle,
                coverages,
                premiums
        );
    }

    /**
     * Parse vehicle information
     * TODO: Extract to VehicleParser for better OCP compliance
     */
    private TrafficInsuranceOfferVehicleDto parseVehicle(String text) {
        return new TrafficInsuranceOfferVehicleDto(
                extractionService.extractString(text, InsuranceOfferDocumentLabel.PLATE),
                extractionService.extractString(text, InsuranceOfferDocumentLabel.BRAND),
                extractionService.extractString(text, InsuranceOfferDocumentLabel.TYPE),
                extractionService.extractString(text, InsuranceOfferDocumentLabel.ENGINE_NO),
                extractionService.extractString(text, InsuranceOfferDocumentLabel.CHASSIS_NO),
                extractionService.extractInt(text, InsuranceOfferDocumentLabel.MODEL_YEAR),
                extractionService.extractDate(text, InsuranceOfferDocumentLabel.REGISTRATION_DATE),
                extractionService.extractString(text, InsuranceOfferDocumentLabel.USAGE_TYPE),
                extractionService.extractInt(text, InsuranceOfferDocumentLabel.SEAT_COUNT),
                extractionService.extractString(text, InsuranceOfferDocumentLabel.STEP)
        );
    }

    /**
     * Parse coverage information
     * TODO: Extract to CoverageParser for better OCP compliance
     */
    private List<TrafficInsuranceCoverageDto> parseCoverages(String text) {
        Map<CoverageKey, BigDecimal> map = new EnumMap<>(CoverageKey.class);
        for (CoverageKey key : CoverageKey.values()) {
            BigDecimal value = extractionService.extractMoney(text, key.label);
            if (value != null) {
                map.put(key, value);
            }
        }
        
        List<TrafficInsuranceCoverageDto> list = new ArrayList<>(map.size());
        map.forEach((k, v) -> list.add(new TrafficInsuranceCoverageDto(k.name(), v)));
        return list;
    }

    /**
     * Parse premium information
     * TODO: Extract to PremiumParser for better OCP compliance
     */
    private List<TrafficInsurancePremiumDto> parsePremiums(String text) {
        Map<PremiumKey, BigDecimal> map = new EnumMap<>(PremiumKey.class);
        for (PremiumKey key : PremiumKey.values()) {
            BigDecimal value = extractionService.extractMoney(text, key.label);
            if (value != null) {
                map.put(key, value);
            }
        }
        
        List<TrafficInsurancePremiumDto> list = new ArrayList<>(map.size());
        map.forEach((k, v) -> list.add(new TrafficInsurancePremiumDto(k.name(), v)));
        return list;
    }
}
