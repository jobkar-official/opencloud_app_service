
# OpenCloud HLD - Clean Architecture

## Layers
Presentation (Thymeleaf + REST ApiControllers + gRPC Controllers)
Application (DeployOrchestratorService, ServerConnectionService)
Domain (Entities: User, Server, Deployment, DeployVersion, EnvVar + Repositories)
Infrastructure (JPA Impl, gRPC Server, JSch SSH client, Instance Agent protocol)

## Flow (from PDF)
Push -> Webhook -> Agent pulls commit on VPS -> Build (npm install+build / mvn package) -> Build gate -> Deploy via PM2/jar exec -> Health gate -> Auto-rollback to last good -> Snapshot last 3 -> Logs streamed to dashboard

## DB Schema
users(id, email unique, password_hash, role, oauth_provider)
servers(id, name, host, ssh_port, status, owner_id, last_heartbeat)
deployments(id, name, provider[GH/GL/BB], repo_url, branch, build_type, server_id, owner_id, status, commit_sha)
env_vars(id, deployment_id, key, value, secret)
deploy_versions(id, deployment_id, commit_sha, artifact_path, status, created_at)

## gRPC vs REST
REST for dashboard human actions.
gRPC for agent<->control plane (low overhead, streaming logs).
