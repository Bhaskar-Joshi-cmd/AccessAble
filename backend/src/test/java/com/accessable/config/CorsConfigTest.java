package com.accessable.config;

import com.accessable.service.BuildingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link CorsConfig} CORS origin handling.
 *
 * Verifies that configured origins are accepted and unrelated origins are rejected.
 */
@WebMvcTest(controllers = com.accessable.controller.BuildingController.class)
@Import(CorsConfig.class)
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildingService buildingService;

    @Test
    void localhostOriginIsAllowed() throws Exception {
        mockMvc.perform(options("/api/buildings")
                        .header("Origin", "http://localhost:8001"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8001"));
    }

    @Test
    void localIpOriginIsAllowed() throws Exception {
        mockMvc.perform(options("/api/buildings")
                        .header("Origin", "http://127.0.0.1:8001"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://127.0.0.1:8001"));
    }

    @Test
    void unrelatedOriginIsNotAllowed() throws Exception {
        mockMvc.perform(options("/api/buildings")
                        .header("Origin", "http://evil.com"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void noOriginHeaderMeansNoCorsHeader() throws Exception {
        mockMvc.perform(options("/api/buildings"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}