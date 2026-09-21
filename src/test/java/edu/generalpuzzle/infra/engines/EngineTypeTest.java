package edu.generalpuzzle.infra.engines;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EngineTypeTest {

    @Test
    void mapsAllLegacyEngineIds() {
        assertEquals(EngineType.DLX, EngineType.fromLegacyId(0));
        assertEquals(EngineType.RECURSIVE, EngineType.fromLegacyId(1));
        assertEquals(EngineType.ITERATIVE, EngineType.fromLegacyId(2));
    }

    @Test
    void rejectsUnknownLegacyEngineId() {
        assertThrows(IllegalArgumentException.class, () -> EngineType.fromLegacyId(99));
    }
}
