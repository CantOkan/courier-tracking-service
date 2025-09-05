# Courier Tracking Service
A Spring Boot application for tracking courier locations, computing traveled distance, and detecting store entrances.

## Technology Stack
- **Framework**: Spring Boot 3.5.5
- **Java Version**: 21
- **Database**: PostgreSQL
- **Documentation**: OpenAPI/Swagger UI
- **Testing**: TestContainers for integration tests
- **Build Tool**: Maven

## ✨ Features
- **Courier management API (CRUD)**: Create courier, get courier by ID, delete courier, and fetch
- **Geofencing**: Detects when a courier is within 100 m of a store and logs the entrance
- **Location logging**: Append-only CourierGeolocation records
- **Total distance (strategy-based)**: Pluggable distance calculation, Haversine by default

### Design Patterns
**Strategy Pattern— Distance Calculation** <br>
Pluggable DistanceCalculationStrategy with Haversine as the default implementation (easy to swap in other strategies like Vincenty).

**Observer Pattern — Events** <br>
When a courier enters a store geofence, a CourierStoreObserver list is notified (e.g., StoreEntranceService for logging). Observers are decoupled and independently extendable.


## 📋 Prerequisites
- **Java 21+**
- **Maven 3.9+**
- **PostgreSQL** (or Docker for containerized setup)
- **Docker** (for the containerized option)
- **Port 8080** (free on your machine)

## 🏗️ Project Structure
````
src/
 ├─ main/java/com/cok/couriertracking/
 │   ├─ controller/            # REST controllers
 │   ├─ domain/                # JPA entities (Courier, CourierGeolocation, Store, StoreEntranceLog)
 │   ├─ dto/                   # API models (requests/responses)
 │   ├─ exception/             # Custom exceptions + GlobalExceptionController
 │   ├─ mapper/                # CourierMapper (request → entity)
 │   ├─ observer/              # Observer interfaces + implementations
 │   ├─ repository/            # Spring Data JPA repositories
 │   ├─ service/               # Services (incl. transactional unit)
 │   └─ strategy/              # DistanceCalculationStrategy (+ Haversine)
 └─ main/resources/
     ├─ application.yml / application-docker.yml
     └─ stores.json            # Seeded store coordinates

````

## ⚙️ How to Run
```bash

# Clone the repository
git clone <repository-url>
cd couriertracking

# Build the project
./mvnw clean install

# Run the application (local dev)
./mvnw spring-boot:run
```

### Using Docker Compose
```bash
# Build images and start PostgreSQL + the app
docker compose up --build -d
# (If you have the old plugin) docker-compose up --build -d

# View logs
docker compose logs -f app

# Stop & Cleanup (Docker)
docker compose down -v
```

### Manual JAR Execution
```bash
# Build JAR
./mvnw clean package

# Run JAR (wildcard handles versioned filename)
java -jar target/*.jar
```

## 📖 API Documentation
Once the application is running, access the API documentation at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`
- **Postman Collection**: [`docs/CourierTracking.postman_collection.json`](docs/CourierTracking.postman_collection.json)
