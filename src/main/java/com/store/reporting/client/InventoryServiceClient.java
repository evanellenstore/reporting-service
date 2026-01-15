package com.store.reporting.client;

import com.store.reporting.dto.InventoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "inventory-service", url = "${inventory.service.url}")
public interface InventoryServiceClient {
    @GetMapping("/inventory")
    List<InventoryDTO> getAllInventory();
}
