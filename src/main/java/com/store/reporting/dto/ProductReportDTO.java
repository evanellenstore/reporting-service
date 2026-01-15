package com.store.reporting.dto;

import lombok.Data;

@Data
public class ProductReportDTO {
    private Long productId;
    private Integer totalPurchased;
    private Double totalRevenue;
}
