package com.etl.policy.repository;

import com.etl.policy.entity.insurance.OfferVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OfferVehicleRepository extends JpaRepository<OfferVehicle, Long> {
  Optional<OfferVehicle> findByChassisNo(String chassisNo);
  @Query("select v from OfferVehicle v where v.header.id=:headerId")
  Optional<OfferVehicle> findFirstByHeaderId(@Param("headerId") Long headerId);
}