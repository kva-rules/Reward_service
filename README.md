# Reward Service

Gamification microservice — points, badges, and leaderboards. Consumes domain events from `ticket-service`, `solution-service`, and `knowledge-service`, and awards points per **configurable rules** (see `app.reward.rules` in yaml). Exposes REST for the frontend leaderboard / user-statistics / badge catalog.

---

## At a glance
| | |
|---|---|
| **Port** | 8086 |
| **Database** | postgres-reward (`reward_db`) |
| **Kafka topics (in)** | `ticket.resolved`, `solution.approved`, `solution.voted`, `knowledge.created`, `knowledge.rated` |
| **Kafka topics (out)** | `reward.points.added`, `reward.badge.awarded`, `leaderboard.updated` |
| **Swagger UI (direct)** | http://localhost:8086/swagger-ui.html |
| **Swagger UI (via gateway)** | http://localhost:8080/swagger-ui.html?urls.primaryName=reward-service |
| **OpenAPI JSON** | http://localhost:8086/v3/api-docs |
| **Java** | 21 (Temurin) |
| **Spring Boot** | 3.2.4 |

---

## What it does
- Listens for lifecycle events from 3 upstream services
- **Awards points** per configurable rule
- **Unlocks badges** when cumulative point thresholds cross
- **Computes leaderboard** (global + per-period) and republishes it on every change
- Exposes read APIs for the frontend dashboard

---

## Reward rules (`app.reward.rules` in `application.yaml`)
| Event | Points |
|---|---|
| `ticket-resolved` | 50 |
| `solution-approved` | 30 |
| `knowledge-created` | 20 |
| `upvote-received` | 5 |

Change these without a redeploy by setting env vars:
```bash
APP_REWARD_RULES_TICKET_RESOLVED=100 ./services.sh restart reward-service
```

---

## Kafka topic mapping (`app.kafka.topics` in yaml)
| Logical event | Topic |
|---|---|
| Ticket resolved | `ticket.resolved` |
| Solution approved | `solution.approved` |
| Knowledge created | `knowledge.created` |
| Solution voted | `solution.voted` |
| Knowledge rated | `knowledge.rated` |
| Reward points added (out) | `reward.points.added` |
| Reward badge awarded (out) | `reward.badge.awarded` |
| Leaderboard updated (out) | `leaderboard.updated` |

---

## API surface

### Rewards (`/api/rewards/**`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| GET | `/api/rewards` | JWT | Current user's point history |
| GET | `/api/rewards/by-user/{userId}` | JWT | Admin / manager lookup |
| POST | `/api/rewards` | JWT + ADMIN | Manual point grant |
| GET | `/api/rewards/{id}` | JWT | Single reward record |

### Badges (`/api/badges/**`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| GET | `/api/badges` | JWT | Badge catalog |
| GET | `/api/badges/by-user/{userId}` | JWT | Badges unlocked by a user |
| POST | `/api/badges` | JWT + ADMIN | Create badge definition |
| PUT | `/api/badges/{id}` | JWT + ADMIN | Edit badge |

### Leaderboard (`/api/leaderboard/**`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| GET | `/api/leaderboard` | JWT | Global top-N |
| GET | `/api/leaderboard/by-period?days=30` | JWT | Rolling-window leaderboard |
| GET | `/api/leaderboard/rank/{userId}` | JWT | A single user's rank |

### Statistics (`/api/statistics/**`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| GET | `/api/statistics` | JWT | Aggregate counts for dashboards |

### Internal (`/internal/**`) — service-to-service
| Method | Path | Purpose |
|---|---|---|
| POST | `/internal/rewards` | Grant points (used if not event-driven) |
| POST | `/internal/rewards/recalculate/{userId}` | Recompute a user's total |

Live: **http://localhost:8086/swagger-ui.html**.

---

## Configuration
| Env var | Yaml key | Default | Purpose |
|---|---|---|---|
| `SERVER_PORT` | `server.port` | `8086` | |
| `SPRING_DATASOURCE_URL` | | `jdbc:postgresql://postgres-reward:5432/reward_db` | |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | | `kafka:9092` | |
| `JWT_SECRET` | `jwt.secret` | (shared) | |
| `APP_REWARD_RULES_*` | `app.reward.rules.*` | see above | Overridable scoring |

---

## Build & run
```bash
./services.sh start reward-service
```

## Docker / K8s
- Manifest: `k8s/reward-service.yaml`
- Service: `reward-service`

---

## Troubleshooting

**No points awarded after `ticket.resolved`**
1. Confirm Kafka is up: `docker ps | grep kafka`.
2. Tail reward logs: `./services.sh logs reward-service`. Look for `"Consumed event"` lines.
3. If you see `UnknownTopicOrPartitionException`, restart Kafka or restart the producing service to auto-create the topic.
4. Verify consumer-group offset with `docker exec kafka kafka-consumer-groups --bootstrap-server kafka:9092 --describe --group reward-service-group`.

**No points awarded after `solution.approved` (May 2026 fix)**
Root cause 1: The global `spring.json.value.default.type: RewardEventDTO` (in `application-local.yaml`) forced ALL Kafka messages to deserialise as `RewardEventDTO`. Spring then tried to convert the instance to `SolutionApprovedEvent` (the common-library class used as the listener parameter) — this conversion always failed.

Root cause 2: `SolutionEvent.timestamp` is a `LocalDateTime`, which Jackson serialises as a JSON array `[2026, 5, 3, 12, 0]`. Attempting to deserialise that array into `RewardEventDTO.timestamp` (type `Long`) threw a `JsonMappingException`.

Fix applied:
- `RewardEventDTO` now has `@JsonIgnoreProperties(ignoreUnknown = true)` and `@JsonIgnore` on `timestamp`.
- A local `SolutionApprovalEvent` DTO was created (matching the `contributors: List<UUID>` field the solution service actually publishes).
- `KafkaConfig` has a dedicated `solutionApprovedContainerFactory` bean with `USE_TYPE_INFO_HEADERS: false` and `VALUE_DEFAULT_TYPE: SolutionApprovalEvent.class.getName()`.
- `consumeSolutionApproved` in `RewardEventConsumer` uses `containerFactory = "solutionApprovedContainerFactory"` and accepts a `SolutionApprovalEvent` parameter.

**Leaderboard stale**
Aggregation runs synchronously on each reward event. Check `leaderboard.updated` is being published (consumer-group lag should be 0).

**Double-credit when retrying events**
Reward-service is idempotent per `(userId, eventType, sourceId)`. Duplicate events are no-ops, but verify by inspecting `rewards.source_event_id` uniqueness.

---

## Tech stack
- Java 21 (Temurin)
- Spring Boot 3.2.4
- Spring Kafka (consumer + producer)
- Spring Data JPA + PostgreSQL 16
- Spring Security + JJWT
- springdoc-openapi 2.6.0
- Lombok 1.18.34
- `com.kva:common-library` 1.0.0
