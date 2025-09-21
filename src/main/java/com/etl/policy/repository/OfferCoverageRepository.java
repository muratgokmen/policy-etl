package com.etl.policy.repository;

import com.etl.policy.entity.insurance.OfferCoverage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfferCoverageRepository extends JpaRepository<OfferCoverage, Long> {
  Optional<OfferCoverage> findByHeaderIdAndCoverageKey(Long headerId, String key);
}