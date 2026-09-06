
# OpenCloud — Production Ready (DB + gRPC + Clean Architecture)

This is the **complete working rebuild** of https://github.com/jobkar-official/opencloud_app_service.git — replacing mock in-memory store with real DB, real REST + gRPC APIs, and production UI.

## What's fixed vs old repo
- **DB connected**: Postgres (prod) + H2 (dev) via Spring Data JPA. Entities: User, Server, Deployment, DeployVersion, EnvVar.
- **Icons working**: GitHub / GitLab / Bitbucket provider cards use RemixIcon SVG with real brand icons.
- **APIs working**: REST at /api/v1/* + gRPC control plane at :9090 for VPS agents.
- **Theme**: Roboto + JetBrains Mono, AWS-console inspired but modern, not boring. Clean cards, badges, real layout.
- **Clean Architecture**: domain / application / infrastructure / presentation layers, HLD in docs/HLD.md
- **Real flow**: Git push -> webhook -> pull -> build -> deploy via gRPC -> health check -> auto-rollback -> snapshot last 3

## Run
```bash
# dev (H2)
./gradlew bootRun

# prod (Postgres)
export DATABASE_URL=jdbc:postgresql://localhost:5432/opencloud
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export DB_DRIVER=org.postgresql.Driver
./gradlew bootRun
```
gRPC server starts on 9090 automatically.

## gRPC
proto in src/main/proto/deploy.proto
Server impl: infrastructure/grpc/DeployGrpcService
Agent flow: RegisterAgent -> Heartbeat (poll) -> PullDeployment -> ReportDeployStatus

## HLD
See docs/HLD.md for clean architecture diagram + deployment sequence.
