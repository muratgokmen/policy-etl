package com.etl.policy.batch.reader;

import com.etl.policy.entity.document.PdfStore;
import com.etl.policy.repository.document.PdfStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PdfReader implements ItemReader<PdfStore> {
  
  private final PdfStoreRepository repo;
  private Iterator<PdfStore> iterator;

  @Override
  public PdfStore read() {
    if (iterator == null) {
      List<PdfStore> unprocessedPdfs = repo.findUnprocessed();
      log.info("Found {} unprocessed PDFs for batch processing", unprocessedPdfs.size());
      iterator = unprocessedPdfs.iterator();
    }
    
    if (iterator.hasNext()) {
      PdfStore pdf = iterator.next();
      log.debug("Reading PDF for processing: ID={}, filename={}", pdf.getId(), pdf.getFilename());
      return pdf;
    }
    
    log.info("No more PDFs to process");
    return null;
  }
}
