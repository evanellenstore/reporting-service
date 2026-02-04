package com.store.reporting.service;

import com.store.reporting.client.BillingServiceClient;
import com.store.reporting.client.InventoryServiceClient;
import com.store.reporting.client.PurchaseServiceClient;
import com.store.reporting.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportingService {

    private final PurchaseServiceClient purchaseClient;
    private final InventoryServiceClient inventoryClient;
    private final BillingServiceClient billingClient;

    // runtime-configurable URLs (optional); if not set or incorrect we'll catch and continue
    @Value("${purchase.service.url:}")
    private String purchaseServiceUrl;

    @Value("${inventory.service.url:}")
    private String inventoryServiceUrl;

    @Value("${billing.service.url:}")
    private String billingServiceUrl;

    private final RestTemplate rest = new RestTemplate();

    public ReportResponseDTO generateReport() {
    // Delegate to the robust implementation so /report does not fail when a downstream
    // service URL or mapping is incorrect. This keeps backwards compatibility while
    // returning partial data where possible.
    return generateReportRobust();
    }

    /**
     * Robust runtime-safe report which uses RestTemplate and configured URLs so the
     * reporting app won't fail if the Feign url/property is wrong or a downstream
     * service is unreachable. Returns partial data where possible.
     */
    public ReportResponseDTO generateReportRobust() {
        ReportResponseDTO report = new ReportResponseDTO();

        List<ProductReportDTO> purchases = Collections.emptyList();
        List<InventoryDTO> inventoryList = Collections.emptyList();
        List<BillingDTO> bills = Collections.emptyList();

        // purchases
        if (purchaseServiceUrl != null && !purchaseServiceUrl.isBlank()) {
            String url = purchaseServiceUrl.endsWith("/") ? purchaseServiceUrl + "purchases" : purchaseServiceUrl + "/purchases";
            try {
                ResponseEntity<ProductReportDTO[]> resp = rest.getForEntity(url, ProductReportDTO[].class);
                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    purchases = Arrays.asList(resp.getBody());
                }
            } catch (RestClientException e) {
                System.err.println("Robust report: failed to fetch purchases from " + url + ": " + e.getMessage());
            }
        } else {
            // as a last resort try Feign client if available
            try { purchases = purchaseClient.getAllPurchases(); } catch (Exception ignored) {}
        }

        // inventory
        if (inventoryServiceUrl != null && !inventoryServiceUrl.isBlank()) {
            String url = inventoryServiceUrl.endsWith("/") ? inventoryServiceUrl + "inventory" : inventoryServiceUrl + "/inventory";
            try {
                ResponseEntity<InventoryDTO[]> resp = rest.getForEntity(url, InventoryDTO[].class);
                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    inventoryList = Arrays.asList(resp.getBody());
                }
            } catch (RestClientException e) {
                System.err.println("Robust report: failed to fetch inventory from " + url + ": " + e.getMessage());
            }
        } else {
            try { inventoryList = inventoryClient.getAllInventory(); } catch (Exception ignored) {}
        }

        // billing - prefer billing summary endpoint if available
        if (billingServiceUrl != null && !billingServiceUrl.isBlank()) {
            String urlReport = billingServiceUrl.endsWith("/") ? billingServiceUrl + "billings/report" : billingServiceUrl + "/billings/report";
            String urlFallback = billingServiceUrl.endsWith("/") ? billingServiceUrl + "billings" : billingServiceUrl + "/billings";
            try {
                ResponseEntity<BillingDTO[]> resp = rest.getForEntity(urlReport, BillingDTO[].class);
                if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                    bills = Arrays.asList(resp.getBody());
                } else {
                    // try fallback
                    ResponseEntity<BillingDTO[]> resp2 = rest.getForEntity(urlFallback, BillingDTO[].class);
                    if (resp2.getStatusCode().is2xxSuccessful() && resp2.getBody() != null) bills = Arrays.asList(resp2.getBody());
                }
            } catch (RestClientException e) {
                System.err.println("Robust report: failed to fetch bills from " + urlReport + " or fallback " + urlFallback + ": " + e.getMessage());
            }
        } else {
            try { bills = billingClient.getAllBills(); } catch (Exception ignored) {}
        }

        double totalRevenue = bills.stream().mapToDouble(b -> b.getTotalAmount() == null ? 0 : b.getTotalAmount()).sum();
        double totalTax = bills.stream().mapToDouble(b -> b.getTaxAmount() == null ? 0 : b.getTaxAmount()).sum();

        report.setProductReports(purchases);
        report.setInventoryStatus(inventoryList);
        report.setTotalRevenue(totalRevenue);
        report.setTotalTax(totalTax);

        return report;
    }

    /**
     * Backwards-compatible proxy method kept for controller – delegates to robust implementation.
     */
    public ReportResponseDTO generateReportProxy() {
        return generateReportRobust();
    }
}
