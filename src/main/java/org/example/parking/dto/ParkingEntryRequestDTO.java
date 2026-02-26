package org.example.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.parking.models.enums.CarType;

public record ParkingEntryRequestDTO(
        @NotBlank(message = "Номер должен быть заполнен")
        @Size(min = 3, max = 20, message = "Длина номера превышает не соответствует диапазону от 3 до 20")
        String number,
        @NotNull(message = "Тип машины не должен быть пустым") CarType carType) {
}
