package com.etl.policy.mapper;

import com.etl.policy.dto.batch.event.BatchEventDto;
import com.etl.policy.entity.BatchEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { BatchEventDetailMapper.class })
public interface BatchEventMapper {
    @Mapping(target = "details", source = "batchEventDetailList")
    BatchEvent toEntity(BatchEventDto dto);

    @Mapping(target = "batchEventDetailList", source = "details")
    BatchEventDto toDto(BatchEvent entity);

    List<BatchEventDto> toDtoList(List<BatchEvent> entities);
}