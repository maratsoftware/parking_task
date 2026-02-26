package org.example.parking.service.impl;

import org.example.parking.dto.*;
import org.example.parking.mapper.ParkingMapper;
import org.example.parking.models.ParkingEntity;
import org.example.parking.models.enums.CarType;
import org.example.parking.repository.ParkingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceImplTest {
    @InjectMocks
    private ParkingServiceImpl parkingServiceImpl;
    @Mock
    private ParkingRepository parkingRepository;
    @Mock
    private ParkingMapper parkingMapper;
    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2024-06-12T12:00:00Z"),
            ZoneId.of("UTC")
    );

    @Test
    void should_register_new_car_successfully(){
        //given
        String number = "Ф777ФФ";
        ParkingEntryRequestDTO requestDTO = new ParkingEntryRequestDTO(number, CarType.TRUCK);
        ParkingEntity mappedEntity = ParkingEntity.builder().id(12L).number(number).carType(CarType.TRUCK).build();
        ParkingEntity savedEntity = ParkingEntity.builder().id(12L).number(number).carType(CarType.TRUCK).timeIn(Instant.now(fixedClock)).build();
        //when
        when(parkingRepository.existsByNumberAndTimeOutIsNull(number)).thenReturn(false);
        when(parkingMapper.toEntity(requestDTO)).thenReturn(mappedEntity);
        when(parkingRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(parkingMapper.toEntryResponseDTO(savedEntity)).thenReturn(new ParkingEntryResponseDTO(savedEntity.getTimeIn()));
        //then
        ParkingEntryResponseDTO responseDTO = parkingServiceImpl.save(requestDTO);
        assertThat(responseDTO.time_in()).isEqualTo(savedEntity.getTimeIn());
        verify(parkingRepository, times(1)).save(mappedEntity);
        verify(parkingRepository, times(1)).existsByNumberAndTimeOutIsNull(number);
    }

    @Test
    void should_throw_conflict_exception_when_car_already_parked(){
        //given
        String number = "Ф777ФФ";
        ParkingEntryRequestDTO requestDTO = new ParkingEntryRequestDTO(number, CarType.TRUCK);
        //when
        when(parkingRepository.existsByNumberAndTimeOutIsNull(number)).thenReturn(true);
        //then
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> parkingServiceImpl.save(requestDTO));
        assertEquals(HttpStatus.CONFLICT,responseStatusException.getStatusCode());
        assertTrue(responseStatusException.getMessage().contains("Car with number '%s' is already parked".formatted(number)));
        verify(parkingRepository, never()).save(any());
    }

    @Test
    void should_update_existing_car_successfully(){
        //given
        String number = "Ф777ФФ";
        ParkingExitRequestDTO requestDTO = new ParkingExitRequestDTO(number);
        ParkingEntity findEntity = ParkingEntity.builder()
                .id(12L).number(number).carType(CarType.TRUCK)
                .timeIn(Instant.now(fixedClock).minusMillis(3600))
                .build();
        ParkingEntity savedEntity = ParkingEntity.builder()
                .id(12L).number(number).carType(CarType.TRUCK)
                .timeIn(Instant.now(fixedClock).minusMillis(3600))
                .timeOut(Instant.now(fixedClock))
                .build();
        //when
        when(parkingRepository.findByNumberAndTimeOutIsNull(requestDTO.number())).thenReturn(Optional.of(findEntity));
        when(parkingRepository.save(findEntity)).thenReturn(savedEntity);
        when(parkingMapper.toExitResponseDTO(savedEntity)).thenReturn(new ParkingExitResponseDTO(savedEntity.getTimeOut()));
        //then
        ParkingExitResponseDTO responseDTO = parkingServiceImpl.update(requestDTO);
        assertThat(responseDTO.time_out()).isEqualTo(savedEntity.getTimeOut());
        verify(parkingRepository, times(1)).save(findEntity);
    }

    @Test
    void should_throw_not_found_exception_when_car_not_parked(){
        //given
        String number = "Ошибка";
        ParkingExitRequestDTO requestDTO = new ParkingExitRequestDTO(number);
        //when
        when(parkingRepository.findByNumberAndTimeOutIsNull(number)).thenReturn(Optional.empty());
        //then
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> parkingServiceImpl.update(requestDTO));
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatusCode());
        assertTrue(responseStatusException.getMessage().contains("Car with number '%s' not found".formatted(requestDTO.number())));
        verify(parkingRepository, never()).save(any());
    }

    @Test
    void should_generate_new_report_between_dates() {
        //given
        LocalDate start = LocalDate.of(2023, 6, 1);
        LocalDate end = LocalDate.of(2023, 6, 2);
        ParkingReportRequestDTO requestDTO = new ParkingReportRequestDTO(start, end);
        ParkingReportIntermediateDTO resultQuery = new ParkingReportIntermediateDTO(2L, 2L, BigDecimal.valueOf(1930.0));
        ParkingReportResponseDTO mappedResult = new ParkingReportResponseDTO(2L, 2L, Duration.parse("PT32M30S"));
        //when
        when(parkingRepository.findByDate(start, end)).thenReturn(Optional.of(resultQuery));
        when(parkingMapper.toReportResponseDTO(resultQuery)).thenReturn(mappedResult);
        //then
        ParkingReportResponseDTO responseDTO = parkingServiceImpl.generateReport(requestDTO);
        assertThat(responseDTO.countCarIn()).isEqualTo(2L);
        assertThat(responseDTO.countCarOut()).isEqualTo(2L);
        assertThat(responseDTO.duration()).isEqualTo(Duration.parse("PT32M30S"));
        verify(parkingRepository, times(1)).findByDate(start, end);
    }

    @Test
    void should_generate_empty_report_between_incorrect_dates(){
        //given
        LocalDate start = LocalDate.of(2023, 6, 2);
        LocalDate end = LocalDate.of(2023, 6, 1);
        ParkingReportRequestDTO requestDTO = new ParkingReportRequestDTO(start, end);
        ParkingReportIntermediateDTO resultQuery = new ParkingReportIntermediateDTO(0L, 0L, BigDecimal.ZERO);
        ParkingReportResponseDTO mappedResult = new ParkingReportResponseDTO(0L, 0L, Duration.ZERO);
        //when
        when(parkingRepository.findByDate(start, end)).thenReturn(Optional.empty());
        when(parkingMapper.toReportResponseDTO(resultQuery)).thenReturn(mappedResult);
        //then
        ParkingReportResponseDTO responseDTO = parkingServiceImpl.generateReport(requestDTO);
        assertThat(responseDTO.countCarIn()).isEqualTo(0L);
        assertThat(responseDTO.countCarOut()).isEqualTo(0L);
        assertThat(responseDTO.duration()).isEqualTo(Duration.ZERO);
        verify(parkingRepository, times(1)).findByDate(start, end);
    }
}