package com.etl.policy.repository;

import com.etl.policy.entity.insurance.PdfStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PdfStoreRepository extends JpaRepository<PdfStore, Long> {
  @Query("""
    select s from PdfStore s
    where not exists (
      select 1 from ParseRun r where r.pdf = s and r.status = 'SUCCESS'
    )
    order by s.receivedAt
  """)
  List<PdfStore> findUnprocessed();
}
