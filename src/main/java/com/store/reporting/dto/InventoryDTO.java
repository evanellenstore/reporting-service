package com.store.reporting.dto;

import lombok.Data;

@Data
public class InventoryDTO {
    private Long productId;
    private Integer availableQty;
    private Integer reservedQty;
}
