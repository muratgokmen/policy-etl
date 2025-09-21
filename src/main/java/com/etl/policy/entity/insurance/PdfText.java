package com.etl.policy.entity.insurance;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "pdf_text")
public class PdfText {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_id", unique = true)
    private PdfStore pdf;

    @Lob
    private String text;

    private boolean ocrApplied;

    @Column(columnDefinition = "timestamptz")
    private OffsetDateTime extractedAt = OffsetDateTime.now();
}