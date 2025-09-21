package com.etl.policy.parser;

import com.etl.policy.dto.insurance.TrafficOfferDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.etl.policy.parser.TrPatterns.*;

@Component
public class TrafficOfferParser {

    public TrafficOfferDto parse(String t) {
        // --- Üst Kısım (başlık/özet tablo) ---
        // Satır: "Müşteri No. Acente No Teklif No Zeyil No Tanzim Tarihi Başlama Tarihi Bitiş Tarihi Gün"
        String gridRow = pick(t,
                "Müşteri No\\.[^\\n]*\\n([0-9 ]+\\S.*?)(?:\\n|$)");
        String offerNo = null, endorsementNo = null;
        LocalDate issueDate = null, startDate = null, endDate = null;
        Integer dayCount = null;
        if (gridRow != null) {
            // örn: "67061945 101010 301000224076017 0 14/09/2025 16/09/2025 16/09/2026 365"
            String[] parts = gridRow.trim().split("\\s+");
            if (parts.length >= 8) {
                offerNo = parts[2];
                endorsementNo = parts[3];
                issueDate = parseDate(parts[4]);
                startDate = parseDate(parts[5]);
                endDate = parseDate(parts[6]);
                dayCount = safeInt(parts[7]);
            }
        }

        // --- Sözleşme Tarafları ---
        // --- Sözleşme Tarafları ---
        String insurerName    = pickAfterColon(t, "Sigorta Şirketi");
        String insurerAddress = pickAfterColon(t, "Sigorta Şirketi Adresi");
        String agentName      = pickAfterColon(t, "Acente Ad/Ünvan");
        String agentRegNo     = pickAfterColon(t, "Acente Levha Kayıt No");
        String agentAddress   = pickAfterColon(t, "Acente Adresi");
        String customerName   = pickAfterColon(t, "Sigortalı Adı Soyadı/Ünvanı");
        String customerId     = pickAfterColon(t, "Kimlik Numarası");

// --- Araç Bilgileri ---  (aynı satırda çok etiket var → lookahead'li kullanım şart)
        String plate      = pickAfterColon(t, "Plaka");
        String brand      = pickAfterColon(t, "Marka");
        String engineNo   = pickAfterColon(t, "Motor No");
        String type       = pickAfterColon(t, "Tipi");
        String chassisNo  = pickAfterColon(t, "Şasi No");
        Integer modelYear = pickInt(t, "Model Yılı\\s*:\\s*([0-9]{4})"); // tek başına yıl olduğundan basit kalabilir
        LocalDate regDate = pickDate(t, "Tescil Tarihi\\s*:\\s*([0-9./]+)");
        String usageType  = pickAfterColon(t, "Kullanım Tarzı");
        Integer seatCount = pickInt(t, "Koltuk Adedi\\s*:\\s*([0-9]+)");
        String step       = pickAfterColon(t, "Basamak");


        // --- Teminatlar (soldaki tablo) ---
        Map<String, BigDecimal> coverages = new LinkedHashMap<>();
        putMoney(t, coverages, "Araç Başına Maddi", "Araç Başına Maddi\\s*([0-9.,]+)");
        putMoney(t, coverages, "Kaza Başına Maddi", "Kaza Başına Maddi\\s*([0-9.,]+)");
        putMoney(t, coverages, "Sakatlanma ve Ölüm - Kişi Başı", "Sakatlanma ve Ölüm - Kişi Başı\\s*([0-9.,]+)");
        putMoney(t, coverages, "Sakatlanma ve Ölüm - Kaza Başı", "Sakatlanma ve Ölüm - Kaza Başı\\s*([0-9.,]+)");
        putMoney(t, coverages, "Sağlık Giderleri - Kişi Başı", "Sağlık Giderleri - Kişi Başı\\s*([0-9.,]+)");
        putMoney(t, coverages, "Sağlık Giderleri - Kaza Başı", "Sağlık Giderleri - Kaza Başı\\s*([0-9.,]+)");
        putMoney(t, coverages, "Ferdi Kaza - Ölüm", "Ferdi Kaza - Ölüm\\s*([0-9.,]+)");
        putMoney(t, coverages, "Ferdi Kaza - Sürekli Sakatlılık", "Ferdi Kaza - Sürekli Sakatlılık\\s*([0-9.,]+)");
        putMoney(t, coverages, "Hukuksal Koruma", "Hukuksal Koruma\\s*([0-9.,]+)");
        putMoney(t, coverages, "Trafik Kazası Sonucu Yatarak Tedavi", "Trafik Kazası Sonucu Yatarak Tedavi\\s*([0-9.,]+)");

        // --- Prim Bilgileri (sağdaki tablo) ---
        Map<String, BigDecimal> premiums = new LinkedHashMap<>();
        putMoney(t, premiums, "Trafik Primi", "Trafik Primi\\s*([0-9.,]+)");
        putMoney(t, premiums, "SGK Primi", "SGK Primi\\s*([0-9.,]+)");
        putMoney(t, premiums, "Ek Teminat Prim", "Ek Teminat Prim\\s*([0-9.,]+)");
        putMoney(t, premiums, "Net Prim", "Net Prim\\s*([0-9.,]+)");
        putMoney(t, premiums, "Gider Vergisi", "Gider Vergisi\\s*([0-9.,]+)");
        putMoney(t, premiums, "T.H.G.F", "T\\.H\\.G\\.F\\s*([0-9.,]+)");
        putMoney(t, premiums, "Güvence Hesabı", "Güvence Hesabı\\s*([0-9.,]+)");
        putMoney(t, premiums, "Brüt Prim", "Brüt Prim\\s*([0-9.,]+)");

        return new TrafficOfferDto(
                insurerName, insurerAddress, agentName, agentRegNo, agentAddress,
                customerName, customerId,
                offerNo, endorsementNo, issueDate, startDate, endDate, dayCount,
                plate, brand, type, engineNo, chassisNo, modelYear, regDate, usageType, seatCount, step,
                coverages, premiums
        );
    }

    private static void putMoney(String t, Map<String, BigDecimal> map, String key, String regex) {
        var val = pickMoney(t, regex);
        if (val != null) map.put(key, val);
    }

    private static LocalDate parseDate(String s) {
        try { return LocalDate.parse(s, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
        catch (Exception e) {
            try { return LocalDate.parse(s, java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")); }
            catch (Exception ignored) { return null; }
        }
    }

    private static Integer safeInt(String s) {
        try { return Integer.parseInt(s.replaceAll("\\D", "")); } catch (Exception e) { return null; }
    }
}
