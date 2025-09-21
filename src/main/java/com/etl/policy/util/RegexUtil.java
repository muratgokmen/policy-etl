package com.etl.policy.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexUtil {
    
    // Compiled patterns for better performance
    private static final Pattern LABEL_PATTERN_CACHE = Pattern.compile(
        "([^:]+)\\s*:\\s*(.+?)\\s*(?=\\n|\\Z|\\s{2,}[^:\\n]{2,60}:)",
        Pattern.DOTALL | Pattern.UNICODE_CASE
    );
    
    private RegexUtil() {
    }

    // "Etiket: Değer" biçiminde tek satır yakala
    public static Optional<String> pickLineValue(String fullText, String label) {
        // ^\\s*<label>\\s*:\\s*(.+)$  (DOTALL YOK, satır bazlı)
        String pattern = "^\\s*" + Pattern.quote(label) + "\\s*:\\s*(.+)\\s*$";
        Pattern p = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(fullText);
        if (m.find()) return Optional.of(m.group(1).trim());
        return Optional.empty();
    }

    // "Etiket: Değer" ve sonraki etikete/satır sonuna kadar (çok satırlı değer için)
    public static Optional<String> pickBlockValue(String fullText, String label) {
        // Bir sonraki alan sınırı: satır sonu ya da iki boşluk + "Xxx:" paterni
        String next = "(?:\\n|\\Z|\\s{2,}[^:\\n]{2,60}:)";
        String pattern = Pattern.quote(label) + "\\s*:\\s*(.+?)\\s*(?=" + next + ")";
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(fullText);
        if (m.find()) return Optional.of(m.group(1).trim());
        return Optional.empty();
    }
}
