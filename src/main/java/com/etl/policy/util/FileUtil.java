package com.etl.policy.util;

import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public class FileUtil {
    
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("application/pdf");
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    public static boolean isPdfFile(MultipartFile file) {
        return ALLOWED_CONTENT_TYPES.contains(file.getContentType());
    }
    
    public static boolean isValidFileSize(MultipartFile file) {
        return file.getSize() <= MAX_FILE_SIZE;
    }
    
    public static String calculateSHA256(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content);
            StringBuilder hexString = new StringBuilder();
            
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algoritması bulunamadı", e);
        }
    }
    
    public static String getCleanFilename(String originalFilename) {
        if (originalFilename == null) {
            return "unknown.pdf";
        }

        return originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    public static void validatePdfFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş olamaz");
        }
        
        if (!isPdfFile(file)) {
            throw new IllegalArgumentException("Sadece PDF dosyaları kabul edilir");
        }
        
        if (!isValidFileSize(file)) {
            throw new IllegalArgumentException("Dosya boyutu 10MB'dan büyük olamaz");
        }
    }

}
