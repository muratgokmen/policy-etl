package com.etl.policy.batch.writer;

import com.etl.policy.dto.insurance.TrafficInsuranceOfferDto;
import com.etl.policy.entity.insurance.TrafficInsuranceOffer;
import com.etl.policy.mapper.insurance.TrafficOfferAggregateMapper;
import com.etl.policy.repository.insurance.OfferHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PdfWriter implements ItemWriter<TrafficInsuranceOfferDto> {

  private final OfferHeaderRepository headerRepo;
  private final TrafficOfferAggregateMapper trafficOfferAggregateMapper;

  @Transactional
  @Override
  public void write(Chunk<? extends TrafficInsuranceOfferDto> chunk)  {
    for (var dto : chunk) {
      TrafficInsuranceOffer offer = trafficOfferAggregateMapper.toEntity(dto);

      var existing = headerRepo.findByOfferNoAndEndorsementNo(
        offer.getOfferNo(), 
        offer.getEndorsementNo()
      );
      
      if (existing.isPresent()) {
        offer.setId(existing.get().getId());
        offer.setPdf(existing.get().getPdf());
      }

      headerRepo.save(offer);
    }
  }
}
