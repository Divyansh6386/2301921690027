package com.affordmed.vehiclescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class ScheduleResult {
    private String depotId;
    private String depotName;
    private int mechanicHoursAvailable;
    private List<Vehicle> selectedVehicles;
    private int totalServiceDuration;
    private int totalOperationalImpactScore;
}
