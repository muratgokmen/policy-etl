package com.etl.policy.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
public class PdfTextReader {

    public String readAllText(MultipartFile file) {
        try (InputStream in = file.getInputStream(); PDDocument doc = PDDocument.load(in)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String raw = stripper.getText(doc);
            return normalize(raw);
        } catch (Exception e) {
            throw new RuntimeException("PDF okunamadı: " + e.getMessage(), e);
        }
    }

    private String normalize(String s) {
        String t = s.replace("\r\n", "\n").replace("\r", "\n");
        t = t.replaceAll("[\u00A0\t]+", " ");
        t = t.replaceAll(" *\n *", "\n");
        t = t.replaceAll(" {2,}", " ");
        return t.trim();
    }
}
