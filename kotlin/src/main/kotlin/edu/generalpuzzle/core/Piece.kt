package edu.generalpuzzle.core

import org.slf4j.LoggerFactory

class Piece(
    val index: Int,
    layout: Array<IntArray>,
    availRotations: Int,
    symmetric: Int,
    val name: Char
) {
    companion object {
        @JvmField
        var totalFill = 0
    }

    private val log = LoggerFactory.getLogger(Piece::class.java)

    var totalThisFill = 0
        private set

    private val firstSquarePos: IntArray
    private val rowsSet: Array<IntArray?>
    private val columnsSet: Array<IntArray?>

    private var currRotation = 0

    fun getAvailRotations() = firstSquarePos.size
    fun getFirstSquarePos() = firstSquarePos[currRotation]
    fun getRowSet(i: Int) = rowsSet[currRotation]!![i]
    fun getColumnSet(i: Int) = columnsSet[currRotation]!![i]

    private var row = -1
    private var column = -1

    fun getRow() = row
    fun getColumn() = column

    init {
        log.info("Piece$name")

        currRotation = 0

        // these are only needed during init; getLayout() is unused at runtime
        var layouts: Array<Array<IntArray>?>? = null

        layouts = arrayOfNulls(availRotations * symmetric)
        firstSquarePos = IntArray(availRotations * symmetric)
        layouts[0] = layout

        rowsSet = arrayOfNulls(availRotations * symmetric)
        columnsSet = arrayOfNulls(availRotations * symmetric)

        firstSquarePos[0] = 0
        while (firstSquarePos[0] < layout[0].size && layout[0][firstSquarePos[0]] == 0) {
            firstSquarePos[0]++
        }

        var maxColumns = -1

        for (row in layout) {
            for (value in row) {
                if (value == 1) {
                    totalFill++
                    totalThisFill++
                }
            }
            if (row.size > maxColumns) {
                maxColumns = row.size
            }
        }

        printPart(0, layout)
        if (availRotations > 1) {
            layouts[1] = realRotate(layouts[0]!!, maxColumns, layout.size, 1)
            printPart(1, layouts[1]!!)
            if (availRotations > 2) {
                layouts[2] = realRotate(layouts[1]!!, layout.size, maxColumns, 2)
                printPart(2, layouts[2]!!)
                if (availRotations > 3) {
                    layouts[3] = realRotate(layouts[2]!!, maxColumns, layout.size, 3)
                    printPart(3, layouts[3]!!)
                    if (availRotations > 4) {
                        log.error("rotations is up to 4")
                    }
                }
            }
        }

        if (symmetric == 2) {
            for (i in 0 until availRotations) {
                layouts[i + availRotations] = copySymmetric(layouts[i]!!)
                printPart(i + availRotations, layouts[i + availRotations]!!)

                firstSquarePos[i + availRotations] = 0
                while (firstSquarePos[i + availRotations] < layouts[i + availRotations]!![0].size
                    && layouts[i + availRotations]!![0][firstSquarePos[i + availRotations]] == 0
                ) {
                    firstSquarePos[i + availRotations]++
                }
            }
        } else if (symmetric > 2) {
            log.error("symmetric is up to 2")
        }

        for (rot in 0 until availRotations * symmetric) {
            rowsSet[rot] = IntArray(totalThisFill)
            columnsSet[rot] = IntArray(totalThisFill)
            var setSoFar = 0
            for (i in layouts[rot]!!.indices) {
                for (j in layouts[rot]!![i].indices) {
                    if (layouts[rot]!![i][j] == 1) {
                        rowsSet[rot]!![setSoFar] = i
                        columnsSet[rot]!![setSoFar] = j
                        setSoFar++
                    }
                }
            }
        }
    }

    override fun toString() = "id $name rotation ${90 * currRotation} used ${row != -1}"

    private fun copySymmetric(original: Array<IntArray>): Array<IntArray> {
        val rows = original.size
        val result = Array<IntArray?>(rows) { null }
        for (i in 0 until rows) {
            result[i] = IntArray(original[rows - i - 1].size)
            for (j in result[i]!!.indices) {
                result[i]!![j] = original[rows - i - 1][j]
            }
        }
        @Suppress("UNCHECKED_CAST")
        return result as Array<IntArray>
    }

    private fun realRotate(original: Array<IntArray>, rows: Int, columns: Int, index: Int): Array<IntArray> {
        val result = Array(rows) { IntArray(columns) }

        for (i in 0 until columns) {
            for (j in 0 until rows) {
                try {
                    result[rows - j - 1][i] = original[i][j]
                } catch (_: ArrayIndexOutOfBoundsException) {
                }
            }
        }

        firstSquarePos[index] = 0
        while (firstSquarePos[index] < result[0].size && result[0][firstSquarePos[index]] == 0) {
            firstSquarePos[index]++
        }

        return result
    }

    fun rotate() {
        currRotation++
        if (currRotation == firstSquarePos.size) {
            currRotation = 0
        }
    }

    fun setPosition(row: Int, column: Int) {
        this.row = row
        this.column = column
    }

    private fun printPart(l: Int, layout: Array<IntArray>) {
        println("    layout=$l")
        for (ints in layout) {
            for (value in ints) {
                print(value)
            }
            println()
        }
    }
}
