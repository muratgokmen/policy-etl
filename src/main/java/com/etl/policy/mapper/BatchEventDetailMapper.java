package com.etl.policy.mapper;

import com.etl.policy.dto.batch.event.BatchEventDetailDto;
import com.etl.policy.entity.BatchEventDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BatchEventDetailMapper {
    @Mapping(target = "batchEvent", ignore = true) // parent’ı serviste set edeceğiz
    BatchEventDetail toEntity(BatchEventDetailDto dto);

    @Mapping(target = "batchId", source = "batchEvent.id")
    BatchEventDetailDto toDto(BatchEventDetail entity);

    List<BatchEventDetailDto> toDtoList(List<BatchEventDetail> entities);
}