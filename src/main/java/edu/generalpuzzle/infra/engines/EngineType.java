package edu.generalpuzzle.infra.engines;

/** The available algorithms for solving a puzzle. */
public enum EngineType {
    DLX(0),
    RECURSIVE(1),
    ITERATIVE(2);

    private final int legacyId;

    EngineType(int legacyId) {
        this.legacyId = legacyId;
    }

    public int legacyId() {
        return legacyId;
    }

    public static EngineType fromLegacyId(int legacyId) {
        for (EngineType engineType : values()) {
            if (engineType.legacyId == legacyId) {
                return engineType;
            }
        }
        throw new IllegalArgumentException("Unknown engine type: " + legacyId);
    }
}
