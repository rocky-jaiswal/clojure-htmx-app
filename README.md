# Clojure Web App with HTMX

## Stack - Integrant + Postgres + htmx + Alpine.js

A full-stack Clojure web application using Integrant, Reitit, Postgres (via Docker Compose), htmx, Alpine.js, and Tailwind CSS. The app runs locally against a Dockerised DB — no JVM inside Docker during development.

---

## Stack Overview

| Layer | Technology |
|---|---|
| Framework | Integrant + Reitit |
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
- Clojure CLI (`clojure` / `clj`) — install via [clojure.org](https://clojure.org/guides/install_clojure)
- Docker (database only — the app runs on the host JVM)

### First-time setup

Copy the env template and fill in values:
```bash
cp .env.example .env
```

Required variables:

| Variable | Purpose |
|----------|---------|
| `DATABASE_URL` | JDBC URL for the app DB |
| `SESSION_SECRET` | Cookie signing key — change in production |
| `PORT` | HTTP port (default `3000`) |
| `LOG_LEVEL` | Timbre log level: `debug`, `info`, `warn`, `error` (default `info`) |
| `DB_POOL_SIZE` | HikariCP max pool size (default `10`) |

### Starting the dev environment

**1. Start the database**
```bash
docker compose up db -d
```

**2. Export env vars** (source your `.env` or export manually)
```bash
export DATABASE_URL="jdbc:postgresql://localhost:5432/htmx_app?user=htmx_app&password=htmx_app"
export SESSION_SECRET="dev-secret-change-in-prod"
```

**3. Start the REPL**
```bash
make repl
# or: clj -M:dev -m nrepl.cmdline --middleware "[cider.nrepl/cider-middleware]"
```

**4. Start the system at the REPL prompt**
```clojure
(go)
```

App is now running at `http://localhost:3000`. Migrations run automatically on startup.

> **Important:** Always start the REPL from the terminal (`make repl` or `clj -M:dev`).
> Do not use Calva's Jack-In for this project — it fires before the namespace is ready and hangs.

### Code changes

```clojure
(reset)    ; reload changed namespaces and restart the system (~2-4s)
(halt)     ; stop the system
(go)       ; start the system
```

### Reset the database

```bash
docker compose down -v   # wipe the DB volume
docker compose up db -d  # fresh DB
```

Migrations re-run automatically on the next `(go)`.

---

## Commands Cheat Sheet

### Make targets

```bash
make repl             # start nREPL server (CIDER-compatible)
make test-unit        # run unit tests (no DB needed)
make test-integration # run integration tests (requires Docker DB on port 5433)
make test             # run all test suites
make lint             # clj-kondo static analysis
make fmt-check        # check formatting without modifying files
make fmt              # fix formatting in src/ and test/
make build            # build production uberjar
```

### Database

```bash
docker compose up db -d          # start dev DB (port 5432)
docker compose down -v           # wipe dev DB volume
docker compose -f docker-compose.test.yml up -d    # start integration test DB (port 5433)
docker compose -f docker-compose.test.yml down -v  # teardown test DB
```

### Tests (direct)

```bash
clj -M:test unit         # unit tests only — fast, no DB, uses with-redefs
clj -M:test integration  # integration tests — requires test DB on port 5433
clj -M:test              # all suites
```

### Build and run

```bash
clj -T:build uber                          # build target/app.jar
java -jar target/app.jar                   # run production uberjar
```

---

## Key Design Decisions

- **CSRF handled globally** — `wrap-anti-forgery` in the middleware stack; htmx sends the token via the JS snippet in `base.html`
- **Sessions in Postgres** — survives app restarts; no separate Redis needed for an experiment
- **Migrations on boot** — Migratus runs before Jetty starts; safe for single-instance deployments
- **Secrets via env vars only** — Aero reads `#env` tags; `.env` file is for local convenience, never committed
- **Uberjar = single artifact** — `java -jar app.jar` with env vars is the complete production runtime
