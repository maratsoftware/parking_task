package org.example.parking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.parking.dto.*;
import org.example.parking.mapper.ParkingMapper;
import org.example.parking.repository.ParkingRepository;
import org.example.parking.service.ParkingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService {
    private final ParkingMapper mapper;
    private final ParkingRepository repository;

    @Override
    @Transactional
    public ParkingEntryResponseDTO save(ParkingEntryRequestDTO requestDTO) {
        log.info("Register car: {}", requestDTO);
        if (repository.existsByNumberAndTimeOutIsNull(requestDTO.number())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Car with number '%s' is already parked".formatted(requestDTO.number()));
        }
        var entry = mapper.toEntity(requestDTO);
        entry.setTimeIn(Instant.now(Clock.systemUTC()));
        var resultEntry = repository.save(entry);
        log.info("Registration successful: {}", resultEntry);
        return mapper.toEntryResponseDTO(resultEntry);
    }

    @Override
    @Transactional
    public ParkingExitResponseDTO update(ParkingExitRequestDTO requestDTO) {
        log.info("Update exit car: {}", requestDTO);
        var parkingEntry = repository.findByNumberAndTimeOutIsNull(requestDTO.number()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Car with number '%s' not found".formatted(requestDTO.number())));
        parkingEntry.setTimeOut(Instant.now(Clock.systemUTC()));
        var resultEntry = repository.save(parkingEntry);
        log.info("Car exit successful: {} ", resultEntry);
        return mapper.toExitResponseDTO(resultEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingReportResponseDTO generateReport(ParkingReportRequestDTO requestDTO) {
        log.info("Generation report between dates {} and {}", requestDTO.start_date(), requestDTO.end_date());
        var intermediateResult = repository.findByDate(requestDTO.start_date(), requestDTO.end_date())
                .orElse(new ParkingReportIntermediateDTO(0L,0L, BigDecimal.ZERO));
        var responseDTO = mapper.toReportResponseDTO(intermediateResult);
        log.info("Report about cars: {} between dates {} and {}", responseDTO, requestDTO.start_date(), requestDTO.end_date());
        return responseDTO;
    }
}
