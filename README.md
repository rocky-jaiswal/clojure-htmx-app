# Clojure Web App — Kit + Postgres + htmx + Alpine.js

A full-stack Clojure web application using Kit framework, Postgres (via Docker Compose), htmx, Alpine.js, and Tailwind CSS. Designed for a developer-friendly local experience with minimal setup beyond Docker and a JVM. in production the whole app can run with a single command.

---

## Stack Overview

| Layer | Technology |
|---|---|
| Framework | Kit (Integrant + Reitit) |
| Language | Clojure (JVM 21+) |
| Database | PostgreSQL 16 |
| Migrations | Migratus |
| DB Access | next.jdbc + HikariCP |
| Validation | Malli |
| Auth | Buddy (hashers + auth) |
| Sessions | Ring session + ring-jdbc-session |
| Templating | Hiccup (HTML as Clojure data structures) |
| Frontend | htmx + Alpine.js + Tailwind CSS (CDN in dev, compiled in prod) |
| Logging | Timbre (structured JSON in prod) |
| Config | Aero (EDN profiles) |
| Testing | clojure.test + kaocha |
| Build | tools.build (uberjar) |
| Dev tooling | Clojure CLI (deps.edn) |

---

## Dev Workflow

### Prerequisites

- JDK 21+ (`sudo apt install openjdk-21-jdk`)
- Clojure CLI (`clojure` / `clj`) — install via [linux-install.sh](https://clojure.org/guides/install_clojure)
- Docker (for the database only)

### Starting the dev environment

**1. Start the database**
```bash
docker compose up db -d
```

**2. Set environment variables (fish shell)**
```fish
set -x DATABASE_URL "jdbc:postgresql://localhost:5432/htmx_app?user=htmx_app&password=htmx_app"
set -x SESSION_SECRET "9c15b34faaca7b45fac8e4fdd24e80c9"
```

**3. Start the REPL**
```bash
clj -M:dev
```

**4. Start the system at the REPL prompt**
```clojure
(go)
```

App is now running at `http://localhost:3000`. Migrations run automatically on startup.

### Code changes

```clojure
(reset)   ; reload changed namespaces and restart the system (~2-4s)
(halt)    ; stop the system
(go)      ; start the system
```

### Reset the database

```bash
docker compose down -v   # wipe the DB volume
docker compose up db -d  # fresh DB
```

Migrations will re-run on the next `(go)`.

## Summary: Commands Cheat Sheet

```bash
# Dev
docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d      # Start
docker compose -f docker-compose.yml -f docker-compose.dev.yml down        # Stop
docker compose -f docker-compose.yml -f docker-compose.dev.yml logs -f app # Logs

# Test
docker compose -f docker-compose.test.yml up -d     # Start test DB
clojure -M:test -m kaocha.runner                    # Run tests
docker compose -f docker-compose.test.yml down      # Teardown

# Build
clojure -T:build uber                               # Build uberjar

# Production
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d     # Start prod
docker compose -f docker-compose.yml -f docker-compose.prod.yml down       # Stop prod
```

---

## Key Design Decisions

- **CSRF handled globally** — `wrap-anti-forgery` in the middleware stack; htmx sends the token via the JS snippet in `base.html`
- **Sessions in Postgres** — survives app restarts; no separate Redis needed for an experiment
- **Migrations on boot** — Migratus runs before Jetty starts; safe for single-instance deployments
- **Secrets via env vars only** — Aero reads `#env` tags; `.env` file is for local convenience, never committed
- **Uberjar = single artifact** — `java -jar app.jar` with env vars is the complete production runtime
