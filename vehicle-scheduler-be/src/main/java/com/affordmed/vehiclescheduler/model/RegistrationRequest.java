package com.affordmed.vehiclescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class RegistrationRequest {
    private String companyName;
    private String ownerName;
    private String rollNo;
    private String ownerEmail;
    private String accessCode;
}
