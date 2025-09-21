package com.etl.policy.service;

import com.etl.policy.dto.batch.event.BatchEventDetailDto;
import com.etl.policy.dto.batch.event.BatchEventDto;

public interface BatchEventService {
    BatchEventDto save(BatchEventDto dto);
    BatchEventDto update(Long id, BatchEventDto dto);
    BatchEventDto findById(Long id);
    void addDetail(Long batchId, BatchEventDetailDto detailDto); // isteğe bağlı helper
}