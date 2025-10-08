package com.etl.policy.repository.insurance;

import com.etl.policy.entity.insurance.TrafficInsuranceOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfferHeaderRepository extends JpaRepository<TrafficInsuranceOffer, Long> {
  Optional<TrafficInsuranceOffer> findByOfferNoAndEndorsementNo(String offerNo, String endorsementNo);
}