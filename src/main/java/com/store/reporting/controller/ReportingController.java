package com.store.reporting.controller;

import com.store.reporting.dto.ReportResponseDTO;
import com.store.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/report")
    public ReportResponseDTO getReport() {
        return reportingService.generateReport();
    }
}
