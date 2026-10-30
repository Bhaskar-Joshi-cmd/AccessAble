package com.accessable.controller;

import com.accessable.dto.SummaryResponse;
import com.accessable.service.SummaryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the summary endpoint.
 */
@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    /**
     * GET /api/summary
     * Returns aggregate counts of facilities and pending reports.
     */
    @GetMapping
    public SummaryResponse getSummary() {
        return summaryService.getSummary();
    }
}