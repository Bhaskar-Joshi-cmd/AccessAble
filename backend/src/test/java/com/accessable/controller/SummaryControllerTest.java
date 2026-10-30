package com.accessable.controller;

import com.accessable.dto.SummaryResponse;
import com.accessable.service.SummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer test for SummaryController. Uses MockMvc and mocks the service —
 * no database needed. Verifies summary JSON shape and counts.
 */
@WebMvcTest(SummaryController.class)
class SummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SummaryService summaryService;

    @Test
    void getSummaryReturnsCounts() throws Exception {
        SummaryResponse summary = new SummaryResponse(6, 4, 0, 1, 1, 2);
        when(summaryService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFacilities").value(6))
                .andExpect(jsonPath("$.available").value(4))
                .andExpect(jsonPath("$.temporarilyUnavailable").value(0))
                .andExpect(jsonPath("$.underMaintenance").value(1))
                .andExpect(jsonPath("$.unknown").value(1))
                .andExpect(jsonPath("$.pendingReports").value(2));
    }

    @Test
    void getSummaryZeroCounts() throws Exception {
        SummaryResponse summary = new SummaryResponse(0, 0, 0, 0, 0, 0);
        when(summaryService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFacilities").value(0))
                .andExpect(jsonPath("$.available").value(0))
                .andExpect(jsonPath("$.temporarilyUnavailable").value(0))
                .andExpect(jsonPath("$.underMaintenance").value(0))
                .andExpect(jsonPath("$.unknown").value(0))
                .andExpect(jsonPath("$.pendingReports").value(0));
    }
}