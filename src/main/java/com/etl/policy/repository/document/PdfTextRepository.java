package com.etl.policy.repository.document;

import com.etl.policy.entity.document.PdfText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PdfTextRepository extends JpaRepository<PdfText, Long> {
  @Query("select t from PdfText t where t.pdf.id=:pdfId")
  Optional<PdfText> findByPdfId(@Param("pdfId") Long pdfId);


  List<PdfText> findByPdfIdIn(List<Long> pdfIds);
}