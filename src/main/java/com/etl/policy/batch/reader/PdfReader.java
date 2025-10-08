package com.etl.policy.batch.reader;

import com.etl.policy.entity.document.PdfStore;
import com.etl.policy.entity.document.PdfText;
import com.etl.policy.repository.document.PdfStoreRepository;
import com.etl.policy.repository.document.PdfTextRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PdfReader implements ItemReader<PdfText> {
  
  private final PdfStoreRepository repo;
  private final PdfTextRepository pdfTextRepository;
  private Iterator<PdfText> iterator;

  @Override
  public PdfText read() {
    if (iterator == null) {
      List<PdfStore> unprocessedPdfs = repo.findUnprocessed();
      List<Long> pdfIds = unprocessedPdfs.stream().map(PdfStore::getId).toList();
      log.info("Found {} unprocessed PDFs for batch processing", unprocessedPdfs.size());
      List<PdfText> pdfTexts = pdfTextRepository.findByPdfIdIn(pdfIds);
      iterator = pdfTexts.iterator();
    }
    
    if (iterator.hasNext()) {
      PdfText pdfTexts = iterator.next();
      log.debug("Reading PDF ");
      return pdfTexts;
    }
    
    log.info("No more PDFs to process");
    return null;
  }
}
