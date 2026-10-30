package com.accessable.config;

import com.accessable.service.BuildingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests that an explicitly configured production origin (via {@code app.frontend-origin}
 * / {@code FRONTEND_ORIGIN}) is allowed by CORS, and unrelated origins are rejected.
 */
@WebMvcTest(controllers = com.accessable.controller.BuildingController.class)
@Import(CorsConfig.class)
@TestPropertySource(properties = "app.frontend-origin=https://accessable-example.vercel.app")
class CorsConfigCustomOriginTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildingService buildingService;

    @Test
    void configuredProductionOriginIsAllowed() throws Exception {
        mockMvc.perform(options("/api/buildings")
                        .header("Origin", "https://accessable-example.vercel.app"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin",
                        "https://accessable-example.vercel.app"));
    }

    @Test
    void unrelatedOriginIsNotAllowedWithCustomOrigin() throws Exception {
        mockMvc.perform(options("/api/buildings")
                        .header("Origin", "http://evil.com"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}