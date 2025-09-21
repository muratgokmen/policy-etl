package com.etl.policy.entity.insurance;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name="pdf_store", uniqueConstraints=@UniqueConstraint(columnNames="content_sha256"))
public class PdfStore {
  @Id
  @GeneratedValue
  private Long id;

  private String sourceName;

  private String filename;

  @Lob
  private byte[] content;

  @Column(length=64, nullable=false)
  private String contentSha256;

  @Column(columnDefinition="timestamptz")
  private OffsetDateTime receivedAt = OffsetDateTime.now();

}