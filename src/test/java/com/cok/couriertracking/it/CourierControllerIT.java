package com.cok.couriertracking.it;

import com.jayway.jsonpath.JsonPath;
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
class CourierControllerIT {

    @Autowired
    MockMvc mockMvc;

    private static final String PATH = "/api/v1/couriers";
    private static final String GEO_PATH = "/api/v1/couriers/geolocations";

    @Test
    void shouldPersistsCourierReturn201AndBody() throws Exception {
        var body = """
                {
                  "fullName": "Can Test",
                  "licensePlate": "34ABC123"
                }
                """;
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.fullName", is("Can Test")))
                .andExpect(jsonPath("$.data.licensePlate", is("34ABC123")));

    }

    @Test
    void shouldReturnsPersistedCourier() throws Exception {

        var body = """
                {
                  "fullName": "Can Test",
                  "licensePlate": "34ABC123"
                }
                """;
        var createResult = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        var responseJson = createResult.getResponse().getContentAsString();
        Long id = JsonPath.parse(responseJson).read("$.data.id", Number.class).longValue();

        mockMvc.perform(get(PATH + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(id.intValue())))
                .andExpect(jsonPath("$.data.fullName", is("Can Test")))
                .andExpect(jsonPath("$.data.licensePlate", is("34ABC123")));
    }

    @Test
    void shouldReturnTotalDistanceZero() throws Exception {
        var body = """
                {
                  "fullName": "Can Test",
                   "licensePlate": "34ABC123"
                }
                """;

        var createResult = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        var responseJson = createResult.getResponse().getContentAsString();
        Long id = JsonPath.parse(responseJson).read("$.data.id", Number.class).longValue();

        mockMvc.perform(get(PATH + "/{id}/total-distance", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(0.0)));
    }

    @Test
    void shouldDeleteCourierReturn200() throws Exception {
        var body = """
                {
                    "fullName": "Can Test",
                   "licensePlate": "34ABC123"
                }
                """;

        var createResult = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        var responseJson = createResult.getResponse().getContentAsString();
        Long id = JsonPath.parse(responseJson).read("$.data.id", Number.class).longValue();

        mockMvc.perform(delete(PATH + "/{id}", id))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAccumulatedTotalDistance() throws Exception {
        var body = """
                {
                  "fullName": "Can Test",
                   "licensePlate": "34ABC123"
                }
                """;

        var createResult = mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();

        var responseJson = createResult.getResponse().getContentAsString();
        Long id = JsonPath.parse(responseJson).read("$.data.id", Number.class).longValue();

        var firstGeoLocation = """
                {
                  "courierId": %d,
                  "lat": 41.0000,
                  "lng": 29.0000
                }
                """.formatted(id);

        var secondGeoLocation = """
                {
                        "courierId": %d,
                        "lat": 41.0000,
                        "lng": 29.0030
                      }
                """.formatted(id);

        mockMvc.perform(post(GEO_PATH).contentType(MediaType.APPLICATION_JSON).content(firstGeoLocation))
                .andExpect(status().isOk());

        mockMvc.perform(post(GEO_PATH).contentType(MediaType.APPLICATION_JSON).content(secondGeoLocation))
                .andExpect(status().isOk());

        mockMvc.perform(get(PATH + "/{id}/total-distance", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", greaterThan(0.0)));
    }


}
