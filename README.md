# Reward Service

A Spring Boot microservice for managing gamification, rewards, badges, and leaderboards.

## Features

- **Points Calculation Engine** - Configurable rule-based point system
- **Gamification System** - Event-driven rewards for user activities
- **Badge Achievements** - Automatic and manual badge assignment
- **Leaderboard Ranking** - Daily, weekly, monthly, and all-time rankings
- **Contribution Tracking** - Track user contributions across the platform
- **Event-Driven Architecture** - Kafka integration for async processing
- **Analytics & Statistics** - Comprehensive reward statistics API

## Tech Stack

- Java 17
- Spring Boot 3.2.4
- Spring Data JPA
- Spring Security (JWT)
- Spring Kafka
- PostgreSQL
- MapStruct
- Lombok
- JaCoCo (80% coverage requirement)

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Apache Kafka

### Configuration

Update `application.yaml` with your environment settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/reward_service_db
    username: postgres
    password: root

  kafka:
    bootstrap-servers: localhost:9092

app:
  jwt:
    secret: ${JWT_SECRET}
```

### Running Locally

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

### Running with Docker

```bash
# Build Docker image
docker build -t reward-service:latest .

# Run container
docker run -p 8085:8085 reward-service:latest
```

## API Endpoints

### Reward APIs

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/rewards/users/{userId}/points` | Get user points | ENGINEER |
| GET | `/api/rewards/users/{userId}/transactions` | Get user transactions | ENGINEER |
| POST | `/api/rewards/points` | Add points | ADMIN |
| GET | `/api/rewards/users/{userId}/contributions` | Get contribution summary | ENGINEER |

### Badge APIs

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/rewards/users/{userId}/badges` | Get user badges | ENGINEER |
| GET | `/api/rewards/badges` | Get all badges | ENGINEER |
| POST | `/api/rewards/badges` | Create badge | ADMIN |
| POST | `/api/rewards/badges/assign` | Assign badge | ADMIN |

### Leaderboard APIs

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/rewards/leaderboard` | Get leaderboard | ENGINEER |
| GET | `/api/rewards/top-contributors` | Get top contributors | ENGINEER |
| POST | `/api/rewards/leaderboard/generate` | Generate leaderboard | ADMIN |

### Statistics API

| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/rewards/statistics` | Get reward statistics | ENGINEER |

### Internal APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/internal/rewards/event` | Process reward event |
| GET | `/internal/rewards/users/{userId}` | Get user reward info |

## Kafka Topics

### Consumed Topics
- `ticket.resolved` - Ticket resolved events
- `solution.approved` - Solution approved events
- `knowledge.created` - Knowledge article created events
- `solution.voted` - Solution upvote events
- `knowledge.rated` - Knowledge article rated events

### Published Topics
- `reward.points.added` - Points added notification
- `reward.badge.awarded` - Badge awarded notification
- `leaderboard.updated` - Leaderboard updated notification

## Point Rules (Configurable)

| Event | Points |
|-------|--------|
| Ticket Resolved | 50 |
| Solution Approved | 30 |
| Knowledge Created | 20 |
| Upvote Received | 5 |

## Testing

```bash
# Run tests
./mvnw test

# Run tests with coverage report
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## Project Structure

```
src/main/java/com/cognizant/Reward_service/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── domain/           # JPA entities
├── dto/
│   ├── event/        # Kafka event DTOs
│   ├── request/      # Request DTOs
│   └── response/     # Response DTOs
├── enums/            # Enumerations
├── exception/        # Custom exceptions & handlers
├── kafka/            # Kafka producers & consumers
├── mapper/           # MapStruct mappers
├── repository/       # JPA repositories
├── scheduler/        # Scheduled jobs
├── security/         # JWT security
└── service/
    └── impl/         # Service implementations
```

## License

This project is proprietary software.
