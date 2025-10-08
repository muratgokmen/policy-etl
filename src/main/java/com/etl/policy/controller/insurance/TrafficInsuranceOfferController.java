package com.etl.policy.controller.insurance;

import com.etl.policy.dto.insurance.TrafficInsuranceOfferDto;
import com.etl.policy.service.insurance.TrafficOfferService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Trafik sigortası teklifi REST API controller.
 * Unified DTO (TrafficInsuranceOfferDto) kullanır - nested yapı.
 */
@RestController
@RequestMapping("/api")
public class TrafficInsuranceOfferController {

    private final TrafficOfferService service;

    public TrafficInsuranceOfferController(TrafficOfferService service) {
        this.service = service;
    }

    @PostMapping(value = "/parse-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrafficInsuranceOfferDto> parsePdf(@RequestPart("file") @NotNull MultipartFile file) {
        return ResponseEntity.ok(service.parsePdf(file));
    }
}
