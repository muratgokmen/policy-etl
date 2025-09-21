package com.etl.policy.repository.batch;

import com.etl.policy.entity.batch.BatchEventDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchEventDetailRepository extends JpaRepository<BatchEventDetail, Long> {
    List<BatchEventDetail> findByBatchEvent_Id(Long batchId);
}