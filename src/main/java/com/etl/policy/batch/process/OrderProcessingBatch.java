package com.etl.policy.batch.process;

import com.etl.policy.batch.annotation.BatchProcess;
import com.etl.policy.batch.annotation.BatchTask;
import com.etl.policy.dto.batch.payload.OrderPayload;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderProcessingBatch {

    @BatchProcess(name = "ORDER_BATCH", info = "Sipariş işleme batch jobu")
    public void processOrders() {
        List<String> orderIds = List.of("ORD-1001", "ORD-1002", "ORD-1003");
        for (String orderId : orderIds) {
            processSingleOrder(orderId);
        }
    }

    @BatchTask
    public OrderPayload processSingleOrder(String orderId) {
        // Fake business logic
        if ("ORD-1002".equals(orderId)) {
            throw new RuntimeException("Payment failed!");
        }

        return OrderPayload.builder()
                .orderId(orderId)
                .customerName("Customer " + orderId)
                .totalAmount(Math.random() * 1000)
                .status("SUCCESS")
                .build();
    }
}