package org.example.parking.mapper;

import org.example.parking.dto.*;
import org.example.parking.mapper.utils.ParkingMappingUtils;
import org.example.parking.models.ParkingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE,
uses = {ParkingMappingUtils.class})
public interface ParkingMapper {
    ParkingEntity toEntity (ParkingEntryRequestDTO requestDTO);
    @Mapping(source = "timeIn", target = "time_in")
    ParkingEntryResponseDTO toEntryResponseDTO(ParkingEntity entity);
    @Mapping(source = "timeOut", target = "time_out")
    ParkingExitResponseDTO toExitResponseDTO(ParkingEntity entity);
    @Mapping(target = "duration", source = "avgParkingTime", qualifiedByName = {"ParkingMappingUtils", "createDurationFromBigDecimal"})
    ParkingReportResponseDTO toReportResponseDTO(ParkingReportIntermediateDTO intermediate);
}
