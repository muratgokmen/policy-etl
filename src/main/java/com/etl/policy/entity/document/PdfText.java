package com.etl.policy.entity.document;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "pdf_text")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdfText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_id", unique = true)
    private PdfStore pdf;

    // PostgreSQL TEXT - no @Lob needed
    @Column(columnDefinition = "TEXT")
    private String text;

    private boolean ocrApplied;

    @Column(columnDefinition = "timestamptz")
    private OffsetDateTime extractedAt = OffsetDateTime.now();
}