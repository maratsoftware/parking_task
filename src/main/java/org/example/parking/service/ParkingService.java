package org.example.parking.service;

import org.example.parking.dto.*;

public interface ParkingService {
    ParkingEntryResponseDTO save(ParkingEntryRequestDTO requestDTO);
    ParkingExitResponseDTO update(ParkingExitRequestDTO requestDTO);
    ParkingReportResponseDTO generateReport(ParkingReportRequestDTO requestDTO);
}
