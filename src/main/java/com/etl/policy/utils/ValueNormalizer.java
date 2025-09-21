package com.etl.policy.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValueNormalizer {
    private ValueNormalizer() {
    }

    public static BigDecimal parseMoney(String s) {
        if (s == null) return null;
        // "300,000.00" ve "300.000,00" ikisini de destekle
        String t = s.replaceAll("\\s", "")
                .replace(".", "")        // nokta binlik ayırıcıyı kaldır
                .replace(",", ".");      // virgülü nokta yap
        t = t.replaceAll("[^0-9.\\-]", "");
        if (t.isEmpty()) return null;
        return new BigDecimal(t);
    }

    public static Integer parseIntSafe(String s) {
        if (s == null) return null;
        Matcher m = Pattern.compile("(-?\\d+)").matcher(s);
        return m.find() ? Integer.valueOf(m.group(1)) : null;
    }

    public static LocalDate parseDate(String s) {
        if (s == null) return null;
        List<DateTimeFormatter> fs = List.of(
                DateTimeFormatter.ofPattern("dd/MM/uuuu"),
                DateTimeFormatter.ofPattern("dd.MM.uuuu"),
                DateTimeFormatter.ISO_LOCAL_DATE
        );
        for (DateTimeFormatter f : fs) {
            try {
                return LocalDate.parse(s.trim(), f);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
