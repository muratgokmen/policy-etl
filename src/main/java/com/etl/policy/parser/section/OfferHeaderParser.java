package com.etl.policy.parser.section;

import com.etl.policy.enums.InsuranceOfferDocumentLabel;
import com.etl.policy.parser.extraction.FieldExtractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OfferHeaderParser {
    
    private final FieldExtractionService extractionService;
    
    public OfferHeader parse(String text) {
        return OfferHeader.builder()
                .offerNo(extractionService.extractString(text, InsuranceOfferDocumentLabel.OFFER_NO))
                .endorsementNo(extractionService.extractString(text, InsuranceOfferDocumentLabel.ENDORSEMENT_NO))
                .issueDate(extractionService.extractDate(text, InsuranceOfferDocumentLabel.ISSUE_DATE))
                .startDate(extractionService.extractDate(text, InsuranceOfferDocumentLabel.START_DATE))
                .endDate(extractionService.extractDate(text, InsuranceOfferDocumentLabel.END_DATE))
                .dayCount(extractionService.extractInt(text, InsuranceOfferDocumentLabel.DAY_COUNT))
                .build();
    }
    
    /**
     * Immutable data holder - parsed offer header
     */
    @lombok.Builder
    public record OfferHeader(
            String offerNo,
            String endorsementNo,
            LocalDate issueDate,
            LocalDate startDate,
            LocalDate endDate,
            Integer dayCount
    ) {}
}

