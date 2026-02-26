package org.example.parking.dto;

import java.time.Duration;

public record ParkingReportResponseDTO(Long countCarIn, Long countCarOut, Duration duration) {
}
