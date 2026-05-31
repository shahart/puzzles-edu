# System Architecture

This repository is a single Java module, but the code is organized into clear internal layers:

- `edu.generalpuzzle` is the Spring Boot HTTP entrypoint.
- `edu.generalpuzzle.main` is the primary runtime/CLI orchestration layer.
- `edu.generalpuzzle.infra` holds the solver abstractions and shared engine state.
- `edu.generalpuzzle.infra.engines` contains the concrete solving strategies.
- `edu.generalpuzzle.examples` contains puzzle-specific grids, parts, and edge definitions.
- `edu.generalpuzzle.kickoff` is a standalone legacy polyomino backtracking prototype.

## Mermaid Diagram

```mermaid
flowchart TD
    %% External actors
    Client[HTTP client / CLI user]

    %% Spring Boot entry path
    subgraph Boot[Spring Boot entrypoint]
        App[Application]
        Ctrl[Controller]
    end

    %% Main runtime
    subgraph MainLayer[Runtime orchestration]
        MainPkg[edu.generalpuzzle.main.Main]
        JMain[JMain / CLI entrypoints]
        MainHelpers[Args, Tee, ShowSol*, Bedlam, PuzzleException, ShortFormatter]
    end

    %% Solver core
    subgraph Infra[Solver core]
        CoreTypes[IGrid, IPart, ICellPart, IEdge, CellId, Parts, Shared, Utils, GraphIt]
        EngineAPI[IEngineStrategy, EngineStrategy, ParallelEngineStrategy]
        EngineImpls[TrivialRecursive, TrivialIterative, DlxEngineStrategy, DancingLinksGeneric]
    end

    %% Example puzzles
    subgraph Examples[Puzzle implementations]
        Ex2D[examples.cube.dimension2]
        Ex3D[examples.cube.dimension3]
        ExPoly[examples.cube.dimension2.polysticks]
        ExHex[examples.hexPrism]
        ExPie[examples.pie]
        ExSphere[examples.spheresPyramid]
        ExTang[examples.tangram]
    end

    %% Legacy prototype
    subgraph Kickoff[Legacy prototype]
        KickoffPkg[kickoff.Puzzle2D / Piece]
    end

    %% External libraries
    subgraph External[External libraries]
        Spring[Spring Boot Web + Actuator]
        BeanShell[BeanShell]
        Log4j[Log4j 1.x / 2.x bridge]
        Groovy[Groovy build dependency]
    end

    Client --> App
    Client --> Ctrl
    App --> Spring
    Ctrl --> MainPkg
    Ctrl --> Spring

    JMain --> MainPkg
    MainPkg --> BeanShell
    MainPkg --> Log4j
    MainPkg --> CoreTypes
    MainPkg --> EngineAPI
    MainPkg --> EngineImpls
    MainPkg --> Ex2D
    MainPkg --> Ex3D

    EngineAPI --> CoreTypes
    EngineImpls --> EngineAPI
    EngineImpls --> CoreTypes
    EngineImpls --> Log4j

    Ex2D --> CoreTypes
    Ex3D --> CoreTypes
    ExPoly --> Ex2D
    ExPoly --> CoreTypes
    ExHex --> CoreTypes
    ExPie --> CoreTypes
    ExSphere --> CoreTypes
    ExTang --> CoreTypes

    %% Cross-coupling visible in the current codebase
    CoreTypes -.-> MainHelpers
    CoreTypes -.-> ExTang
    EngineImpls -.-> MainPkg

    %% Main helpers are part of the runtime package but do not define the core flow
    MainHelpers -.-> CoreTypes
    MainHelpers -.-> EngineAPI
```

## Dependency Notes

- `Controller` exposes `GET /solve/{problemId}` and delegates directly to `main.Main`.
- `main.Main` is the central orchestrator. It loads puzzle definitions from `config/*.bsh`, selects an engine, and runs the solver.
- `infra` is the shared solver layer. `Parts`, `IGrid`, `IPart`, and related types are the main abstractions used by every engine and puzzle implementation.
- `infra.engines` depends on `infra` and contains the real solver strategies:
  - `DlxEngineStrategy` implements the dancing-links solver.
  - `TrivialRecursiveEngineStrategy` and `TrivialIterativeEngineStrategy` implement backtracking variants.
- `examples` depends on `infra` and provides concrete puzzle layouts, edges, and part definitions.
- There are a few reverse or incidental dependencies worth noting:
  - `infra.IPart` imports `examples.tangram.CellPartTang`.
  - `infra.Parts` imports `main.PuzzleException`.
  - `TrivialIterativeEngineStrategy` imports `main.Main`.
- `kickoff` is effectively a separate proof-of-concept solver and is not part of the Spring Boot request path.

## Build-Level Dependencies

From `build.gradle` / `pom.xml`, the module currently depends on:

- Spring Boot Web
- Spring Boot Actuator
- Spring Boot Test
- BeanShell
- Log4j (`log4j-1.2-api`, `log4j-api`, `log4j-core`)
- Groovy
