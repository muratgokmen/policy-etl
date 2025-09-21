package com.etl.policy.service.document;

public final class TextCleaner {
  private TextCleaner(){}
  public static String clean(String s) {
    // Normalleştirme: CRLF -> LF, trailing space trim, çoklu boşluk sadeleştirme opsiyonel
    String t = s.replace("\r\n", "\n");
    // bazı PDF’lerde sayfa numarası/alt bilgi temizliği gibi ek kurallar eklenebilir
    return t;
  }
}