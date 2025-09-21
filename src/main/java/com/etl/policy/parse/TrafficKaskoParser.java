package com.etl.policy.parse;

import com.etl.policy.utils.RegexUtil;
import com.etl.policy.utils.ValueNormalizer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class TrafficKaskoParser {

  public ParsedOffer parse(String text) {
    ParsedOffer out = new ParsedOffer();

    out.insurerName = RegexUtil.pickBlockValue(text, "Sigorta Şirketi")
        .or(() -> RegexUtil.pickBlockValue(text, "Sigortacı"))
        .orElse(null);

    out.insurerAddress = RegexUtil.pickBlockValue(text, "Sigorta Şirketi Adresi").orElse(null);
    out.agentName = RegexUtil.pickBlockValue(text, "Acente Ad/Ünvan").orElse(null);
    out.agentRegistryNo = RegexUtil.pickBlockValue(text, "Acente Levha Kayıt No").orElse(null);
    out.agentAddress = RegexUtil.pickBlockValue(text, "Acente Adresi").orElse(null);

    out.customerName = RegexUtil.pickBlockValue(text, "Sigortalı Adı Soyadı/Ünvanı")
        .or(() -> RegexUtil.pickBlockValue(text, "Sigorta Ettiren"))
        .orElse(null);
    out.customerIdMasked = RegexUtil.pickBlockValue(text, "Kimlik Numarası").orElse(null);

    out.offerNo = RegexUtil.pickBlockValue(text, "Teklif No").orElse(null);
    out.endorsementNo = RegexUtil.pickBlockValue(text, "Zeyil No").orElse(null);
    out.issueDate = ValueNormalizer.parseDate(RegexUtil.pickBlockValue(text, "Tanzim Tarihi").orElse(null));
    out.startDate = ValueNormalizer.parseDate(RegexUtil.pickBlockValue(text, "Başlama Tarihi").orElse(null));
    out.endDate = ValueNormalizer.parseDate(RegexUtil.pickBlockValue(text, "Bitiş Tarihi").orElse(null));
    out.dayCount = ValueNormalizer.parseIntSafe(RegexUtil.pickBlockValue(text, "Gün").orElse(null));

    out.plate = RegexUtil.pickBlockValue(text, "Plaka").orElse(null);
    out.brand = RegexUtil.pickBlockValue(text, "Marka").orElse(null);
    out.type  = RegexUtil.pickBlockValue(text, "Tipi").orElse(null);
    out.engineNo = RegexUtil.pickBlockValue(text, "Motor No").orElse(null);
    out.chassisNo = RegexUtil.pickBlockValue(text, "Şasi No").orElse(null);
    out.modelYear = ValueNormalizer.parseIntSafe(RegexUtil.pickBlockValue(text, "Model Yılı").orElse(null));
    out.registrationDate = ValueNormalizer.parseDate(RegexUtil.pickBlockValue(text, "Tescil Tarihi").orElse(null));
    out.usageType = RegexUtil.pickBlockValue(text, "Kullanım Tarzı").orElse(null);
    out.seatCount = ValueNormalizer.parseIntSafe(RegexUtil.pickBlockValue(text, "Koltuk Adedi").orElse(null));
    out.step = RegexUtil.pickBlockValue(text, "Basamak").orElse(null);

    // Teminat blokları
    Map<String, BigDecimal> coverages = new LinkedHashMap<>();
    putMoney(coverages, "Araç Başına Maddi", RegexUtil.pickBlockValue(text, "Araç Başına Maddi").orElse(null));
    putMoney(coverages, "Kaza Başına Maddi", RegexUtil.pickBlockValue(text, "Kaza Başına Maddi").orElse(null));
    putMoney(coverages, "Sakatlanma ve Ölüm - Kişi Başı", RegexUtil.pickBlockValue(text, "Sakatlanma ve Ölüm - Kişi Başı").orElse(null));
    // ... diğerleri aynı
    out.coverages = coverages;

    // Primler
    Map<String, BigDecimal> premiums = new LinkedHashMap<>();
    putMoney(premiums, "Trafik Primi", RegexUtil.pickBlockValue(text, "Trafik Primi").orElse(null));
    putMoney(premiums, "SGK Primi", RegexUtil.pickBlockValue(text, "SGK Primi").orElse(null));
    // ... diğerleri
    out.premiums = premiums;

    out.sourceConfidence = 100; // LLM yok → tamamen kural tabanlı
    return out;
  }

  private void putMoney(Map<String, BigDecimal> map, String key, String raw) {
    BigDecimal v = ValueNormalizer.parseMoney(raw);
    if (v != null) map.put(key, v);
  }

  // DTO
  public static class ParsedOffer {
    public String insurerName, insurerAddress, agentName, agentRegistryNo, agentAddress;
    public String customerName, customerIdMasked;
    public String offerNo, endorsementNo;
    public LocalDate issueDate, startDate, endDate;
    public Integer dayCount;
    public String plate, brand, type, engineNo, chassisNo;
    public Integer modelYear, seatCount;
    public LocalDate registrationDate;
    public String usageType, step;
    public Map<String, BigDecimal> coverages, premiums;
    public Integer sourceConfidence;
  }
}
