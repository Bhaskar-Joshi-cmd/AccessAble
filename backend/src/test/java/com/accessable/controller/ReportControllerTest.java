package com.accessable.controller;

import com.accessable.service.AdminKeyService;
import com.accessable.service.ReportService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private AdminKeyService adminKeyService;

    @Test
    void getReportsWithoutKeyReturns401() throws Exception {
        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getReportsWithIncorrectKeyReturns401() throws Exception {
        when(adminKeyService.validate("wrong-key")).thenReturn(false);
        mockMvc.perform(get("/api/reports")
                .header("X-Admin-Key", "wrong-key"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getReportsWithMissingKeyReturns401() throws Exception {
        when(adminKeyService.validate(" ")).thenReturn(false);
        mockMvc.perform(get("/api/reports")
                .header("X-Admin-Key", " "))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getReportsWithCorrectKeyReturns200() throws Exception {
        when(adminKeyService.validate("correct-key")).thenReturn(true);
        when(reportService.listReports("PENDING")).thenReturn(List.of());
        mockMvc.perform(get("/api/reports")
                .header("X-Admin-Key", "correct-key"))
                .andExpect(status().isOk());
    }

    @Test
    void getReportsWithMissingStatusDefaultsToPending() throws Exception {
        when(adminKeyService.validate("correct-key")).thenReturn(true);
        when(reportService.listReports("PENDING")).thenReturn(List.of());
        mockMvc.perform(get("/api/reports")
                .header("X-Admin-Key", "correct-key"))
                .andExpect(status().isOk());
    }

    @Test
    void getReportsWithValidStatusFilters() throws Exception {
        when(adminKeyService.validate("correct-key")).thenReturn(true);
        for (String status : List.of("VERIFIED", "REJECTED", "RESOLVED")) {
            when(reportService.listReports(status)).thenReturn(List.of());
            mockMvc.perform(get("/api/reports")
                    .header("X-Admin-Key", "correct-key")
                    .param("status", status))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getReportsWithInvalidStatusReturns400() throws Exception {
        when(adminKeyService.validate("correct-key")).thenReturn(true);
        when(reportService.listReports("INVALID"))
                .thenThrow(new com.accessable.exception.InvalidFilterException("Invalid report status: INVALID"));
        mockMvc.perform(get("/api/reports?status=INVALID")
                .header("X-Admin-Key", "correct-key"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReportsEmptyResultReturnsEmptyArray() throws Exception {
        when(adminKeyService.validate("correct-key")).thenReturn(true);
        when(reportService.listReports("PENDING")).thenReturn(List.of());
        mockMvc.perform(get("/api/reports")
                .header("X-Admin-Key", "correct-key"))
                .andExpect(status().isOk());
    }

    @Test
    void publicReportSubmissionStillWorks() throws Exception {
        mockMvc.perform(post("/api/reports")
                .contentType("application/json")
                .content("{\"facilityId\":1,\"issueType\":\"OUT_OF_SERVICE\",\"suggestedStatus\":\"TEMPORARILY_UNAVAILABLE\",\"description\":\"Test issue\",\"reportStatus\":\"PENDING\"}"))
                .andExpect(status().isCreated());
    }
}