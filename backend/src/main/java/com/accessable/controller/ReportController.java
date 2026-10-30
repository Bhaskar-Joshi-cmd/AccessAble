
package com.accessable.controller;

import com.accessable.dto.CreateReportRequest;
import com.accessable.dto.ReviewReportRequest;
import com.accessable.dto.ReportListResponse;
import com.accessable.dto.ReportResponse;
import com.accessable.service.AdminKeyService;
import com.accessable.service.ReportService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final AdminKeyService adminKeyService;

    public ReportController(
            ReportService reportService,
            AdminKeyService adminKeyService) {
        this.reportService = reportService;
        this.adminKeyService = adminKeyService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestBody CreateReportRequest request) {

        ReportResponse response = reportService.createReport(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> listReports(
            @RequestParam(value = "status", required = false) String status,
            @RequestHeader(value = "X-Admin-Key", required = false)
                    String adminKey,
            HttpServletRequest request) {

        if (adminKey == null || !adminKeyService.validate(adminKey)) {

            Map<String, Object> error = new LinkedHashMap<>();
            error.put("timestamp", OffsetDateTime.now().toString());
            error.put("status", 401);
            error.put("error", "Unauthorized");
            error.put("message", "Missing or invalid admin key.");
            error.put("path", request.getRequestURI());

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);
        }

        String filterStatus;

        if (status == null || status.isBlank()) {
            filterStatus = "PENDING";
        } else {
            filterStatus = status.trim();
        }

        List<ReportListResponse> reports =
                reportService.listReports(filterStatus);

        return ResponseEntity.ok(reports);
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<Void> reviewReport(
            @PathVariable Long id,
            @RequestBody ReviewReportRequest request,
            @RequestHeader(value = "X-Admin-Key", required = false)
                    String adminKey,
            HttpServletRequest httpRequest) {

        if (adminKey == null || !adminKeyService.validate(adminKey)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Missing or invalid admin key.");
        }

        reportService.reviewReport(id, request.getDecision(), request.getAdminNote());

        return ResponseEntity.ok().build();
    }
}
