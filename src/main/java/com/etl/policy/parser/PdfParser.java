package com.etl.policy.parser;

/**
 * PDF parser interface - DIP compliance
 * Farklı PDF tiplerini parse etmek için ortak contract
 */
public interface PdfParser<T> {
    T parse(String text);
}

