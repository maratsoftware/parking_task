package org.example.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ParkingExitRequestDTO(
        @NotBlank(message = "Номер должен быть заполнен")
        @Size(min = 3, max = 20, message = "Длина номера превышает не соответствует диапазону от 3 до 20")
        String number) {
}
