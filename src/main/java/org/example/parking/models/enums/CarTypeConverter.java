package org.example.parking.models.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter
public class CarTypeConverter implements AttributeConverter<CarType, String> {
    @Override
    public String convertToDatabaseColumn(CarType carType) {
        return carType.getDisplayName();
    }

    @Override
    public CarType convertToEntityAttribute(String displayName) {
        return Arrays.stream(CarType.values())
                .filter(s -> s.getDisplayName().equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
