package lectio.controller;

import lectio.cal.Day;
import lectio.cal.Readings;
import lectio.cal.Reading;
import lectio.cal.LiturgicalColor;
import lectio.cal.Memorial;
import lectio.service.LiturgicalYearService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map; // Keep for sampleDayData if still used for readings, though direct objects are better

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(DayController.class)
public class DayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LiturgicalYearService liturgicalYearService;

    @Test
    public void testGetDayWithContentNegotiation() throws Exception {
        // Prepare mock data
        Reading ot = new Reading("Gen 1:1", "OT Text", "In the beginning...");
        Reading ep = new Reading("Rom 1:1", "EP Text", "Paul, a servant...");
        Reading go = new Reading("John 1:1", "GO Text", "In the beginning was the Word...");
        Reading ps = new Reading("Ps 23", "PS Text", "The Lord is my shepherd...");
        Readings readings = new Readings("A Glorious Theme", ot, ep, go, ps, null);

        Day sampleDayObject = new Day(
            "Feast of St. Test",
            LocalDate.now(),
            Collections.emptyList(),
            LiturgicalColor.GREEN,
            readings
        );

        // Mock service call
        when(liturgicalYearService.currentDay(any(LocalDate.class))).thenReturn(sampleDayObject);

        // Test JSON response
        mockMvc.perform(get("/day")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Feast of St. Test"))
                .andExpect(jsonPath("$.liturgicalColor").value("GREEN"))
                .andExpect(jsonPath("$.readings.theme").value("A Glorious Theme"))
                .andExpect(jsonPath("$.readings.ot.sweRef").value("Gen 1:1"));

        // Test HTML response
        mockMvc.perform(get("/day")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Feast of St. Test")))
                .andExpect(content().string(containsString("A Glorious Theme")))
                .andExpect(content().string(containsString("Gen 1:1")));
    }

    @Test
    public void testGetNextDayWithContentNegotiation() throws Exception {
        String testDateString = "2024-03-10";
        LocalDate testDate = LocalDate.parse(testDateString);

        Reading goNext = new Reading("John 3:16", "GO Next Text", "For God so loved the world...");
        Readings readingsNext = new Readings("Next Theme", null, null, goNext, null, null);
        Day sampleNextDayObject = new Day(
            "Next Feast Day",
            testDate.plusDays(1),
            Collections.emptyList(),
            LiturgicalColor.RED,
            readingsNext
        );

        when(liturgicalYearService.nextDay(any(LocalDate.class))).thenReturn(sampleNextDayObject);

        // Test JSON response for /next/{date}
        mockMvc.perform(get("/next/" + testDateString)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Next Feast Day"))
                .andExpect(jsonPath("$.readings.theme").value("Next Theme"))
                .andExpect(jsonPath("$.readings.go.sweRef").value("John 3:16"));

        // Test HTML response for /next/{date}
        mockMvc.perform(get("/next/" + testDateString)
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Next Feast Day")))
                .andExpect(content().string(containsString("Next Theme")))
                .andExpect(content().string(containsString("John 3:16")));
    }

    @Test
    public void testGetPreviousDayWithContentNegotiation() throws Exception {
        String testDateString = "2024-03-10";
        LocalDate testDate = LocalDate.parse(testDateString);

        Reading psPrev = new Reading("Ps 1:1", "PS Prev Text", "Blessed is the one...");
        Readings readingsPrev = new Readings("Previous Theme", null, null, null, psPrev, null);
        Day samplePrevDayObject = new Day(
            "Previous Feast Day",
            testDate.minusDays(1),
            Collections.emptyList(),
            LiturgicalColor.WHITE,
            readingsPrev
        );

        when(liturgicalYearService.previousDay(any(LocalDate.class))).thenReturn(samplePrevDayObject);

        // Test JSON response for /previous/{date}
        mockMvc.perform(get("/previous/" + testDateString)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Previous Feast Day"))
                .andExpect(jsonPath("$.readings.theme").value("Previous Theme"))
                .andExpect(jsonPath("$.readings.ps.sweRef").value("Ps 1:1"));

        // Test HTML response for /previous/{date}
        mockMvc.perform(get("/previous/" + testDateString)
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("Previous Feast Day")))
                .andExpect(content().string(containsString("Previous Theme")))
                .andExpect(content().string(containsString("Ps 1:1")));
    }

    @Test
    public void testGetDayReturnsJsonByDefault() throws Exception {
        // Prepare mock data (similar to testGetDayWithContentNegotiation)
        Reading ot = new Reading("Gen 1:1", "OT Text", "In the beginning...");
        Reading ep = new Reading("Rom 1:1", "EP Text", "Paul, a servant...");
        Reading go = new Reading("John 1:1", "GO Text", "In the beginning was the Word...");
        Reading ps = new Reading("Ps 23", "PS Text", "The Lord is my shepherd...");
        // Assuming 'alt' is the 6th parameter for Readings constructor based on previous successful tests
        Readings readings = new Readings("Test Theme", ot, ep, go, ps, null);

        Day sampleDay = new Day(
            "Feast of St. Default",
            LocalDate.now(),
            Collections.emptyList(),
            LiturgicalColor.GREEN,
            readings
        );

        when(liturgicalYearService.currentDay(any(LocalDate.class))).thenReturn(sampleDay);

        // Test JSON response without explicit Accept header for JSON
        mockMvc.perform(get("/day")) // No .accept() header
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Should default to JSON
                .andExpect(jsonPath("$.name").value("Feast of St. Default"))
                .andExpect(jsonPath("$.readings.theme").value("Test Theme"));

        // Test with Accept: */*
        mockMvc.perform(get("/day").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Should still default to JSON
                .andExpect(jsonPath("$.name").value("Feast of St. Default"));
    }
}
