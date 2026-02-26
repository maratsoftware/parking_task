package org.example.parking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.parking.dto.*;
import org.example.parking.service.ParkingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parking")
public class ParkingController {
    private final ParkingService parkingService;

    @PostMapping("/entry")
    public ResponseEntity<ParkingEntryResponseDTO> setEntry(@RequestBody @Valid ParkingEntryRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingService.save(requestDTO));
    }

    @PostMapping("/exit")
    public ResponseEntity<ParkingExitResponseDTO> updateEntry(@RequestBody @Valid ParkingExitRequestDTO requestDTO){
        return ResponseEntity.ok().body(parkingService.update(requestDTO));
    }

    @GetMapping("/report")
    public ResponseEntity<ParkingReportResponseDTO> getReport(@Valid ParkingReportRequestDTO requestDTO){
        return ResponseEntity.ok().body(parkingService.generateReport(requestDTO));
    }
}
