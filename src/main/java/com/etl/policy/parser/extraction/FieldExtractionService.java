package com.etl.policy.parser.extraction;

import com.etl.policy.enums.InsuranceOfferDocumentLabel;
import com.etl.policy.util.ValueNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Field extraction and normalization service
 * 
 * Provides type-safe extraction methods for common field types:
 * - String
 * - LocalDate
 * - Integer, Long
 * - BigDecimal (money)
 * - Boolean
 * 
 * All extraction methods delegate to FieldExtractor and normalize values.
 */
@Service
@RequiredArgsConstructor
public class FieldExtractionService {
    
    private final FieldExtractor fieldExtractor;

    public String extractString(String text, InsuranceOfferDocumentLabel label) {
        return fieldExtractor.extract(text, label.aliases).orElse(null);
    }

    public LocalDate extractDate(String text, InsuranceOfferDocumentLabel label) {
        return fieldExtractor.extract(text, label.aliases)
                .map(ValueNormalizer::parseDate)
                .orElse(null);
    }
    
    public Integer extractInt(String text, InsuranceOfferDocumentLabel label) {
        return fieldExtractor.extract(text, label.aliases)
                .map(ValueNormalizer::parseIntSafe)
                .orElse(null);
    }

    public BigDecimal extractMoney(String text, String label) {
        return fieldExtractor.extract(text, label)
                .map(ValueNormalizer::parseMoney)
                .orElse(null);
    }

}

