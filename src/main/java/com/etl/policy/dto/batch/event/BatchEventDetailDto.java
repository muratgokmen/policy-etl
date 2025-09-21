package com.etl.policy.dto.batch.event;

import com.etl.policy.enums.BatchStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchEventDetailDto {
    private Long id;
    private String batchReference;          // Örn: orderId
    private BatchStatusEnum status;
    private String errorMessage;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Long batchId;

    // Task’ın işlediği veri burada JSON olarak tutulacak
    private String payloadJson;
}