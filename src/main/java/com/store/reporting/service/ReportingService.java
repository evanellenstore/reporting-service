package com.store.reporting.service;

import com.store.reporting.client.BillingServiceClient;
import com.store.reporting.client.InventoryServiceClient;
import com.store.reporting.client.PurchaseServiceClient;
import com.store.reporting.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportingService {

    private final PurchaseServiceClient purchaseClient;
    private final InventoryServiceClient inventoryClient;
    private final BillingServiceClient billingClient;

    public ReportResponseDTO generateReport() {

        List<ProductReportDTO> purchases = purchaseClient.getAllPurchases();
        List<InventoryDTO> inventoryList = inventoryClient.getAllInventory();
        List<BillingDTO> bills = billingClient.getAllBills();

        double totalRevenue = bills.stream().mapToDouble(BillingDTO::getTotalAmount).sum();
        double totalTax = bills.stream().mapToDouble(BillingDTO::getTaxAmount).sum();

        ReportResponseDTO report = new ReportResponseDTO();
        report.setProductReports(purchases);
        report.setInventoryStatus(inventoryList);
        report.setTotalRevenue(totalRevenue);
        report.setTotalTax(totalTax);

        return report;
    }
}
