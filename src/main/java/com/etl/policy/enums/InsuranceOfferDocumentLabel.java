package com.etl.policy.enums;

public enum InsuranceOfferDocumentLabel {
  INSURER_NAME("Sigorta Şirketi", "Sigortacı"),
  INSURER_ADDRESS("Sigorta Şirketi Adresi"),
  AGENT_NAME("Acente Ad/Ünvan"),
  AGENT_REG_NO("Acente Levha Kayıt No"),
  AGENT_ADDRESS("Acente Adresi"),
  CUSTOMER_NAME("Sigortalı Adı Soyadı/Ünvanı", "Sigorta Ettiren"),
  CUSTOMER_ID("Kimlik Numarası"),
  OFFER_NO("Teklif No"),
  ENDORSEMENT_NO("Zeyil No"),
  ISSUE_DATE("Tanzim Tarihi"),
  START_DATE("Başlama Tarihi"),
  END_DATE("Bitiş Tarihi"),
  DAY_COUNT("Gün"),

  PLATE("Plaka"),
  BRAND("Marka"),
  TYPE("Tipi"),
  ENGINE_NO("Motor No"),
  CHASSIS_NO("Şasi No"),
  MODEL_YEAR("Model Yılı"),
  REGISTRATION_DATE("Tescil Tarihi"),
  USAGE_TYPE("Kullanım Tarzı"),
  SEAT_COUNT("Koltuk Adedi"),
  STEP("Basamak");

  public final String[] aliases;
  InsuranceOfferDocumentLabel(String... aliases) { this.aliases = aliases; }
}
