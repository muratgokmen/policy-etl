package com.etl.policy.parser.extraction;

import com.etl.policy.util.RegexUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Regex-based field extractor implementation
 * 
 * Uses RegexUtil for pattern matching and value extraction
 */
@Component
public class RegexFieldExtractor implements FieldExtractor {
    
    @Override
    public Optional<String> extract(String text, String... labels) {
        for (String label : labels) {
            Optional<String> value = RegexUtil.pickBlockValue(text, label);
            if (value.isPresent() && !value.get().isBlank()) {
                return Optional.of(value.get().trim());
            }
        }
        return Optional.empty();
    }
}

