package org.example.parking.dto;

import java.math.BigDecimal;

public record ParkingReportIntermediateDTO(Long countCarIn, Long countCarOut, BigDecimal avgParkingTime) {
}
