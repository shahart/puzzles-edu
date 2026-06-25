package edu.generalpuzzle.core

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.text.NumberFormat

@Service
class Puzzle2D {

    private val log = LoggerFactory.getLogger(Puzzle2D::class.java)

    var PIECES = 12

    var ROWS = 0
    var COLUMNS = 0

    var totalSolutions = 0
    var triedPieces = 0

    var row = 0
    var column = 0

    var piecesIndices = mutableListOf<Int>()
    var solution = arrayOfNulls<Int>(PIECES)
    var pieces = arrayOfNulls<Piece>(PIECES)

    lateinit var names: String
    lateinit var grid: Array<IntArray>

    lateinit var currPiece: Piece

    private var totalFillInGrid = 0
    private var availInGrid = 0

    fun set(rows: Int, columns: Int) {
        piecesIndices = mutableListOf()
        solution = arrayOfNulls(PIECES)
        totalSolutions = 0
        triedPieces = 0

        ROWS = rows
        COLUMNS = columns

        grid = Array(ROWS) { IntArray(COLUMNS) }

        val allPieces = arrayOf(
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

        val rotations = intArrayOf(4, 4, 2, 1, 4, 4, 4, 4, 2, 4, 4, 2)
        val symmetric = intArrayOf(2, 1, 1, 1, 2, 2, 1, 2, 2, 1, 1, 1)
        names = "LUFXYNWPZVTI"

        if (ROWS == COLUMNS && ROWS == 8) {
            rotations[2] = 1
        }

        if (allPieces.size > PIECES) {
            log.warn("Id {}_{}. Warning- using first pieces from the whole set", ROWS, COLUMNS)
        }

        if (ROWS == 0 && COLUMNS == 0) {
            throw IllegalStateException("not supported yet")
        } else {
            for (i in 0 until PIECES) {
                piecesIndices.add(i)
                pieces[i] = Piece(i, allPieces[i], rotations[i], symmetric[i], names[i])
            }
            if (ROWS == COLUMNS && ROWS == 8) {
                grid[0][0] = -1
                grid[7][0] = -1
                grid[0][7] = -1
                grid[7][7] = -1
            }
            while (grid[0][column] == -1) {
                column++
            }
        }

        totalFillInGrid = ROWS * COLUMNS
        for (ints in grid) {
            for (anInt in ints) {
                if (anInt == -1) {
                    totalFillInGrid--
                }
            }
        }

        availInGrid = totalFillInGrid
        log.info("Found {} rows, {} cols, with total of cells {}", ROWS, COLUMNS, availInGrid)
        showGrid()
    }

    fun showGrid() {
        println("[msec] showGrid. Tried Pieces $triedPieces leftPieces ${piecesIndices.size}")
        for (i in 0 until ROWS) {
            for (j in 0 until COLUMNS) {
                if (grid[i][j] == -1) {
                    print("*  ")
                } else if (grid[i][j] == 0) {
                    print("-  ")
                } else {
                    print("${names[grid[i][j] - 1]} ")
                }
            }
            println()
        }
    }

    fun showPieces() {
        var line = ""
        for (i in 0 until PIECES - piecesIndices.size) {
            line += "${names[solution[i]!!]} "
        }
        log.info(line)
    }

    fun put() {
        val leftPieces = piecesIndices.size

        if (leftPieces == 0) {
            totalSolutions++
            log.info("totalSolutions {}", totalSolutions)
            if (totalSolutions == 1) {
                log.info("Found a solution")
                showGrid()
                showPieces()
            }
        }

        if (totalSolutions >= 1) return

        val rowsSet = IntArray(5)
        val columnsSet = IntArray(5)

        for (i in 0 until leftPieces) {
            val piece = piecesIndices[i]
            currPiece = pieces[piece]!!
            for (r in pieces[piece]!!.getAvailRotations() downTo 1) {
                if (canPut(rowsSet, columnsSet)) {
                    piecesIndices.removeAt(i)
                    solution[PIECES - leftPieces] = piece
                    putCurrPiece(rowsSet, columnsSet)

                    if (triedPieces % 50_000 == 0) {
                        showGrid()
                        showPieces()

                        if (Thread.currentThread().isInterrupted) {
                            log.warn("Id {}_{}. Signaled timed out! totalSolutions {}", ROWS, COLUMNS, totalSolutions)
                            return
                        }
                    }

                    put()
                    removeLast(piece, rowsSet, columnsSet)
                    piecesIndices.add(i, piece)
                }
                currPiece.rotate()
            }
        }
    }

    private fun putCurrPiece(rowsSet: IntArray, columnsSet: IntArray) {
        currPiece.setPosition(row, column)

        val currIndex = currPiece.index + 1
        for (i in 0 until currPiece.totalThisFill) {
            grid[rowsSet[i]][columnsSet[i]] = currIndex
        }

        goForward()
        availInGrid -= currPiece.totalThisFill
    }

    private fun goForward() {
        while (row < ROWS) {
            while (column < COLUMNS) {
                if (grid[row][column] == 0) return
                column++
            }
            column = 0
            row++
        }
    }

    private fun removeLast(piece: Int, rowsSet: IntArray, columnsSet: IntArray) {
        currPiece = pieces[piece]!!

        row = currPiece.getRow()
        column = currPiece.getColumn()

        for (i in 0 until currPiece.totalThisFill) {
            grid[rowsSet[i]][columnsSet[i]] = 0
        }

        availInGrid += currPiece.totalThisFill
    }

    fun canPut(rowsSet: IntArray, columnsSet: IntArray): Boolean {
        try {
            var setSoFar = 0
            val j = currPiece.getFirstSquarePos()
            val columnjj = column - j
            for (i in 0 until currPiece.totalThisFill) {
                val rowi = row + currPiece.getRowSet(setSoFar)
                val columnj = columnjj + currPiece.getColumnSet(setSoFar)
                if (grid[rowi][columnj] != 0) {
                    return false
                } else {
                    rowsSet[setSoFar] = rowi
                    columnsSet[setSoFar] = columnj
                    setSoFar++
                }
            }
        } catch (_: ArrayIndexOutOfBoundsException) {
            return false
        }

        triedPieces++
        return true
    }

    fun solve(): Int {
        val start = System.currentTimeMillis()

        if (totalFillInGrid != Piece.totalFill) {
            log.error("Id {}_{}. Invalid config, grid {} pieces {}", ROWS, COLUMNS, totalFillInGrid, Piece.totalFill)
        } else {
            Piece.totalFill = 0
            log.info("Starting rows {} cols {}", ROWS, COLUMNS)
            put()
        }

        val elapsedTime = (System.currentTimeMillis() - start) / 1000
        val nf = NumberFormat.getInstance()

        log.info("tried {} pieces", nf.format(triedPieces))
        if (elapsedTime > 0) {
            log.info("at {} pieces per sec", nf.format(triedPieces / elapsedTime))
        }
        log.info("number of solutions {}", totalSolutions * if (ROWS == COLUMNS) 8 else 4)

        return totalSolutions
    }
}
