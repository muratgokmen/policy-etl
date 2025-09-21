package com.etl.policy.batch.writer;

import com.etl.policy.entity.insurance.OfferCoverage;
import com.etl.policy.entity.insurance.OfferHeader;
import com.etl.policy.entity.insurance.OfferPremium;
import com.etl.policy.entity.insurance.OfferVehicle;
import com.etl.policy.parser.TrafficKaskoParser;
import com.etl.policy.repository.insurance.OfferCoverageRepository;
import com.etl.policy.repository.insurance.OfferHeaderRepository;
import com.etl.policy.repository.insurance.OfferPremiumRepository;
import com.etl.policy.repository.insurance.OfferVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PdfWriter implements ItemWriter<TrafficKaskoParser.ParsedOffer> {

  private final OfferHeaderRepository headerRepo;
  private final OfferVehicleRepository vehicleRepo;
  private final OfferCoverageRepository covRepo;
  private final OfferPremiumRepository premRepo;

  @Transactional
  @Override
  public void write(Chunk<? extends TrafficKaskoParser.ParsedOffer> chunk) throws Exception {
    for (var po : chunk) {
      // Header UPSERT (offer_no + endorsement_no unique)
      OfferHeader header = headerRepo.findByOfferNoAndEndorsementNo(po.offerNo, po.endorsementNo)
              .orElseGet(OfferHeader::new);

      header.setInsurerName(po.insurerName);
      header.setInsurerAddress(po.insurerAddress);
      header.setAgentName(po.agentName);
      header.setAgentRegistryNo(po.agentRegistryNo);
      header.setAgentAddress(po.agentAddress);
      header.setCustomerName(po.customerName);
      header.setCustomerIdMasked(po.customerIdMasked);
      header.setOfferNo(po.offerNo);
      header.setEndorsementNo(po.endorsementNo);
      header.setIssueDate(po.issueDate);
      header.setStartDate(po.startDate);
      header.setEndDate(po.endDate);
      header.setDayCount(po.dayCount);
      header.setSourceConfidence(po.sourceConfidence.shortValue());
      header = headerRepo.save(header);

      // Vehicle UPSERT (chassis_no unique)
      OfferVehicle vehicle = (po.chassisNo == null)
              ? vehicleRepo.findFirstByHeaderId(header.getId()).orElseGet(OfferVehicle::new)
              : vehicleRepo.findByChassisNo(po.chassisNo).orElseGet(OfferVehicle::new);

      vehicle.setHeader(header);
      vehicle.setPlate(po.plate);
      vehicle.setBrand(po.brand);
      vehicle.setType(po.type);
      vehicle.setEngineNo(po.engineNo);
      vehicle.setChassisNo(po.chassisNo);
      vehicle.setModelYear(po.modelYear);
      vehicle.setRegistrationDate(po.registrationDate);
      vehicle.setUsageType(po.usageType);
      vehicle.setSeatCount(po.seatCount);
      vehicle.setStep(po.step);
      vehicle = vehicleRepo.save(vehicle);

      // KV tabloları (önce var olan key’leri çek, sonra upsert)
      for (var e : po.coverages.entrySet()) {
        OfferCoverage c = covRepo.findByHeaderIdAndCoverageKey(header.getId(), e.getKey())
                .orElseGet(OfferCoverage::new);
        c.setHeader(header);
        c.setCoverageKey(e.getKey());
        c.setCoverageValue(e.getValue());
        covRepo.save(c);
      }
      for (var e : po.premiums.entrySet()) {
        OfferPremium p = premRepo.findByHeaderIdAndPremiumKey(header.getId(), e.getKey())
                .orElseGet(OfferPremium::new);
        p.setHeader(header);
        p.setPremiumKey(e.getKey());
        p.setPremiumValue(e.getValue());
        premRepo.save(p);
      }
    }
  }
}
