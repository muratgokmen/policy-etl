package com.etl.policy.repository.document;

import com.etl.policy.entity.document.ParseRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParseRunRepository extends JpaRepository<ParseRun, Long> {}