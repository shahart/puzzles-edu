package edu.generalpuzzle.core

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class Puzzle2DTest {

    @BeforeEach
    fun resetGlobals() {
        Piece.totalFill = 0
    }

    @Autowired
    private lateinit var puzzle2D: Puzzle2D

    @Test
    fun testHasSolution12_5() {
        puzzle2D.set(12, 5)
        Assertions.assertEquals(1, puzzle2D.solve())
    }

    @Test
    fun testHasSolution10_6() {
        puzzle2D.set(10, 6)
        Assertions.assertEquals(1, puzzle2D.solve())
    }

    @Test
    fun testNoSolution5_13() {
        puzzle2D.set(5, 13)
        Assertions.assertEquals(0, puzzle2D.solve())
    }
}
