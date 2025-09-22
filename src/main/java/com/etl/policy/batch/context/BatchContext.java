package com.etl.policy.batch.context;

import com.etl.policy.dto.batch.event.BatchEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class BatchContext {
    private static final ThreadLocal<BatchEventDto> HOLDER = new ThreadLocal<>();
    private static final ObjectMapper OM = new ObjectMapper();

    public static void setCurrentBatchEvent(BatchEventDto dto) { HOLDER.set(dto); }
    public static BatchEventDto getCurrentBatchEvent() { return HOLDER.get(); }
    public static void clear() { HOLDER.remove(); }

    public static void attachPayload(String referenceId, Object payload) {
        BatchEventDto batch = getCurrentBatchEvent();
        if (batch == null || referenceId == null) return;

        batch.getBatchEventDetailList().stream()
                .filter(d -> referenceId.equals(d.getBatchReference()))
                .findFirst()
                .ifPresent(d -> {
                    try { d.setPayloadJson(OM.writeValueAsString(payload)); }
                    catch (Exception ignored) {}
                });
    }
}