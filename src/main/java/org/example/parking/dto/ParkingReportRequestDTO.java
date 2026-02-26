package org.example.parking.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ParkingReportRequestDTO(@NotNull(message = "Время заезда машин не должно быть пустым") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate start_date,
                                      @NotNull(message = "Время выезда машин не должно быть пустым") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate end_date) {
}
