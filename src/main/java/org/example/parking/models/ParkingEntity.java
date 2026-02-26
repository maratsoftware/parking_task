package org.example.parking.models;

import jakarta.persistence.*;
import lombok.*;
import org.example.parking.models.enums.CarType;
import org.example.parking.models.enums.CarTypeConverter;

import java.time.Instant;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "parking")
public class ParkingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false, length = 20, name = "number")
    private String number;
    @Column(nullable = false, name = "time_in")
    private Instant timeIn;
    @Column(name = "time_out")
    private Instant timeOut;
    @Column(nullable = false, length = 15, name = "car_type")
    @Convert(converter = CarTypeConverter.class)
    private CarType carType;

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [id=" + id +
                ", number=" + number +
                ", timeIn=" + timeIn +
                ", timeOut=" + timeOut +
                ", carType=" + carType + "]";
    }
}
