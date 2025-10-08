package com.etl.policy.mapper.insurance;

import com.etl.policy.dto.insurance.TrafficInsuranceCoverageDto;
import com.etl.policy.dto.insurance.TrafficInsuranceOfferDto;
import com.etl.policy.dto.insurance.TrafficInsuranceOfferVehicleDto;
import com.etl.policy.dto.insurance.TrafficInsurancePremiumDto;
import com.etl.policy.entity.insurance.TrafficInsuranceOffer;
import com.etl.policy.entity.insurance.TrafficInsuranceOfferCoverage;
import com.etl.policy.entity.insurance.TrafficInsuranceOfferPremium;
import com.etl.policy.entity.insurance.TrafficInsuranceOfferVehicle;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrafficOfferAggregateMapper {

  /**
   * TrafficInsuranceOfferDto'dan entity'ye mapping (nested children dahil).
   * DTO'daki vehicle (tekil) -> entity'deki vehicles (list) mapping için özel metod kullanılır.
   * AfterMapping ile bidirectional ilişkileri kurar.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "pdf", ignore = true)
  @Mapping(source = "vehicle", target = "vehicles", qualifiedByName = "vehicleToList")
  @Mapping(source = "coverages", target = "coverages")
  @Mapping(source = "premiums", target = "premiums")
  TrafficInsuranceOffer toEntity(TrafficInsuranceOfferDto dto);

  /**
   * Vehicle DTO'dan entity'ye mapping
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "header", ignore = true)
  TrafficInsuranceOfferVehicle toEntity(TrafficInsuranceOfferVehicleDto dto);

  /**
   * Coverage DTO'dan entity'ye mapping
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "header", ignore = true)
  TrafficInsuranceOfferCoverage toEntity(TrafficInsuranceCoverageDto dto);

  /**
   * Premium DTO'dan entity'ye mapping
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "header", ignore = true)
  TrafficInsuranceOfferPremium toEntity(TrafficInsurancePremiumDto dto);

  /**
   * List mapping - coverages
   */
  List<TrafficInsuranceOfferCoverage> toCoverageEntities(List<TrafficInsuranceCoverageDto> dtos);

  /**
   * List mapping - premiums
   */
  List<TrafficInsuranceOfferPremium>  toPremiumEntities (List<TrafficInsurancePremiumDto>  dtos);

  /**
   * Vehicle DTO'yu list içinde wrap eder (ParsedOffer mapping için)
   */
  @Named("vehicleToList")
  default List<TrafficInsuranceOfferVehicle> mapVehicleToList(TrafficInsuranceOfferVehicleDto dto) {
    if (dto == null) {
      return new ArrayList<>();
    }
    List<TrafficInsuranceOfferVehicle> list = new ArrayList<>();
    list.add(toEntity(dto));
    return list;
  }

  /**
   * Mapping sonrası bidirectional ilişkileri kurar
   */
  @AfterMapping
  default void establishRelationships(@MappingTarget TrafficInsuranceOffer offer) {
    // Vehicle ilişkilerini kur
    if (offer.getVehicles() != null) {
      offer.getVehicles().forEach(vehicle -> vehicle.setHeader(offer));
    }
    
    // Coverage ilişkilerini kur
    if (offer.getCoverages() != null) {
      offer.getCoverages().forEach(coverage -> coverage.setHeader(offer));
    }
    
    // Premium ilişkilerini kur
    if (offer.getPremiums() != null) {
      offer.getPremiums().forEach(premium -> premium.setHeader(offer));
    }
  }

}
