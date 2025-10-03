package com.etl.policy.controller;

import com.etl.policy.dto.insurance.TrafficOfferDto;
import com.etl.policy.service.insurance.TrafficOfferService;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class CarInsuranceController {

    private final TrafficOfferService service;

    public CarInsuranceController(TrafficOfferService service) {
        this.service = service;
    }

    @PostMapping(value = "/parse-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrafficOfferDto> parsePdf(@RequestPart("file") @NotNull MultipartFile file) {
        return ResponseEntity.ok(service.parsePdf(file));
    }
}
