package com.etl.policy.controller.document;

import com.etl.policy.dto.document.PdfUploadResponse;
import com.etl.policy.entity.document.PdfStore;
import com.etl.policy.service.document.PdfStoreService;
import com.etl.policy.service.document.PdfTextReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UploadController {

    private final PdfTextReader pdfTextReader;
    private final PdfStoreService pdfStoreService;

    // Sadece PDF metnini çıkarıp geri döner (parsing yok)
    @PostMapping(value = "/extract-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> extractText(@RequestPart("file") MultipartFile file) {
        try {
            String text = pdfTextReader.readAllText(file);
            return ResponseEntity.ok(text);
        } catch (Exception e) {
            log.error("Error extracting text from PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("PDF metin çıkarma hatası: " + e.getMessage());
        }
    }

    // PDF dosyasını veritabanına yükler
    @PostMapping("/upload-pdf")
    public ResponseEntity<PdfUploadResponse> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "sourceName", required = false) String sourceName) {
        
        log.info("PDF upload request - Filename: {}, Size: {} bytes, SourceName: {}", file.getOriginalFilename(), file.getSize(), sourceName);
        
        PdfUploadResponse response = pdfStoreService.uploadPdf(file, sourceName);
        
        if (response.getId() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Tüm yüklenmiş PDF'leri listeler
    @GetMapping("/pdfs")
    public ResponseEntity<List<PdfStore>> getAllPdfs() {
        try {
            List<PdfStore> pdfs = pdfStoreService.getAllPdfs();
            return ResponseEntity.ok(pdfs);
        } catch (Exception e) {
            log.error("Error retrieving PDFs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // İşlenmemiş PDF'leri listeler
    @GetMapping("/pdfs/unprocessed")
    public ResponseEntity<List<PdfStore>> getUnprocessedPdfs() {
        try {
            List<PdfStore> unprocessedPdfs = pdfStoreService.findUnprocessedPdfs();
            return ResponseEntity.ok(unprocessedPdfs);
        } catch (Exception e) {
            log.error("Error retrieving unprocessed PDFs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Belirli bir PDF'i ID ile getirir
    @GetMapping("/pdfs/{id}")
    public ResponseEntity<PdfStore> getPdfById(@PathVariable Long id) {
        try {
            Optional<PdfStore> pdf = pdfStoreService.findById(id);
            return pdf.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error retrieving PDF with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PDF dosyasını indirir
    @GetMapping("/pdfs/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadPdf(@PathVariable Long id) {
        try {
            Optional<PdfStore> pdfOpt = pdfStoreService.findById(id);
            
            if (pdfOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            PdfStore pdf = pdfOpt.get();
            ByteArrayResource resource = new ByteArrayResource(pdf.getContent());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + pdf.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdf.getContent().length)
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Error downloading PDF with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PDF'i siler
    @DeleteMapping("/pdfs/{id}")
    public ResponseEntity<String> deletePdf(@PathVariable Long id) {
        try {
            boolean deleted = pdfStoreService.deletePdf(id);
            if (deleted) {
                return ResponseEntity.ok("PDF başarıyla silindi");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting PDF with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("PDF silinirken hata oluştu: " + e.getMessage());
        }
    }
}
