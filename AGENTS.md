# Repository Guidelines

## Project Structure & Module Organization
`src/main/java/edu/generalpuzzle/` contains the Spring Boot entrypoint, REST controller, solver core, and engine strategies. Put reusable solver abstractions in `infra/`, runtime entry classes in `main/`, and concrete puzzle implementations under `examples/`. Tests live in `src/test/java/` and generally mirror the production package structure. Runtime puzzle definitions are stored in `config/` as BeanShell pairs such as `2d_ascii_grid.bsh` and `2d_ascii_parts.bsh`. Static browser demos and generated solver pages are in `docs/`; thesis and reference material are in `doc/`.

## Build, Test, and Development Commands
Prefer Gradle for day-to-day work because GitHub Actions builds with it.

- `./gradlew build` or `gradlew.bat build`: compile, run tests, and assemble artifacts.
- `./gradlew test`: run the JUnit suite only.
- `./gradlew bootRun`: start the app on `localhost:8080`.
- `curl http://localhost:8080/solve/2d_ascii`: quick smoke test against the REST endpoint.
- `./mvnw test` or `./mvnw spring-boot:run`: Maven fallback when touching `pom.xml` or wrapper behavior.

## Coding Style & Naming Conventions
Use Java 21 and preserve the existing 4-space indentation style. Keep packages lowercase (`edu.generalpuzzle.infra.engines`), classes in `UpperCamelCase`, methods and fields in `lowerCamelCase`, and constants in `UPPER_SNAKE_CASE`. Match existing naming for puzzle definitions: `{id}_grid.bsh`, `{id}_parts.bsh`, and optional `{id}_matches.bsh`. No formatter is enforced in the repo, so keep changes small and consistent with surrounding code.

## Testing Guidelines
Tests use JUnit 5 through Spring Boot’s test starter. Add focused unit tests beside the affected package, with names ending in `Test` or `AcceptanceTest`. Cover both solver behavior and configuration loading when changing engine logic or `config/` scripts. Check reports in `build/reports/tests/` after Gradle runs.

## Commit & Pull Request Guidelines
Recent history favors short, imperative subjects such as `Update README.md. Fix 404` and `allow back on mobile`. Keep commit titles concise, describe observable behavior, and avoid bundling unrelated changes. PRs should include a short summary, the commands you ran (`./gradlew test`, smoke checks, or both), linked issues when applicable, and screenshots only for `docs/` or UI-facing changes.

## Configuration & Agent Notes
Do not change the Java version without updating both [build.gradle](/C:/repos/puzzles-edu/build.gradle) and [pom.xml](/C:/repos/puzzles-edu/pom.xml). Treat `config/` scripts as first-class source: a small script edit can affect every solver path that loads it.
