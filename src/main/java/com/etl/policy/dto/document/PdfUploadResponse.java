package com.etl.policy.dto.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdfUploadResponse {
    private Long id;
    private String filename;
    private String sourceName;
    private String contentSha256;
    private Long fileSizeBytes;
    private OffsetDateTime uploadedAt;
    private String message;
    
    public static PdfUploadResponse success(Long id, String filename, String sourceName, 
                                          String sha256, Long fileSize, OffsetDateTime uploadedAt) {
        return new PdfUploadResponse(id, filename, sourceName, sha256, fileSize, uploadedAt, 
                                   "PDF başarıyla yüklendi");
    }
    
    public static PdfUploadResponse error(String message) {
        PdfUploadResponse response = new PdfUploadResponse();
        response.setMessage(message);
        return response;
    }
}
