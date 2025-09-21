package com.etl.policy.entity.document;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "parse_run")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParseRun {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_id")
    private PdfStore pdf;
    private String parserName;
    private String status; // PENDING/RUNNING/SUCCESS/FAILED
    @Lob
    private String errorMessage;
    @Column(columnDefinition = "timestamptz")
    private OffsetDateTime startedAt;
    @Column(columnDefinition = "timestamptz")
    private OffsetDateTime finishedAt;
}