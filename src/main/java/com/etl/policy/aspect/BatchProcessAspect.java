package com.etl.policy.aspect;

import com.etl.policy.annotation.BatchProcess;
import com.etl.policy.batch.context.BatchContext;
import com.etl.policy.dto.batch.event.BatchEventDetailDto;
import com.etl.policy.dto.batch.event.BatchEventDto;
import com.etl.policy.dto.batch.payload.OrderPayload;
import com.etl.policy.enums.BatchStatusEnum;
import com.etl.policy.notification.email.EmailService;
import com.etl.policy.service.batch.BatchEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BatchProcessAspect {

    private final BatchEventService batchEventService;
    private final EmailService emailService;

    @Before("@annotation(batchProcess)")
    public void beforeBatchProcess(BatchProcess batchProcess) {
        BatchEventDto batchEventDto = BatchEventDto.builder()
                .guid(UUID.randomUUID().toString())
                .batchName(batchProcess.name())
                .status(BatchStatusEnum.RUNNING)
                .batchEventDetailList(new ArrayList<>())
                .build();
        batchEventDto = batchEventService.save(batchEventDto);
        BatchContext.setCurrentBatchEvent(batchEventDto);
    }

    @AfterReturning("@annotation(batchProcess)")
    public void afterReturningBatchProcess(BatchProcess batchProcess) {
        BatchEventDto batch = BatchContext.getCurrentBatchEvent();
        List<BatchEventDetailDto> failedTasks = batch.getBatchEventDetailList().stream()
                .filter(d -> d.getStatus() == BatchStatusEnum.FAILED)
                .toList();

        if (!failedTasks.isEmpty()) {
            batch.setStatus(BatchStatusEnum.FAILED);
        } else {
            batch.setStatus(BatchStatusEnum.COMPLETED);
        }

        batchEventService.update(batch.getId(), batch);

        // Commit sonrası mail gönder
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                String subject = "[Batch] " + batch.getBatchName() + " → " + batch.getStatus();
                String html = buildHtml(batch);
                emailService.send(subject, html);
            }
        });

        BatchContext.clear();
    }

    private String buildHtml(BatchEventDto batch) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h3>Batch Result: ").append(batch.getBatchName()).append("</h3>");
        sb.append("<p>Status: ").append(batch.getStatus()).append("</p>");
        sb.append("<table border='1'><tr><th>Order</th><th>Status</th><th>Total</th></tr>");

        ObjectMapper om = new ObjectMapper();
        for (BatchEventDetailDto d : batch.getBatchEventDetailList()) {
            OrderPayload payload = null;
            try {
                if (d.getPayloadJson() != null) {
                    payload = om.readValue(d.getPayloadJson(), OrderPayload.class);
                }
            } catch (Exception ignored) {}

            sb.append("<tr>")
              .append("<td>").append(payload != null ? payload.getOrderId() : d.getBatchReference()).append("</td>")
              .append("<td>").append(d.getStatus()).append("</td>")
              .append("<td>").append(payload != null ? payload.getTotalAmount() : "-").append("</td>")
              .append("</tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }
}