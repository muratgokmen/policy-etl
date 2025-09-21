package com.etl.policy.parse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrPatterns {

    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
    };

    public static String pick(String text, String labelRegex) {
        Matcher m = Pattern.compile(labelRegex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL)
                .matcher(text);
        if (m.find()) return m.group(1).trim();
        return null;
    }

    public static LocalDate pickDate(String text, String labelRegex) {
        String raw = pick(text, labelRegex);
        if (raw == null) return null;
        raw = raw.trim();
        for (DateTimeFormatter f : DATE_FORMATS) {
            try { return LocalDate.parse(raw, f); } catch (Exception ignored) {}
        }
        return null;
    }

    public static Integer pickInt(String text, String labelRegex) {
        String raw = pick(text, labelRegex);
        if (raw == null) return null;
        raw = raw.replaceAll("[^0-9-]", "");
        if (raw.isEmpty()) return null;
        try { return Integer.parseInt(raw); } catch (Exception e) { return null; }
    }

    public static BigDecimal pickMoney(String text, String labelRegex) {
        String raw = pick(text, labelRegex);
        if (raw == null) return null;
        return parseMoney(raw);
    }

    /** 1.234,56 / 1,234.56 / 1234,56 / 1234 -> BigDecimal */
    public static BigDecimal parseMoney(String s) {
        String t = s.replaceAll("\\s+", "");
        // Sona yakın para kalıbını çek
        Matcher m = Pattern.compile("([0-9][0-9.,]*)").matcher(t);
        String num = null;
        while (m.find()) num = m.group(1); // son görüleni al
        if (num == null) return null;

        // Türkçe tarzı "1.234,56"
        if (num.contains(",") && (num.lastIndexOf(',') > num.lastIndexOf('.'))) {
            num = num.replace(".", "").replace(",", ".");
        } else {
            num = num.replace(",", "");
        }
        try { return new BigDecimal(num); } catch (Exception e) { return null; }
    }

    public static String pickAfterColon(String text, String label) {
        // label : value   (bir SONRAKİ "ETİKET :" görünce veya satır sonuna gelince dur)
        String regex =
                Pattern.quote(label) +
                        "\\s*:\\s*(.+?)(?=\\s+[\\p{Lu}0-9ÇĞİÖŞÜ()/'`.-]+\\s*:\\s*|\\r?\\n|$)";
        Matcher m = Pattern.compile(regex,
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL)
                .matcher(text);
        return m.find() ? m.group(1).trim() : null;
    }

}
