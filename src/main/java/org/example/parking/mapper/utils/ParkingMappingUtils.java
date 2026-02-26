package org.example.parking.mapper.utils;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.math.BigDecimal;


@Component
@Named("ParkingMappingUtils")
@RequiredArgsConstructor
public class ParkingMappingUtils {
    @Named("createDurationFromBigDecimal")
    public Duration createDurationFromBigDecimal(BigDecimal decimal){
        if (decimal == null){
            return Duration.ZERO;
        }
        return Duration.ofSeconds(decimal.longValue());
    }
}
