package com.etl.policy.repository.batch;

import com.etl.policy.entity.batch.BatchEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BatchEventRepository extends JpaRepository<BatchEvent, Long> {
    Optional<BatchEvent> findByGuid(String guid);
}