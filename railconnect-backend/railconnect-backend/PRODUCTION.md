# Phase 10 — Production Features: what's automated vs. what you still need to do

This document lists the manual, one-time setup each Phase 10 feature still needs — none of it
can be done from inside the codebase itself.

## 1. Secrets — do this first
Every secret in `application.yml` currently has a **development-only default** baked in
(JWT secret, mail credentials, DB password). Before deploying anywhere real:
- [ ] Generate a new random JWT secret (256-bit hex) and set it via the `JWT_SECRET` env var.
- [ ] Set real `MAIL_USERNAME` / `MAIL_PASSWORD` (a Gmail **app password**, not your login password) via env vars.
- [ ] Set a real `DB_PASSWORD` via env var — never rely on the `12345678` default outside local dev.
- [ ] Put all of the above in your platform's secret manager (GitHub Actions secrets, AWS Secrets Manager, Docker secrets, etc.) — never commit them to a `.env` file that gets pushed.

## 2. Redis
The app now **requires** a reachable Redis instance to start (`spring.cache.type: redis`).
- Local dev: `docker compose up redis` (already wired in `docker-compose.yml`), or install Redis locally.
- Production: provision a managed Redis (AWS ElastiCache, Azure Cache for Redis, Redis Cloud, etc.) and point `REDIS_HOST`/`REDIS_PORT`/`REDIS_PASSWORD` at it.
- If you'd rather not run Redis at all, set `CACHE_TYPE=none` — caching is skipped entirely and everything else keeps working.

## 3. Rate Limiting
The limiter in `RateLimitingFilter` is **in-memory, per-instance**. If you run more than one
app instance behind a load balancer:
- [ ] Either accept that the effective limit becomes `requestsPerWindow × instanceCount`, or
- [ ] Replace the in-memory counter with Redis `INCR`/`EXPIRE` (the Redis connection is already there) so all instances share one counter — this is a real code change, not just config, if you need it.
- Tune `RATE_LIMIT_REQUESTS` / `RATE_LIMIT_WINDOW_SECONDS` env vars for your actual traffic before going live; the defaults (100 req/min) are a starting guess, not a measured number.

## 4. Docker
- [ ] `docker compose up --build` should bring up app + Postgres + Redis locally — actually run this once and confirm it works in your environment (I could not run Docker in this sandbox).
- [ ] Decide where the image gets hosted long-term (GHCR is wired in CI by default, see below) and update `docker-compose.yml`'s `build: .` to `image: <registry>/<image>:<tag>` if you want compose to pull instead of build in production.

## 5. CI/CD (GitHub Actions)
The workflow at `.github/workflows/ci.yml` needs no manual secrets for the build/test job.
For the `docker-publish` job (pushes an image to `ghcr.io` on every merge to `main`):
- [ ] Go to your GitHub repo → Settings → Actions → General → "Workflow permissions" → ensure **"Read and write permissions"** is selected (GHCR push needs this; it's off by default on some orgs).
- [ ] If you'd rather publish to Docker Hub instead of GHCR, swap the `docker/login-action` credentials for `DOCKERHUB_USERNAME`/`DOCKERHUB_TOKEN` repo secrets you'll need to add yourself.
- [ ] The workflow only triggers on `main` — if your default branch is named differently, update the `branches:` filters.

## 6. Logging
- [ ] Decide where `logs/railconnect.log` should actually live in production (the rolling file appender writes there by default) — typically you'd mount a volume or, more commonly in containerized setups, ship stdout (already logged to console too) to your log aggregator (CloudWatch, Datadog, ELK, Loki, etc.) instead of relying on the file at all.
- [ ] If you're running in Kubernetes/ECS/etc., the usual pattern is to disable the file appender entirely and let the platform capture stdout — that's a one-line removal in `logback-spring.xml` (drop the `FILE` appender-ref) whenever you're ready.

## 7. Monitoring
- `/actuator/prometheus` is exposed and ready to scrape, but **nothing is scraping it yet**.
- [ ] Stand up Prometheus (or point your existing one) at this endpoint, and Grafana (or similar) on top of it for dashboards — this is infra outside the app itself.
- [ ] `/actuator/health`, `/metrics`, and `/prometheus` are currently `permitAll` in `SecurityConfig` so Docker/k8s health checks work without a JWT. Before exposing this app publicly, consider putting actuator on a separate internal-only port (`management.server.port`) or fronting it with network policy/IP allowlisting so metrics aren't world-readable.

## 8. Health Check
- [ ] If deploying to Kubernetes, wire the Deployment's `livenessProbe`/`readinessProbe` to `GET /actuator/health/liveness` and `/actuator/health/readiness` respectively (both are already enabled).
- [ ] The custom `BookingSystemHealthIndicator` runs a real `count()` query — keep an eye on it if your bookings table ever gets huge, since a full count on an unindexed/huge table isn't free on every health check poll.

## 9. API Versioning
- No manual step to ship v1 — it's already how every controller is mounted.
- [ ] When you eventually need a v2 for a breaking change, the convention is: new controllers live under a parallel `/api/v2/...` path (see the comment in `ApiVersionHeaderFilter`) rather than editing v1 controllers in place. Decide then whether `api.version` becomes per-request-resolved (some clients on v1, some on v2) rather than the single global value it is today.

---
**Sandbox disclaimer:** none of the above (Docker build, Redis connectivity, GitHub Actions run, Prometheus scrape) was actually executed or verified in this environment — I don't have Docker, a running Redis, or network access to GitHub/Maven Central here. Please run `docker compose up --build` and a real `mvn verify` locally before trusting any of this in production.
