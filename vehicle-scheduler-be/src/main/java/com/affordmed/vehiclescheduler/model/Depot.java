package com.affordmed.vehiclescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Depot {
    private String depotId;
    private String depotName;
    private int mechanicHours;
}
