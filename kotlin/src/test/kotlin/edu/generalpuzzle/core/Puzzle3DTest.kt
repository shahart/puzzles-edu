package edu.generalpuzzle.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class Puzzle3DTest {

    @Test
    fun testHasSolution3_4_5() {
        val puzzle3D = Puzzle3D(3, 4, 5)

        assertEquals(1, puzzle3D.solve())
        assertEquals(1, puzzle3D.totalSolutions)

        val cubesPerPiece = IntArray(Puzzle3D.PIECES + 1)
        puzzle3D.grid.forEach { cell ->
            assertTrue(cell in 1..Puzzle3D.PIECES)
            cubesPerPiece[cell]++
        }
        for (piece in 1..Puzzle3D.PIECES) {
            assertEquals(5, cubesPerPiece[piece], "piece $piece")
        }
    }

    @Test
    fun testWrongVolumeHasNoSolution() {
        assertEquals(0, Puzzle3D(3, 2, 5).solve())
    }

    @Test
    fun testDimensionsMustBePositive() {
        assertThrows(IllegalArgumentException::class.java) {
            Puzzle3D(0, 4, 5)
        }
    }
}
