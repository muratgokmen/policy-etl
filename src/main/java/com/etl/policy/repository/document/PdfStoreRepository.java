package com.etl.policy.repository.document;

import com.etl.policy.entity.document.PdfStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PdfStoreRepository extends JpaRepository<PdfStore, Long> {
  
  @Query("""
    select s from PdfStore s
    where not exists (
      select 1 from ParseRun r where r.pdf = s and r.status = 'SUCCESS'
    )
    order by s.receivedAt
  """)
  List<PdfStore> findUnprocessed();
  
  @Query("select p from PdfStore p where p.contentSha256 = :sha256")
  Optional<PdfStore> findByContentSha256(@Param("sha256") String sha256);
  
  @Query("select p from PdfStore p where p.sourceName = :sourceName order by p.receivedAt desc")
  List<PdfStore> findBySourceName(@Param("sourceName") String sourceName);
}
