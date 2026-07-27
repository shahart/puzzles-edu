package edu.generalpuzzle.core

class Puzzle3D(
    private val rows: Int,
    private val columns: Int,
    private val depth: Int
) {
    data class Placement(val piece: Int, val cells: IntArray)

    val grid = IntArray(rows * columns * depth)
    var totalSolutions = 0
        private set
    var triedPieces = 0
        private set

    private val cellCount = rows * columns * depth
    private val placements = mutableListOf<Placement>()
    private val orientations = PENTOMINOES.map(::buildOrientations)

    init {
        require(rows > 0 && columns > 0 && depth > 0) {
            "All dimensions must be positive integers"
        }
        buildPlacements()
    }

    fun solve(): Int {
        totalSolutions = 0
        triedPieces = 0
        grid.fill(0)

        if (cellCount != PIECES * CELLS_PER_PIECE) {
            return 0
        }

        val exactCoverRows = placements.map { placement ->
            placement.cells + (cellCount + placement.piece)
        }
        val result = ExactCoverCounter.findOne(cellCount + PIECES, exactCoverRows)
        totalSolutions = result.solutions
        triedPieces = result.triedRows

        for (rowIndex in result.selectedRows) {
            val placement = placements[rowIndex]
            for (cell in placement.cells) {
                grid[cell] = placement.piece + 1
            }
        }
        return if (totalSolutions > 0) 1 else 0
    }

    private fun buildPlacements() {
        if (cellCount != PIECES * CELLS_PER_PIECE) {
            return
        }

        orientations.forEachIndexed { piece, pieceOrientations ->
            for (orientation in pieceOrientations) {
                val maxRow = orientation.maxOf { it[0] }
                val maxColumn = orientation.maxOf { it[1] }
                val maxDepth = orientation.maxOf { it[2] }

                for (rowOffset in 0 until rows - maxRow) {
                    for (columnOffset in 0 until columns - maxColumn) {
                        for (depthOffset in 0 until depth - maxDepth) {
                            val cells = orientation.map { cube ->
                                val placedRow = rowOffset + cube[0]
                                val placedColumn = columnOffset + cube[1]
                                val placedDepth = depthOffset + cube[2]
                                (placedRow * columns + placedColumn) * depth + placedDepth
                            }.toIntArray()
                            placements += Placement(piece, cells)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val PIECES = 12
        private const val CELLS_PER_PIECE = 5

        private val PENTOMINOES = listOf(
            arrayOf(intArrayOf(1), intArrayOf(1), intArrayOf(1), intArrayOf(1, 1)),
            arrayOf(intArrayOf(1, 1), intArrayOf(1), intArrayOf(1, 1)),
            arrayOf(intArrayOf(0, 1, 1), intArrayOf(1, 1), intArrayOf(0, 1)),
            arrayOf(intArrayOf(0, 1), intArrayOf(1, 1, 1), intArrayOf(0, 1)),
            arrayOf(intArrayOf(1, 1, 1, 1), intArrayOf(0, 0, 1)),
            arrayOf(intArrayOf(0, 1), intArrayOf(1, 1), intArrayOf(1), intArrayOf(1)),
            arrayOf(intArrayOf(0, 0, 1), intArrayOf(0, 1, 1), intArrayOf(1, 1)),
            arrayOf(intArrayOf(1), intArrayOf(1, 1), intArrayOf(1, 1)),
            arrayOf(intArrayOf(0, 0, 1), intArrayOf(1, 1, 1), intArrayOf(1)),
            arrayOf(intArrayOf(0, 0, 1), intArrayOf(0, 0, 1), intArrayOf(1, 1, 1)),
            arrayOf(intArrayOf(0, 0, 1), intArrayOf(1, 1, 1), intArrayOf(0, 0, 1)),
            arrayOf(intArrayOf(1, 1, 1, 1, 1))
        )

        private val PERMUTATIONS = listOf(
            intArrayOf(0, 1, 2),
            intArrayOf(0, 2, 1),
            intArrayOf(1, 0, 2),
            intArrayOf(1, 2, 0),
            intArrayOf(2, 0, 1),
            intArrayOf(2, 1, 0)
        )

        private fun buildOrientations(layout: Array<IntArray>): List<Array<IntArray>> {
            val cubes = mutableListOf<IntArray>()
            layout.forEachIndexed { row, line ->
                line.forEachIndexed { column, value ->
                    if (value == 1) {
                        cubes += intArrayOf(row, column, 0)
                    }
                }
            }

            val orientations = mutableListOf<Array<IntArray>>()
            val seen = mutableSetOf<String>()
            for (permutation in PERMUTATIONS) {
                val permutationSign = permutationSign(permutation)
                for (firstSign in intArrayOf(-1, 1)) {
                    for (secondSign in intArrayOf(-1, 1)) {
                        for (thirdSign in intArrayOf(-1, 1)) {
                            val signs = intArrayOf(firstSign, secondSign, thirdSign)
                            if (permutationSign * signs.reduce(Int::times) != 1) {
                                continue
                            }
                            val transformed = cubes.map { cube ->
                                IntArray(3) { axis -> signs[axis] * cube[permutation[axis]] }
                            }.toTypedArray()
                            normalizeAndSort(transformed)
                            val key = transformed.joinToString(";") { it.joinToString(",") }
                            if (seen.add(key)) {
                                orientations += transformed
                            }
                        }
                    }
                }
            }
            return orientations
        }

        private fun permutationSign(permutation: IntArray): Int {
            var inversions = 0
            for (first in permutation.indices) {
                for (second in first + 1 until permutation.size) {
                    if (permutation[first] > permutation[second]) {
                        inversions++
                    }
                }
            }
            return if (inversions % 2 == 0) 1 else -1
        }

        private fun normalizeAndSort(cubes: Array<IntArray>) {
            val minimum = IntArray(3) { Int.MAX_VALUE }
            for (cube in cubes) {
                for (axis in 0..2) {
                    minimum[axis] = minOf(minimum[axis], cube[axis])
                }
            }
            for (cube in cubes) {
                for (axis in 0..2) {
                    cube[axis] -= minimum[axis]
                }
            }
            cubes.sortWith(compareBy<IntArray>({ it[0] }, { it[1] }, { it[2] }))
        }
    }
}
