package com.etl.policy.entity;

import com.etl.policy.enumeration.BatchStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "BATCH_EVENT_DETAIL")
@Getter
@Setter
public class BatchEventDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=128)
    private String batchReference;

    @Enumerated(EnumType.STRING)
    @Column(length=32)
    private BatchStatusEnum status;

    @Column(length=4000)
    private String errorMessage;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @Column(name="BATCH_ID", insertable=false, updatable=false)
    private Long batchId; // opsiyonel (read-only)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BATCH_ID", nullable = false)
    private BatchEvent batchEvent;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String payloadJson;
}