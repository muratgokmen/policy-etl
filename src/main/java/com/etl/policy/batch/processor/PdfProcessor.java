package com.etl.policy.batch.processor;

import com.etl.policy.dto.insurance.TrafficInsuranceOfferDto;
import com.etl.policy.entity.document.PdfText;
import com.etl.policy.parser.TrafficInsurancePdfParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PdfProcessor implements ItemProcessor<PdfText, TrafficInsuranceOfferDto> {

  private final TrafficInsurancePdfParser parser;

  @Override
  public TrafficInsuranceOfferDto process(PdfText txt) {
    return parser.parse(txt.getText());
  }

}
