package com.etl.policy.batch.processor;

import com.etl.policy.entity.document.ParseRun;
import com.etl.policy.entity.document.PdfStore;
import com.etl.policy.entity.document.PdfText;
import com.etl.policy.parser.TrafficKaskoParser;
import com.etl.policy.repository.document.ParseRunRepository;
import com.etl.policy.repository.document.PdfTextRepository;
import com.etl.policy.service.document.PdfTextService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class PdfProcessor implements ItemProcessor<PdfStore, TrafficKaskoParser.ParsedOffer> {

  @Autowired
  PdfTextRepository textRepo;
  @Autowired
  PdfTextService pdfTextService;
  @Autowired TrafficKaskoParser parser;
  @Autowired
  ParseRunRepository runRepo;

  @Override
  public TrafficKaskoParser.ParsedOffer process(PdfStore pdf) {
    ParseRun run = new ParseRun();
    run.setPdf(pdf);
    run.setParserName("TrafficKaskoParser v1");
    run.setStatus("RUNNING");
    run.setStartedAt(OffsetDateTime.now());
    runRepo.save(run);

    try {
      // PDF→text (cachele)
      PdfText txt = textRepo.findByPdfId(pdf.getId()).orElseGet(() -> {
        PdfText t = new PdfText();
        t.setPdf(pdf);
        t.setText(pdfTextService.extractText(pdf.getContent()));
        t.setOcrApplied(false);
        return textRepo.save(t);
      });

      TrafficKaskoParser.ParsedOffer po = parser.parse(txt.getText());

      run.setStatus("SUCCESS");
      run.setFinishedAt(OffsetDateTime.now());
      runRepo.save(run);
      return po;

    } catch (Exception e) {
      run.setStatus("FAILED");
      run.setErrorMessage(Optional.ofNullable(e.getMessage()).orElse(e.toString()));
      run.setFinishedAt(OffsetDateTime.now());
      runRepo.save(run);
      throw e;
    }
  }
}
