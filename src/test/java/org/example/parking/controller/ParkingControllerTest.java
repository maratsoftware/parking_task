package org.example.parking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.parking.dto.*;
import org.example.parking.models.enums.CarType;
import org.example.parking.service.ParkingService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ParkingController.class)
class ParkingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ParkingService parkingService;
    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2024-06-12T12:00:00Z"),
            ZoneId.of("UTC")
    );
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_return_entry_response_and_status_201() throws Exception {
        //given
        ParkingEntryRequestDTO requestDTO = new ParkingEntryRequestDTO("У666УУ", CarType.TRUCK);
        Instant expectedTime = Instant.now(fixedClock);
        ParkingEntryResponseDTO responseDTO = new ParkingEntryResponseDTO(expectedTime);
        //when
        when(parkingService.save(any(ParkingEntryRequestDTO.class))).thenReturn(responseDTO);
        //then
        mockMvc.perform(post("/api/v1/parking/entry")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.time_in")
                        .value(expectedTime.toString()));
    }

    @Test
    void should_return_exit_response_and_status_200() throws Exception {
        //given
        ParkingExitRequestDTO requestDTO = new ParkingExitRequestDTO("Y666YY");
        Instant expectedTime = Instant.now(fixedClock);
        ParkingExitResponseDTO responseDTO = new ParkingExitResponseDTO(expectedTime);
        //when
        when(parkingService.update(any(ParkingExitRequestDTO.class))).thenReturn(responseDTO);
        //then
        mockMvc.perform(post("/api/v1/parking/exit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time_out").value(expectedTime.toString()));
    }

    @Test
    void should_return_report_response_and_status_200() throws Exception {
        //given
        LocalDate startDate = LocalDate.of(2023, 6, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 2);
        ParkingReportResponseDTO responseDTO = new ParkingReportResponseDTO(2L, 2L, Duration.parse("PT32M30S"));
        //when
        when(parkingService.generateReport(any(ParkingReportRequestDTO.class))).thenReturn(responseDTO);
        //then
        mockMvc.perform(get("/api/v1/parking/report").contentType("application/json")
                        .param("start_date", startDate.toString())
                        .param("end_date", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countCarIn").value(2L))
                .andExpect(jsonPath("$.countCarOut").value(2L))
                .andExpect(jsonPath("$.duration").value("PT32M30S"));
    }

}