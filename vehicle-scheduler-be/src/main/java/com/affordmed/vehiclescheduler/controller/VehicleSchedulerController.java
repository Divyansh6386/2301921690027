package com.affordmed.vehiclescheduler.controller;

import com.affordmed.vehiclescheduler.model.*;
import com.affordmed.vehiclescheduler.service.AuthService;
import com.affordmed.vehiclescheduler.service.VehicleSchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VehicleSchedulerController {

    private final VehicleSchedulerService schedulerService;
    private final AuthService authService;

    public VehicleSchedulerController(VehicleSchedulerService schedulerService,
                                      AuthService authService) {
        this.schedulerService = schedulerService;
        this.authService = authService;
    }

    // ── 1. Register with Affordmed test server ────────────────────────────────
    // POST http://localhost:8080/api/register
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register() {
        RegistrationResponse response = authService.register();
        return ResponseEntity.ok(response);
    }

    // ── 2. Set credentials manually (after you have clientID + clientSecret) ──
    // POST http://localhost:8080/api/credentials
    // Body: { "clientID": "...", "clientSecret": "..." }
    @PostMapping("/credentials")
    public ResponseEntity<Map<String, String>> setCredentials(@RequestBody Map<String, String> body) {
        authService.setClientCredentials(body.get("clientID"), body.get("clientSecret"));
        return ResponseEntity.ok(Map.of("message", "Credentials set successfully"));
    }

    // ── 3. Get all depots ─────────────────────────────────────────────────────
    // GET http://localhost:8080/api/depots
    @GetMapping("/depots")
    public ResponseEntity<List<Depot>> getAllDepots() {
        List<Depot> depots = schedulerService.getAllDepots();
        return ResponseEntity.ok(depots);
    }

    // ── 4. Get vehicles for a depot ───────────────────────────────────────────
    // GET http://localhost:8080/api/depots/{depotId}/vehicles
    @GetMapping("/depots/{depotId}/vehicles")
    public ResponseEntity<List<Vehicle>> getVehiclesByDepot(@PathVariable String depotId) {
        List<Vehicle> vehicles = schedulerService.getVehiclesByDepot(depotId);
        return ResponseEntity.ok(vehicles);
    }

    // ── 5. Get optimized schedule for a single depot ──────────────────────────
    // GET http://localhost:8080/api/depots/{depotId}/schedule
    @GetMapping("/depots/{depotId}/schedule")
    public ResponseEntity<ScheduleResult> scheduleForDepot(@PathVariable String depotId) {
        ScheduleResult result = schedulerService.scheduleForDepot(depotId);
        return ResponseEntity.ok(result);
    }

    // ── 6. Get optimized schedule for ALL depots ──────────────────────────────
    // GET http://localhost:8080/api/schedule
    @GetMapping("/schedule")
    public ResponseEntity<List<ScheduleResult>> scheduleForAllDepots() {
        List<ScheduleResult> results = schedulerService.scheduleForAllDepots();
        return ResponseEntity.ok(results);
    }

    // ── Health check ──────────────────────────────────────────────────────────
    // GET http://localhost:8080/api/health
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "vehicle-scheduler-be"));
    }
}
