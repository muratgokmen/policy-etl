package com.etl.policy.dto.document;

import lombok.Data;

@Data
public class PdfUploadRequest {
    private String sourceName;
    private String description;
}
