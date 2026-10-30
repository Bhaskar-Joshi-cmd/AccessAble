package com.accessable.exception;

import com.accessable.controller.FacilityController;
import com.accessable.dto.ApiError;
import com.accessable.service.AdminKeyService;
import com.accessable.service.FacilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacilityController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacilityService facilityService;

    @MockBean
    private AdminKeyService adminKeyService;

    @Test
    void unknownFacilityIdReturnsStandard404Error() throws Exception {
        // The mock service throws ResourceNotFoundException -> GlobalExceptionHandler maps to 404
        org.mockito.Mockito.when(facilityService.getFacilityById(999999L))
                .thenThrow(new ResourceNotFoundException("Facility not found with id: 999999"));

        mockMvc.perform(get("/api/facilities/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/facilities/999999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}