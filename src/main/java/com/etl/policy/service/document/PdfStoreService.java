package com.etl.policy.service.document;

import com.etl.policy.dto.document.PdfUploadResponse;
import com.etl.policy.entity.document.PdfStore;
import com.etl.policy.repository.document.PdfStoreRepository;
import com.etl.policy.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfStoreService {

    private final PdfStoreRepository storeRepository;

    @Transactional
    public PdfUploadResponse uploadPdf(MultipartFile file, String sourceName) {
        try {
            FileUtil.validatePdfFile(file);

            byte[] content = file.getBytes();
            String sha256 = FileUtil.calculateSHA256(content);

            Optional<PdfStore> existingPdf = findByContentSha256(sha256);
            if (existingPdf.isPresent()) {
                PdfStore existing = existingPdf.get();
                log.warn("Duplicate PDF uploaded. Existing ID: {}, SHA256: {}", existing.getId(), sha256);
                return PdfUploadResponse.success(
                    existing.getId(),
                    existing.getFilename(),
                    existing.getSourceName(),
                    existing.getContentSha256(),
                    (long) existing.getContent().length,
                    existing.getReceivedAt()
                );
            }

            PdfStore pdfStore = new PdfStore();
            pdfStore.setFilename(FileUtil.getCleanFilename(file.getOriginalFilename()));
            pdfStore.setSourceName(sourceName != null ? sourceName : "API_UPLOAD");
            pdfStore.setContent(content);
            pdfStore.setContentSha256(sha256);
            pdfStore.setReceivedAt(OffsetDateTime.now());
            

            PdfStore savedPdf = storeRepository.save(pdfStore);
            
            log.info("PDF successfully uploaded. ID: {}, Filename: {}, Size: {} bytes", 
                    savedPdf.getId(), savedPdf.getFilename(), content.length);
            
            return PdfUploadResponse.success(
                savedPdf.getId(),
                savedPdf.getFilename(),
                savedPdf.getSourceName(),
                savedPdf.getContentSha256(),
                (long) content.length,
                savedPdf.getReceivedAt()
            );
            
        } catch (IOException e) {
            log.error("Error reading file content", e);
            return PdfUploadResponse.error("Dosya okunurken hata oluştu: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid file upload attempt: {}", e.getMessage());
            return PdfUploadResponse.error(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation", e);
            return PdfUploadResponse.error("Veritabanı hatası: Dosya zaten mevcut olabilir");
        } catch (Exception e) {
            log.error("Unexpected error during PDF upload", e);
            return PdfUploadResponse.error("Beklenmedik hata: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<PdfStore> getAllPdfs() {
        return storeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<PdfStore> findById(Long id) {
        return storeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<PdfStore> findUnprocessedPdfs() {
        return storeRepository.findUnprocessed();
    }

    @Transactional(readOnly = true)
    public Optional<PdfStore> findByContentSha256(String sha256) {
        return storeRepository.findByContentSha256(sha256);
    }
    
    @Transactional(readOnly = true)
    public List<PdfStore> findBySourceName(String sourceName) {
        return storeRepository.findBySourceName(sourceName);
    }

    @Transactional
    public boolean deletePdf(Long id) {
        try {
            if (storeRepository.existsById(id)) {
                storeRepository.deleteById(id);
                log.info("PDF deleted successfully. ID: {}", id);
                return true;
            } else {
                log.warn("PDF not found for deletion. ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting PDF with ID: {}", id, e);
            return false;
        }
    }
}
