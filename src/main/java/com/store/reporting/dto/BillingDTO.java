package com.store.reporting.dto;

import lombok.Data;

@Data
public class BillingDTO {
    private Long purchaseId;
    private Double subTotal;
    private Double taxAmount;
    private Double totalAmount;
}
