package com.etl.policy.dto.batch.event;

import com.etl.policy.enumeration.BatchStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchEventDto {
    private Long id;
    private String guid;
    private String batchName;
    private BatchStatusEnum status;
    private String errorMessage;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @Builder.Default
    private List<BatchEventDetailDto> batchEventDetailList = new ArrayList<>();
}