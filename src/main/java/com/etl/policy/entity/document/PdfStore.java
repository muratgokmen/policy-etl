package com.etl.policy.entity.document;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name="pdf_store", uniqueConstraints=@UniqueConstraint(columnNames="content_sha256"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdfStore {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String sourceName;

  private String filename;

  // PostgreSQL BYTEA - no @Lob needed
  private byte[] content;

  @Column(length=64, nullable=false)
  private String contentSha256;

  @Column(columnDefinition="timestamptz")
  private OffsetDateTime receivedAt = OffsetDateTime.now();
}