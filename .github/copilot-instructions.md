# GitHub Copilot / AI Agent Instructions for puzzles-edu

Purpose: help an AI coding agent become productive quickly in this repository.

- **Big picture**: This is a Java-based puzzle solver project (Spring Boot). Core solver code lives under `src/main/java` (package `edu.generalpuzzle.*`). Puzzles and puzzle definitions are expressed as Beanshell scripts in `config/` (files like `2d_ascii_grid.bsh`, `2d_ascii_parts.bsh`) and consumed at runtime by the solver.
- **Build systems**: The repo contains both Gradle and Maven builds. Primary Gradle configuration is `build.gradle` (Spring Boot 3.5.4, Java 21). A `pom.xml` exists (packaging=`war`) — be careful: some tasks expect Gradle (CI uses Gradle). Prefer `./gradlew` for day-to-day tasks unless the change explicitly targets Maven.

- **How to build / run / test**
  - Build (Gradle): `./gradlew build` (on Windows in bash shell use `./gradlew`); CI uses `./gradlew` as well.
  - Run server (Gradle): `./gradlew bootRun` — Spring Boot app listens on `localhost:8080` by default.
  - Run server (Maven): `./mvnw spring-boot:run` (only when testing maven-specific changes).
  - Run tests: `./gradlew test` or `./mvnw test` (project uses JUnit Platform / Spring Boot test starter).
  - Quick API smoke test: `curl http://localhost:8080/solve/2d_ascii` (exists in README as a simple check).

- **Runtime / platform details**
  - Java version: 21 (see `build.gradle` sourceCompatibility and `pom.xml` settings).
  - BeanShell (`bsh`) and Groovy are used for dynamic puzzle scripts (`build.gradle` has `bsh` and `groovy` dependencies). When changing how puzzle scripts are parsed/executed, check `config/` and `src/main/java` usage first.
  - Packaging: Gradle `jar` manifest sets `Main-Class: edu.generalpuzzle.main.JMain`. `pom.xml` historically produces a `war` artifact — don't assume both produce identical artifacts.

- **Key directories & files (where to look first)**
  - `src/main/java` — core solver, web controllers, app entrypoints.
  - `config/` — puzzle definitions (Beanshell `.bsh` files), validation scripts like `validateBsh.sh` / `validateBsh.bat`.
  - `build.gradle` and `pom.xml` — build configuration; prefer Gradle for CI-aligned changes.
  - `README.md` — contains quick run examples and notes.
  - `docs/` and `docs/*.html` — static site and solver views; helpful for expected output/format.
  - `target/`, `build/`, `tmp/` — generated outputs, test reports, and sample HTML results.
  - `.github/workflows/gradle.yml` — CI configuration for builds; review before changing build logic.

- **Project-specific conventions & patterns**
  - Puzzle definitions are code (Beanshell scripts) rather than JSON/YAML. Treat `.bsh` files as first-class source artifacts — changes to parsing/execution can have broad impact.
  - Some utility files (e.g. `myPzl.properties`, `myPzl.txt`) are used as input/config samples; tests and small demos use them.
  - Outputs often include small HTML/X3D snippets under repo root and `target/` — useful for quick visual verification.

- **Integration points & external dependencies**
  - Beanshell (`org.beanshell:bsh`) and Groovy are runtime dependencies for dynamic scripts.
  - Logging uses Log4j2 adapters for legacy 1.2 API (`log4j-1.2-api`, `log4j-core`).
  - There are commented Launch4j settings in `build.gradle` (Windows EXE packaging) — only enable after verifying JDK path and CI expectations.

- **When editing code**
  - If a change affects how `.bsh` puzzle scripts are interpreted, update at least one example file in `config/` and run the server locally to exercise the API endpoint (see `curl` example).
  - Keep the Java target at 21 unless the change requires a different JDK — CI and Gradle are configured for Java 21.
  - Prefer incremental, small changes and run `./gradlew test` locally. Check `build/reports/tests` or `target/surefire-reports` for failures.

- **CI & deployment notes**
  - CI uses `.github/workflows/gradle.yml` and GitHub Pages workflow for static docs. Don't change workflows without running builds locally first.

- **Example quick tasks for an agent**
  - To run the app and validate a solver route:
    - `./gradlew bootRun` then `curl http://localhost:8080/solve/2d_ascii`.
  - To run unit tests: `./gradlew test` and inspect `build/test-results` / `build/reports/tests`.
  - To produce an executable jar: `./gradlew jar` (manifest Main-Class already set in `build.gradle`).

If any section is unclear or you'd like more examples (for example: key controller classes to inspect, or a short list of critical unit tests), tell me which area to expand and I'll iterate.

**Where to start**
- `edu.generalpuzzle.main.Main` - headless command-line entry; core orchestration and `Main.mainGo(String[])` is called by the web controller. File: `src/main/java/edu/generalpuzzle/main/Main.java`.
- `edu.generalpuzzle.main.JMain` - legacy Swing GUI launcher and the class referenced in the Gradle `jar` manifest. File: `src/main/java/edu/generalpuzzle/main/JMain.java`.
- `edu.generalpuzzle.Controller` - Spring REST controller that exposes `GET /solve/{problemId}/{dimensions}` and delegates to `Main.mainGo`. File: `src/main/java/edu/generalpuzzle/Controller.java`.
- `config/` - puzzle definitions. Each puzzle uses a pair/triple of scripts: `<id>_grid.bsh`, `<id>_parts.bsh`, optionally `<id>_matches.bsh`. Example: `config/2d_ascii_grid.bsh` and `config/2d_ascii_parts.bsh`.
- `edu.generalpuzzle.infra` and `edu.generalpuzzle.infra.engines` - core solver interfaces and engine implementations (e.g., `DlxEngineStrategy`, `TrivialRecursiveEngineStrategy`, `TrivialIterativeEngineStrategy`). Inspect `src/main/java/edu/generalpuzzle/infra`.
- `edu.generalpuzzle.infra` and `edu.generalpuzzle.infra.engines` - core solver interfaces and engine implementations. Quick engine pointers:
  - `edu.generalpuzzle.infra.engines.EngineStrategy` — base engine behaviour and global flags. File: `src/main/java/edu/generalpuzzle/infra/engines/EngineStrategy.java`.
  - `edu.generalpuzzle.infra.engines.ParallelEngineStrategy` — parallelization wrapper used by DLX and other parallel engines. File: `src/main/java/edu/generalpuzzle/infra/engines/ParallelEngineStrategy.java`.
  - `edu.generalpuzzle.infra.engines.dlx_hadoop.DlxEngineStrategy` — DLX (dancing links) based exact-cover implementation; used when `EngineStrategy.ENGINE_TYPE == ENGINE_TYPE_DLX`. File: `src/main/java/edu/generalpuzzle/infra/engines/dlx_hadoop/DlxEngineStrategy.java`.
  - `edu.generalpuzzle.infra.engines.trivial.TrivialRecursiveEngineStrategy` — simple recursive backtracker. File: `src/main/java/edu/generalpuzzle/infra/engines/trivial/TrivialRecursiveEngineStrategy.java`.
  - `edu.generalpuzzle.infra.engines.trivial.TrivialIterativeEngineStrategy` — iterative/backtracking variant. File: `src/main/java/edu/generalpuzzle/infra/engines/trivial/TrivialIterativeEngineStrategy.java`.
  - Other helpful infra types: `Parts`, `IGrid`, `IPart`, `Shared`, and `GraphIt` — see `src/main/java/edu/generalpuzzle/infra` for interfaces and implementations.
- `myPzl.properties` and `results.properties` - local runtime configuration and persisted results used by `Main`.

