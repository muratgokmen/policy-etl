package com.etl.policy.parser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrPatterns {

    // Compiled patterns cache for performance
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();
    
    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
    };

    public static String pick(String text, String labelRegex) {
        Pattern pattern = PATTERN_CACHE.computeIfAbsent(labelRegex, 
            regex -> Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL));
        Matcher m = pattern.matcher(text);
        if (m.find()) return m.group(1).trim();
        return null;
    }

    /**
     * @deprecated Use ValueNormalizer.parseDate() instead
     */
    @Deprecated
    public static LocalDate pickDate(String text, String labelRegex) {
        String raw = pick(text, labelRegex);
        return com.etl.policy.util.ValueNormalizer.parseDate(raw);
    }

    /**
     * @deprecated Use ValueNormalizer.parseIntSafe() instead
     */
    @Deprecated
    public static Integer pickInt(String text, String labelRegex) {
        String raw = pick(text, labelRegex);
        return com.etl.policy.util.ValueNormalizer.parseIntSafe(raw);
    }

    /**
     * @deprecated Use ValueNormalizer.parseMoney() instead
     */
    @Deprecated
    public static BigDecimal pickMoney(String text, String labelRegex) {
        String raw = pick(text, labelRegex);
        if (raw == null) return null;
        return com.etl.policy.util.ValueNormalizer.parseMoney(raw);
    }

    /**
     * @deprecated Use ValueNormalizer.parseMoney() instead
     */
    @Deprecated
    public static BigDecimal parseMoney(String s) {
        return com.etl.policy.util.ValueNormalizer.parseMoney(s);
    }

    /**
     * @deprecated Use RegexUtil.pickBlockValue() instead
     */
    @Deprecated
    public static String pickAfterColon(String text, String label) {
        return com.etl.policy.util.RegexUtil.pickBlockValue(text, label).orElse(null);
    }

}
