package com.etl.policy.dto.insurance;

import java.math.BigDecimal;

public record TrafficInsurancePremiumDto (String premiumKey, BigDecimal premiumValue)  {}