package com.store.reporting.client;

import com.store.reporting.dto.BillingDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "billing-service", url = "${billing.service.url}")
public interface BillingServiceClient {
    @GetMapping("/billings")
    List<BillingDTO> getAllBills();
}
