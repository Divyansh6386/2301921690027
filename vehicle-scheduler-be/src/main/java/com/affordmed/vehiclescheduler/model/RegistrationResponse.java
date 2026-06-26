package com.affordmed.vehiclescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class RegistrationResponse {
    private String companyName;
    private String clientID;
    private String clientSecret;
    private String ownerName;
    private String ownerEmail;
    private String rollNo;
}
