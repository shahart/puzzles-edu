package edu.generalpuzzle.solver;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolveRequestTest {

    @Test
    void normalizesProblemIdAndFiltersBlankDimensions() {
        SolveRequest request = new SolveRequest(" 2d_ascii ", List.of("4", "", "  ", "5"));

        assertEquals("2d_ascii", request.problemId());
        assertArrayEquals(new String[] {"2d_ascii", "4", "5"}, request.toArguments());
    }

    @Test
    void defensivelyCopiesDimensions() {
        List<String> dimensions = new ArrayList<>(List.of("4", "5"));
        SolveRequest request = new SolveRequest("2d_ascii", dimensions);
        dimensions.clear();

        assertEquals(List.of("4", "5"), request.dimensions());
    }

    @Test
    void rejectsBlankProblemId() {
        assertThrows(IllegalArgumentException.class, () -> new SolveRequest(" ", List.of()));
    }
}
