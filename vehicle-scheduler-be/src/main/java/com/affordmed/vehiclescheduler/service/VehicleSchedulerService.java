package com.affordmed.vehiclescheduler.service;

import com.affordmed.vehiclescheduler.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class VehicleSchedulerService {

    private final WebClient webClient;
    private final AuthService authService;

    public VehicleSchedulerService(WebClient affordmedWebClient, AuthService authService) {
        this.webClient = affordmedWebClient;
        this.authService = authService;
    }

    // ── Fetch all depots ──────────────────────────────────────────────────────

    public List<Depot> getAllDepots() {
        String token = authService.getBearerToken();

        DepotsResponse response = webClient.get()
                .uri("/depots")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(DepotsResponse.class)
                .block();

        return (response != null && response.getDepots() != null)
                ? response.getDepots()
                : List.of();
    }

    // ── Fetch vehicles for a specific depot ───────────────────────────────────

    public List<Vehicle> getVehiclesByDepot(String depotId) {
        String token = authService.getBearerToken();

        VehiclesResponse response = webClient.get()
                .uri("/depots/{depotId}/vehicles", depotId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(VehiclesResponse.class)
                .block();

        return (response != null && response.getVehicles() != null)
                ? response.getVehicles()
                : List.of();
    }

    // ── Schedule: knapsack optimization for one depot ─────────────────────────

    public ScheduleResult scheduleForDepot(String depotId) {
        // Get all depots to find the mechanic-hours budget for this depot
        List<Depot> depots = getAllDepots();
        Depot depot = depots.stream()
                .filter(d -> d.getDepotId().equals(depotId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Depot not found: " + depotId));

        List<Vehicle> vehicles = getVehiclesByDepot(depotId);
        int budget = depot.getMechanicHours();

        List<Vehicle> selected = knapsack(vehicles, budget);

        int totalDuration = selected.stream().mapToInt(Vehicle::getServiceDuration).sum();
        int totalScore = selected.stream().mapToInt(Vehicle::getOperationalImpactScore).sum();

        return new ScheduleResult(
                depot.getDepotId(),
                depot.getDepotName(),
                budget,
                selected,
                totalDuration,
                totalScore
        );
    }

    // ── Schedule: knapsack optimization for ALL depots ────────────────────────

    public List<ScheduleResult> scheduleForAllDepots() {
        List<Depot> depots = getAllDepots();
        List<ScheduleResult> results = new ArrayList<>();

        for (Depot depot : depots) {
            List<Vehicle> vehicles = getVehiclesByDepot(depot.getDepotId());
            int budget = depot.getMechanicHours();
            List<Vehicle> selected = knapsack(vehicles, budget);

            int totalDuration = selected.stream().mapToInt(Vehicle::getServiceDuration).sum();
            int totalScore = selected.stream().mapToInt(Vehicle::getOperationalImpactScore).sum();

            results.add(new ScheduleResult(
                    depot.getDepotId(),
                    depot.getDepotName(),
                    budget,
                    selected,
                    totalDuration,
                    totalScore
            ));
        }
        return results;
    }

    // ── 0/1 Knapsack (DP) ─────────────────────────────────────────────────────
    // Maximises totalOperationalImpactScore within mechanicHours budget.
    // Time: O(n * W), Space: O(n * W) where W = budget in hours.

    private List<Vehicle> knapsack(List<Vehicle> vehicles, int budget) {
        int n = vehicles.size();

        // dp[i][w] = max score using first i vehicles with w hours available
        int[][] dp = new int[n + 1][budget + 1];

        for (int i = 1; i <= n; i++) {
            Vehicle v = vehicles.get(i - 1);
            int weight = v.getServiceDuration();
            int value  = v.getOperationalImpactScore();

            for (int w = 0; w <= budget; w++) {
                dp[i][w] = dp[i - 1][w]; // don't take vehicle i
                if (weight <= w) {
                    dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - weight] + value);
                }
            }
        }

        // Backtrack to find which vehicles were selected
        List<Vehicle> selected = new ArrayList<>();
        int w = budget;
        for (int i = n; i >= 1; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                selected.add(vehicles.get(i - 1));
                w -= vehicles.get(i - 1).getServiceDuration();
            }
        }

        return selected;
    }
}
