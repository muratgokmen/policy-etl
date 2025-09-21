package com.etl.policy.service;

import com.etl.policy.document.PdfTextReader;
import com.batch.example.dto.insurance.TrafficOfferDto;
import com.etl.policy.parse.TrafficOfferParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TrafficOfferService {

    private final PdfTextReader pdfTextReader;
    private final TrafficOfferParser parser;

    public TrafficOfferService(PdfTextReader pdfTextReader, TrafficOfferParser parser) {
        this.pdfTextReader = pdfTextReader;
        this.parser = parser;
    }

    public TrafficOfferDto parsePdf(MultipartFile file) {
        String text = pdfTextReader.readAllText(file);
        return parser.parse(text);
    }
}
