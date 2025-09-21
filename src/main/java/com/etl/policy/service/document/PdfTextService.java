package com.etl.policy.service.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;

@Service
public class PdfTextService {
  public String extractText(byte[] pdfBytes) {
    try (PDDocument doc = PDDocument.load(pdfBytes)) {
      PDFTextStripper stripper = new PDFTextStripper();
      stripper.setSortByPosition(true);
      String text = stripper.getText(doc);
      return TextCleaner.clean(text);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

