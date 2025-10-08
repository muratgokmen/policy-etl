package com.etl.policy.enums;

public enum PremiumKey {
  TRAFFIC_PREMIUM("Trafik Primi"),
  SGK_PREMIUM("SGK Primi");

  public final String label;

  PremiumKey(String label) {
    this.label = label;
  }

}