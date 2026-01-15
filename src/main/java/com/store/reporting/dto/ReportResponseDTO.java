package com.store.reporting.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReportResponseDTO {
    private List<ProductReportDTO> productReports;
    private List<InventoryDTO> inventoryStatus;
    private Double totalRevenue;
    private Double totalTax;
}
