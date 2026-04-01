# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Java-based puzzle solver framework (Spring Boot) implementing backtracking and Dancing Links (DLX) algorithms for solving combinatorial puzzles on arbitrary structured lattices. Based on an M.Sc. thesis (2010).

## Build & Run Commands

Prefer Gradle for all day-to-day tasks — CI uses Gradle.

```bash
./gradlew build          # Build
./gradlew bootRun        # Run Spring Boot server on localhost:8080
./gradlew test           # Run tests
./gradlew jar            # Produce executable JAR (Main-Class: edu.generalpuzzle.main.JMain)

./mvnw spring-boot:run   # Maven alternative (only when testing Maven-specific changes)
./mvnw test
```

**Quick smoke test:** `curl http://localhost:8080/solve/2d_ascii`

Test reports: `build/reports/tests/` (Gradle) or `target/surefire-reports/` (Maven).

## Architecture

**Request flow:**
```
GET /solve/{problemId}/{dimensions}
  → Controller.solve()
  → Main.mainGo(String[])
  → Load config/{problemId}_grid.bsh + config/{problemId}_parts.bsh via BeanShell
  → Create IGrid + IPart instances
  → Run selected EngineStrategy
  → Return HTML/text response
```

**Core abstractions** (`src/main/java/edu/generalpuzzle/infra/`):
- `IGrid` — puzzle board represented as a graph
- `IPart` — puzzle piece represented as a subgraph
- `ICellPart` — individual cell in a part/grid
- `Parts` — collection of pieces with rotation management
- `Shared` — shared state for parallel engines

**Engine strategies** (`infra/engines/`):
- `EngineStrategy` — base class with global configuration flags (`ENGINE_TYPE`, `ST_HEURISTIC`, etc.)
- `DlxEngineStrategy` — Knuth's Dancing Links exact-cover solver
- `TrivialRecursiveEngineStrategy` / `TrivialIterativeEngineStrategy` — backtracking variants
- `ParallelEngineStrategy` — parallelization wrapper

**Puzzle definitions** (`config/`): Each puzzle uses a pair of BeanShell scripts: `{id}_grid.bsh` (board shape) and `{id}_parts.bsh` (pieces), with an optional `{id}_matches.bsh`. These are runtime-loaded first-class source artifacts — changes to parsing/execution have broad impact.

**Concrete examples** (`infra/examples/`): Implementations for 2D grids, 3D cubes, Tangram, Hexagonal, Sphere pyramids, etc.

## Runtime Details

- Java 21 (do not change without updating both `build.gradle` and `pom.xml`)
- BeanShell (`org.beanshell:bsh`) and Groovy are runtime dependencies for dynamic script execution
- Logging: Log4j2 with legacy 1.2 API adapters
- `myPzl.properties` and `results.properties` — local runtime config and persisted results used by `Main`
- Gradle `jar` manifest sets `Main-Class: edu.generalpuzzle.main.JMain`; `pom.xml` produces a `war` — artifacts differ

## Key Files

| File | Purpose |
|------|---------|
| `src/main/java/edu/generalpuzzle/main/Main.java` | Core orchestration, `mainGo()` entry |
| `src/main/java/edu/generalpuzzle/Controller.java` | REST endpoint |
| `src/main/java/edu/generalpuzzle/infra/engines/EngineStrategy.java` | Base engine + global flags |
| `src/main/java/edu/generalpuzzle/infra/engines/dlx_hadoop/DlxEngineStrategy.java` | DLX implementation |
| `config/` | Puzzle definitions (BeanShell scripts) |
| `.github/workflows/gradle.yml` | CI — review before changing build logic |
