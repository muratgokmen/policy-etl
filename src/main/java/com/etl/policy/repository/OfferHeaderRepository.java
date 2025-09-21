package com.etl.policy.repository;

import com.etl.policy.entity.insurance.OfferHeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfferHeaderRepository extends JpaRepository<OfferHeader, Long> {
  Optional<OfferHeader> findByOfferNoAndEndorsementNo(String offerNo, String endorsementNo);
}