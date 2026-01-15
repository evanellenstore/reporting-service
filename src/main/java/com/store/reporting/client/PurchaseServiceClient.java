package com.store.reporting.client;

import com.store.reporting.dto.ProductReportDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "purchase-service", url = "${purchase.service.url}")
public interface PurchaseServiceClient {
    @GetMapping("/purchases")
    List<ProductReportDTO> getAllPurchases();
}
