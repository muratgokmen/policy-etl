package com.etl.policy.repository.insurance;

import com.etl.policy.entity.insurance.OfferPremium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OfferPremiumRepository extends JpaRepository<OfferPremium, Long> {
  Optional<OfferPremium> findByHeaderIdAndPremiumKey(Long headerId, String key);
}