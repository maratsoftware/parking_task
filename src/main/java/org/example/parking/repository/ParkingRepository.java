package org.example.parking.repository;

import org.example.parking.dto.ParkingReportIntermediateDTO;
import org.example.parking.models.ParkingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ParkingRepository extends JpaRepository<ParkingEntity, Long> {
    Optional<ParkingEntity> findByNumberAndTimeOutIsNull(String number);
    Boolean existsByNumberAndTimeOutIsNull(String number);
    @Query(nativeQuery = true, value = """
            SELECT (SELECT COUNT(*) FROM parking WHERE time_in BETWEEN :start AND :end) AS entries_count,
                   (SELECT COUNT(*) FROM parking WHERE time_out BETWEEN :start AND :end) AS exits_count,
                   AVG(EXTRACT(EPOCH FROM (time_out - time_in))) as interval
            FROM parking
            WHERE
                time_out BETWEEN :start AND :end;
            """)
    Optional<ParkingReportIntermediateDTO> findByDate(@Param("start") LocalDate startDate, @Param("end") LocalDate endDate);
}
