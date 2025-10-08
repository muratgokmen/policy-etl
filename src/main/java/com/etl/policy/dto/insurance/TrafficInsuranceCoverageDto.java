package com.etl.policy.dto.insurance;

import java.math.BigDecimal;

public record TrafficInsuranceCoverageDto(String coverageKey, BigDecimal coverageValue) {}