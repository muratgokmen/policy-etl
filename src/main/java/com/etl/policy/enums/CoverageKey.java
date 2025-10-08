package com.etl.policy.enums;

public enum CoverageKey {
  VEHICLE_MATERIAL_PER_VEHICLE("Araç Başına Maddi"),
  VEHICLE_MATERIAL_PER_ACCIDENT("Kaza Başına Maddi"),
  INJURY_DEATH_PER_PERSON("Sakatlanma ve Ölüm - Kişi Başı");

  public final String label;

  CoverageKey(String label) {
    this.label = label;
  }

}
