package com.etl.policy.dto.batch.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPayload {
    private String orderId;
    private String customerName;
    private Double totalAmount;
    private String status;  // SUCCESS / FAILED
}
