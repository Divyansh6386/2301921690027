package com.affordmed.vehiclescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Vehicle {
    private String vehicleId;
    private String vehicleName;
    private int serviceDuration;
    private int operationalImpactScore;
}
