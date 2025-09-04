package com.cok.couriertracking.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CourierGeoLocationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String COURIERS_PATH = "/api/v1/couriers";
    private static final String GEO_PATH = "/api/v1/couriers/geolocations";

    @Test
    void shouldAddLocationReturn200() throws Exception {
        var createCourierBody = """
                {
                  "fullName": "Can Test",
                  "licensePlate": "34ABC123"
                }
                """;
        var createResult = mockMvc.perform(post(COURIERS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCourierBody))
                .andExpect(status().isCreated())
                .andReturn();

        var createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long courierId = createJson.path("data").path("id").asLong();

        var locationBody = """
                {
                  "courierId": %d,
                  "lat": 41.0000,
                  "lng": 29.0000
                }
                """.formatted(courierId);

        mockMvc.perform(post(GEO_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.courierId", is((int) courierId)))
                .andExpect(jsonPath("$.data.lat", is(41.0)))
                .andExpect(jsonPath("$.data.lng", is(29.0)));
    }

}
