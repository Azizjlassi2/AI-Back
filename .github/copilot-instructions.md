# AI Coding Agent Instructions for AI-Back (Spring Boot Backend)

## Architecture Overview

- **Domain-driven design:** Major domains include users (ClientAccount, DeveloperAccount), AI models, subscription plans, subscriptions, and payments.
- **Service boundaries:** Business logic is handled in service classes (e.g., `SubscriptionService`, `PaymentService`). Persistence is managed via JPA repositories.
- **Data flow:** Users subscribe to AI model plans; subscriptions are activated only after payment confirmation via Konnect gateway.
- **Entities:** Key entities are in `model/` folders. Relationships are explicit (e.g., Subscription references client, plan, developer, and payment).

## Developer Workflows

- **Build:** Use Maven (`mvnw clean install`) for builds. Artifacts are in `target/`.
- **Run:** Start the app with `mvnw spring-boot:run` or run the main class (`BackendApplication.java`).
- **Test:** Run tests with `mvnw test`. Integration tests are in `test/java/com/aiplus/backend/`.
- **Debug:** Use IDE or Spring Boot DevTools for hot reload and debugging.

## Project-Specific Patterns

- **DTOs:** Use DTOs for request/response payloads (e.g., `SubscriptionCreateDTO`).
- **REST Controllers:** Endpoints are in `controller/` folders. Example: `SubscriptionController` for subscription APIs, `PaymentController` for payment APIs.
- **Status-driven workflow:** Subscriptions start as `PENDING`, become `ACTIVE` after payment (`PaymentStatus.COMPLETED`).
- **Konnect Integration:** Payment initiation and webhook/callback handled in `PaymentService` and exposed via REST endpoints.
- **Entity relationships:** Subscription links to client, plan, developer, and payment. Payment links to subscription and user.

## Integration Points

- **Konnect Payment Gateway:** API key and base URL are configured in `application.properties`. Payment logic is stubbed for extension.
- **Spring Security:** User authentication is handled via Spring Security (see `User` entity and config).

## Key Files & Directories

- `src/main/java/com/aiplus/backend/subscription/controller/SubscriptionController.java`
- `src/main/java/com/aiplus/backend/payment/controller/PaymentController.java`
- `src/main/java/com/aiplus/backend/subscription/service/SubscriptionService.java`
- `src/main/java/com/aiplus/backend/payment/service/PaymentService.java`
- `src/main/resources/application.properties`
- `pom.xml`

## Conventions

- **Use Lombok** for boilerplate (`@Data`, `@Builder`, etc.).
- **Prefer constructor injection** for services (`@RequiredArgsConstructor`, `@AllArgsConstructor`).
- **Entity relationships** are explicit and mapped via JPA annotations.
- **Configuration** via environment variables and `application.properties`.

---

If any section is unclear or missing, please provide feedback for further refinement.
