package com.etl.policy.parser.extraction;

import java.util.Optional;

/**
 * Field extraction strategy interface
 * 
 * DIP compliance: Abstraction for field extraction
 * OCP compliance: New extraction strategies can be added
 */
public interface FieldExtractor {
    /**
     * Extract field value from text using given labels
     * 
     * @param text source text
     * @param labels label patterns to search for
     * @return extracted value if found
     */
    Optional<String> extract(String text, String... labels);
}

