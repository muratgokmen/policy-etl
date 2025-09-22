package com.etl.policy.batch.processor;

import com.etl.policy.entity.document.ParseRun;
import com.etl.policy.entity.document.PdfStore;
import com.etl.policy.entity.document.PdfText;
import com.etl.policy.parser.TrafficKaskoParser;
import com.etl.policy.repository.document.ParseRunRepository;
import com.etl.policy.repository.document.PdfTextRepository;
import com.etl.policy.service.document.PdfTextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PdfProcessor implements ItemProcessor<PdfStore, TrafficKaskoParser.ParsedOffer> {

  private final PdfTextRepository textRepo;
  private final PdfTextService pdfTextService;
  private final TrafficKaskoParser parser;
  private final ParseRunRepository runRepo;

  @Override
  public TrafficKaskoParser.ParsedOffer process(PdfStore pdf) {
    log.info("Processing PDF: ID={}, filename={}", pdf.getId(), pdf.getFilename());
    
    ParseRun run = new ParseRun();
    run.setPdf(pdf);
    run.setParserName("TrafficKaskoParser v1");
    run.setStatus("RUNNING");
    run.setStartedAt(OffsetDateTime.now());
    runRepo.save(run);

    try {
      // PDF→text (cache with database)
      PdfText txt = textRepo.findByPdfId(pdf.getId()).orElseGet(() -> {
        log.debug("Extracting text from PDF ID={}", pdf.getId());
        PdfText t = new PdfText();
        t.setPdf(pdf);
        t.setText(pdfTextService.extractText(pdf.getContent()));
        t.setOcrApplied(false);
        return textRepo.save(t);
      });

      log.debug("Parsing extracted text for PDF ID={}", pdf.getId());
      TrafficKaskoParser.ParsedOffer parsedOffer = parser.parse(txt.getText());

      run.setStatus("SUCCESS");
      run.setFinishedAt(OffsetDateTime.now());
      runRepo.save(run);
      
      log.info("Successfully processed PDF ID={}", pdf.getId());
      return parsedOffer;

    } catch (Exception e) {
      log.error("Failed to process PDF ID={}: {}", pdf.getId(), e.getMessage(), e);
      
      run.setStatus("FAILED");
      run.setErrorMessage(Optional.ofNullable(e.getMessage()).orElse(e.toString()));
      run.setFinishedAt(OffsetDateTime.now());
      runRepo.save(run);
      
      throw e;
    }
  }
}
