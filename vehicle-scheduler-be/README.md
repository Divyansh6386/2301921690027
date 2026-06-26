# Vehicle Maintenance Scheduler Microservice

Spring Boot microservice that fetches depot and vehicle data from the Affordmed evaluation server, then applies a **0/1 Knapsack algorithm** to maximize the operational impact score within the available mechanic-hour budget.

## Tech Stack
- Java 17
- Spring Boot 3.2
- Spring WebFlux (WebClient for HTTP calls)
- Lombok

## Setup

### 1. Fill in your credentials in `application.properties`
```properties
affordmed.company-name=YOUR_COMPANY_NAME
affordmed.owner-name=YOUR_NAME
affordmed.roll-no=YOUR_ROLL_NO
affordmed.owner-email=YOUR_EMAIL
affordmed.access-code=YOUR_ACCESS_CODE   # from your email
```

### 2. Build and Run
```bash
./mvnw spring-boot:run
```
Server starts at `http://localhost:8080`

---

## API Endpoints

### Step 1 – Register (POST once)
```
POST http://localhost:8080/api/register
```
Returns `clientID` and `clientSecret`. Save these.

### Step 2 – Set credentials (if you already have them)
```
POST http://localhost:8080/api/credentials
Body: { "clientID": "xxx", "clientSecret": "yyy" }
```

### Step 3 – Get all depots
```
GET http://localhost:8080/api/depots
```

### Step 4 – Get vehicles for a depot
```
GET http://localhost:8080/api/depots/{depotId}/vehicles
```

### Step 5 – Get optimized schedule for one depot
```
GET http://localhost:8080/api/depots/{depotId}/schedule
```

### Step 6 – Get optimized schedule for ALL depots
```
GET http://localhost:8080/api/schedule
```

### Health Check
```
GET http://localhost:8080/api/health
```

---

## Algorithm
Uses **Dynamic Programming 0/1 Knapsack**:
- **Weight** = `serviceDuration` (hours per vehicle)
- **Value** = `operationalImpactScore`
- **Capacity** = `mechanicHours` (daily budget per depot)
- **Goal** = maximize total `operationalImpactScore` without exceeding budget

Time complexity: O(n × W) where n = number of vehicles, W = mechanic-hour budget.
