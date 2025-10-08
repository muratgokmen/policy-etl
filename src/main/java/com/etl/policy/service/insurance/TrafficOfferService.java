package com.etl.policy.service.insurance;

import com.etl.policy.dto.insurance.TrafficInsuranceOfferDto;
import com.etl.policy.parser.TrafficInsurancePdfParser;
import com.etl.policy.service.document.PdfTextReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Trafik sigortası teklifi parsing servisi.
 * PDF'den TrafficInsuranceOfferDto (nested yapı) oluşturur.
 */
@Service
public class TrafficOfferService {

    private final PdfTextReader pdfTextReader;
    private final TrafficInsurancePdfParser parser;

    public TrafficOfferService(PdfTextReader pdfTextReader, TrafficInsurancePdfParser parser) {
        this.pdfTextReader = pdfTextReader;
        this.parser = parser;
    }

    public TrafficInsuranceOfferDto parsePdf(MultipartFile file) {
        String text = pdfTextReader.readAllText(file);
        return parser.parse(text);
    }
}
