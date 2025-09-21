package com.etl.policy.enums;

import lombok.Getter;

@Getter
public enum BatchStatusEnum {
    PENDING("Waiting to start"),
    RUNNING("Currently running"),
    SUCCESS("Completed successfully"),
    FAILED("Execution failed"),
    SKIPPED("Skipped"),
    CANCELLED("Cancelled"),
    COMPLETED("compeleted");

    private final String description;

    BatchStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

