# PeerPay – Real-Time UPI Payment Platform

PeerPay is a production-grade, event-driven payment engine replicating a real-time UPI (Unified Payments Interface) transaction network using Java microservices. It is designed to handle high-throughput, concurrent transactions reliably by adhering to ACID transaction boundaries, distributed consensus, and idempotent event streams.

## 🏗️ Architecture & Component Design

```
                     ┌──────────────────┐
                     │    Client App    │
                     └────────┬─────────┘
                              │ HTTP
                              ▼
                     ┌──────────────────┐
                     │   API Gateway    │ (Spring Cloud Gateway)
                     │    (Port 8080)   │ (Auth, Rate Limiting, Log MDC Trace ID)
                     └─┬──────────────┬─┘
                       │              │
        ┌──────────────┘              └──────────────┐
        ▼ HTTP                                       ▼ HTTP
┌──────────────┐                             ┌──────────────┐
│ User Service │                             │Paymt Service │ (Idempotency Key validation,
│ (Port 8081)  │                             │ (Port 8082)  │  Optimistic Lock, Outbox DB)
└──────────────┘                             └──────┬───────┘
                                                    │
                                                    │ (Publish via Outbox Poller)
                                                    ▼
                                             ┌──────────────┐
                                             │ Apache Kafka │ (Message Broker)
                                             └─┬──────────┬─┘
                                               │          │
                       ┌───────────────────────┘          └───────────────────────┐
                       ▼                                                          ▼
               ┌──────────────┐                                           ┌──────────────┐
               │Ledger Service│ (Double-Entry Bookkeeping:                │ Notif Service│ (Fire & Forget
               │ (Port 8083)  │  Debit & Credit ledger logs)              │ (Port 8084)  │  SMS alerting)
               └──────────────┘                                           └──────────────┘
```

---

## 🛠️ Key Architectural Patterns

1. **Transactional Outbox Pattern**:
   Prevents the "dual-write" problem (where database updates succeed but message publishing fails). Payment initiations save the payment record and a corresponding event in the `payment_outbox` table in the *same* database transaction. An asynchronous scheduler (`OutboxPoller`) continuously scans the outbox table and dispatches them to Kafka, guaranteeing *at-least-once* delivery.

2. **Durable Idempotency Pattern**:
   Clients submit an `X-Idempotency-Key` header with each payment. The Payment Service checks Redis cache first, then checks a PostgreSQL `idempotency_records` table with a `UNIQUE` constraint. If a duplicate is detected, the cached response is returned directly without re-processing.

3. **Optimistic Concurrency Control**:
   Uses JPA `@Version` column checks on `Payment` updates. If a callback/webhook attempts to modify a transaction status simultaneously with a cancellation task, the second transaction fails on version mismatch, preventing race conditions.

4. **Double-Entry Ledger Architecture**:
   To prevent balance corruption, balances are never directly updated. Every completed payment triggers the creation of two ledger entries: a `DEBIT` entry for the payer and a `CREDIT` entry for the payee. Balances are dynamically computed as the sum of credits minus debits.

5. **Log Correlation (MDC Tracing)**:
   The API Gateway acts as the gateway entry point and generates a unique `X-Request-ID` (trace ID) if not present. It injects this ID into the logging MDC (Mapped Diagnostic Context) and passes it downstream via headers. Downstream services extract this header and inject it into their logging contexts, enabling unified log trace queries in Kibana.

---

## ⚙️ Prerequisites

- **Java**: JDK 21 or JDK 26
- **Maven**: 3.9+
- **Docker & Docker Compose**

---

## 🚀 Running the Platform

To make running and stopping the microservices suite as simple as possible, use the helper scripts:

### 1. Start all components
Run the startup script:
```bash
./run-all.sh
```
This script will:
- Check if Docker is running and spin up PostgreSQL, Redis, Kafka, Zookeeper, and the ELK stack.
- Build all modules using `mvn clean install`.
- Boot all 6 Spring Boot services in the background, redirecting their consoles to local logs:
  - `logs/api-gateway.log`
  - `logs/user-service.log`
  - `logs/payment-service.log`
  - `logs/ledger-service.log`
  - `logs/notification-service.log`
  - `logs/reconciliation-service.log`

### 2. Stop all components
To stop all services and gracefully clean up, run:
```bash
./stop-all.sh
```
This script terminates the background JVM instances and prompts you to tear down the Docker Compose infrastructure.

---

## 🧪 E2E Manual Testing

Use the companion [requests.http](requests.http) file. If you are using IntelliJ IDEA or VS Code with the "REST Client" extension, you can execute the requests directly.

Follow the step-by-step instructions in the [requests.http](requests.http) file:
1. **Register Payer & Payee** (User A and User B).
2. **Login** to obtain a JWT token for User A.
3. **Set variables** at the top of `requests.http` (`@jwtToken`, `@payerId`, `@payeeId`).
4. **Initiate Payment** with a unique `X-Idempotency-Key` header.
5. **Simulate completion/failure webhooks** using the simulated callback endpoints.
6. **Verify double-entry balance** updates for both accounts.
7. **Inspect audits** in the Reconciliation Service.
