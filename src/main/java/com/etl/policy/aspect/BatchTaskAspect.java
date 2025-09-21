package com.etl.policy.aspect;

import com.etl.policy.annotation.BatchTask;
import com.etl.policy.batch.context.BatchContext;
import com.etl.policy.dto.batch.event.BatchEventDetailDto;
import com.etl.policy.dto.batch.event.BatchEventDto;
import com.etl.policy.enums.BatchStatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BatchTaskAspect {

    @Before("@annotation(batchTask) && args(orderId,..)")
    public void beforeBatchTask(BatchTask batchTask, String orderId) {
        BatchEventDto batch = BatchContext.getCurrentBatchEvent();
        batch.getBatchEventDetailList().add(BatchEventDetailDto.builder()
                .batchId(batch.getId())
                .batchReference(orderId)
                .status(BatchStatusEnum.RUNNING)
                .createDate(LocalDateTime.now())
                .build());
        BatchContext.setCurrentBatchEvent(batch);
    }

    @AfterReturning(value = "@annotation(batchTask) && args(orderId,..)", returning = "result")
    public void afterReturningBatchTask(BatchTask batchTask, String orderId, Object result) {
        BatchEventDto batch = BatchContext.getCurrentBatchEvent();
        BatchEventDetailDto detail = batch.getBatchEventDetailList().stream()
                .filter(d -> d.getBatchReference().equals(orderId))
                .findFirst().orElse(null);

        if (detail != null) {
            detail.setStatus(BatchStatusEnum.COMPLETED);
            detail.setUpdateDate(LocalDateTime.now());

            // Payload JSON ekle
            if (result != null) {
                try {
                    String json = new ObjectMapper().writeValueAsString(result);
                    detail.setPayloadJson(json);
                } catch (Exception ignored) {}
            }
        }
    }

    @AfterThrowing(value = "@annotation(batchTask) && args(orderId,..)", throwing = "ex")
    public void afterThrowingBatchTask(BatchTask batchTask, String orderId, Throwable ex) {
        BatchEventDto batch = BatchContext.getCurrentBatchEvent();
        BatchEventDetailDto detail = batch.getBatchEventDetailList().stream()
                .filter(d -> d.getBatchReference().equals(orderId))
                .findFirst().orElse(null);

        if (detail != null) {
            detail.setStatus(BatchStatusEnum.FAILED);
            detail.setUpdateDate(LocalDateTime.now());
            detail.setErrorMessage(ex.getMessage());
        }
    }
}