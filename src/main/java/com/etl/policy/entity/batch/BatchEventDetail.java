package com.etl.policy.entity.batch;

import com.etl.policy.enums.BatchStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_event_detail")
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

    @Column(name="batch_id", insertable=false, updatable=false)
    private Long batchId; // opsiyonel (read-only)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private BatchEvent batchEvent;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payloadJson;
}