package com.etl.policy.controller;

import com.etl.policy.service.document.PdfTextReader;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final PdfTextReader pdfTextReader;

    public UploadController(PdfTextReader pdfTextReader) {
        this.pdfTextReader = pdfTextReader;
    }

    // Step 0: Sadece PDF metnini çıkarıp geri döner (parsing yok)
    @PostMapping(value = "/extract-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> extractText(@RequestPart("file") @NotNull MultipartFile file) {
        String text = pdfTextReader.readAllText(file);
        return ResponseEntity.ok(text);
    }

}
