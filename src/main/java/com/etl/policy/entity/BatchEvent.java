package com.etl.policy.entity;

import com.etl.policy.enumeration.BatchStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BATCH_EVENT")
@Getter
@Setter
public class BatchEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=64)
    private String guid;

    @Column(nullable=false, length=128)
    private String batchName;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=32)
    private BatchStatusEnum status;

    @Column(length=4000)
    private String errorMessage;

    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "batchEvent",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<BatchEventDetail> details = new ArrayList<>();
}
