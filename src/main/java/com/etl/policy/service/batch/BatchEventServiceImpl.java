package com.etl.policy.service.batch;

import com.etl.policy.dto.batch.event.BatchEventDetailDto;
import com.etl.policy.dto.batch.event.BatchEventDto;
import com.etl.policy.entity.batch.BatchEvent;
import com.etl.policy.entity.batch.BatchEventDetail;
import com.etl.policy.mapper.batch.BatchEventDetailMapper;
import com.etl.policy.mapper.batch.BatchEventMapper;
import com.etl.policy.repository.batch.BatchEventDetailRepository;
import com.etl.policy.repository.batch.BatchEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchEventServiceImpl implements BatchEventService {

    private final BatchEventRepository batchEventRepository;
    private final BatchEventDetailRepository batchEventDetailRepository;
    private final BatchEventMapper batchEventMapper;
    private final BatchEventDetailMapper batchEventDetailMapper;

    @Transactional
    @Override
    public BatchEventDto save(BatchEventDto dto) {
        BatchEvent entity = batchEventMapper.toEntity(dto);
        entity.setCreateDate(LocalDateTime.now());

        // child back-reference ayarla
        if (entity.getDetails() != null) {
            for (BatchEventDetail d : entity.getDetails()) {
                d.setBatchEvent(entity);
                if (d.getCreateDate() == null) d.setCreateDate(LocalDateTime.now());
            }
        }

        BatchEvent saved = batchEventRepository.save(entity);
        return batchEventMapper.toDto(saved);
    }

    @Transactional
    @Override
    public BatchEventDto update(Long id, BatchEventDto dto) {
        BatchEvent existing = batchEventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("BatchEvent not found: " + id));

        // alanları merge et
        existing.setBatchName(dto.getBatchName());
        existing.setGuid(dto.getGuid());
        existing.setStatus(dto.getStatus());
        existing.setErrorMessage(dto.getErrorMessage());
        existing.setUpdateDate(LocalDateTime.now());

        // child’ları senkronize et (basit strateji: listeyi clear → yeniden ekle)
        if (dto.getBatchEventDetailList() != null) {
            existing.getDetails().clear();
            for (BatchEventDetailDto dDto : dto.getBatchEventDetailList()) {
                BatchEventDetail d = batchEventDetailMapper.toEntity(dDto);
                d.setBatchEvent(existing);
                if (d.getCreateDate() == null) d.setCreateDate(LocalDateTime.now());
                d.setUpdateDate(LocalDateTime.now());
                existing.getDetails().add(d);
            }
        }

        BatchEvent merged = batchEventRepository.save(existing);
        return batchEventMapper.toDto(merged);
    }

    @Transactional(readOnly = true)
    @Override
    public BatchEventDto findById(Long id) {
        return batchEventRepository.findById(id)
                .map(batchEventMapper::toDto)
                .orElse(null);
    }

    @Transactional
    @Override
    public void addDetail(Long batchId, BatchEventDetailDto detailDto) {
        BatchEvent parent = batchEventRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("BatchEvent not found: " + batchId));

        BatchEventDetail detail = batchEventDetailMapper.toEntity(detailDto);
        detail.setBatchEvent(parent);
        if (detail.getCreateDate() == null) detail.setCreateDate(LocalDateTime.now());
        detail.setUpdateDate(LocalDateTime.now());

        parent.getDetails().add(detail);
        batchEventRepository.save(parent); // cascade sayesinde detail de kaydolur
    }
}