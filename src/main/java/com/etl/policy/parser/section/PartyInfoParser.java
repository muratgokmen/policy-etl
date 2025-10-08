package com.etl.policy.parser.section;

import com.etl.policy.enums.InsuranceOfferDocumentLabel;
import com.etl.policy.parser.extraction.FieldExtractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyInfoParser {
    
    private final FieldExtractionService extractionService;
    
    public PartyInfo parse(String text) {
        return PartyInfo.builder()
                .insurerName(extractionService.extractString(text, InsuranceOfferDocumentLabel.INSURER_NAME))
                .insurerAddress(extractionService.extractString(text, InsuranceOfferDocumentLabel.INSURER_ADDRESS))
                .agentName(extractionService.extractString(text, InsuranceOfferDocumentLabel.AGENT_NAME))
                .agentRegistryNo(extractionService.extractString(text, InsuranceOfferDocumentLabel.AGENT_REG_NO))
                .agentAddress(extractionService.extractString(text, InsuranceOfferDocumentLabel.AGENT_ADDRESS))
                .customerName(extractionService.extractString(text, InsuranceOfferDocumentLabel.CUSTOMER_NAME))
                .customerIdMasked(extractionService.extractString(text, InsuranceOfferDocumentLabel.CUSTOMER_ID))
                .build();
    }
    
    /**
     * Immutable data holder - parsed party info
     */
    @lombok.Builder
    public record PartyInfo(
            String insurerName,
            String insurerAddress,
            String agentName,
            String agentRegistryNo,
            String agentAddress,
            String customerName,
            String customerIdMasked
    ) {}
}

