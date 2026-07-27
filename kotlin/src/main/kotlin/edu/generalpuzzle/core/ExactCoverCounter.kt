package edu.generalpuzzle.core

internal data class ExactCoverResult(
    val solutions: Int,
    val triedRows: Int,
    val selectedRows: List<Int> = emptyList()
)

/**
 * Knuth's Algorithm X with dancing links.
 *
 * Puzzle placements are rows and every board cell and puzzle piece is a column.
 * A complete exact cover therefore fills every cell while using every piece once.
 */
internal class ExactCoverCounter private constructor(
    columnCount: Int,
    rows: List<IntArray>,
    private val solutionLimit: Int
) {
    private open class Node {
        var left: Node = this
        var right: Node = this
        var up: Node = this
        var down: Node = this
        lateinit var column: Column
        var rowIndex = -1
    }

    private class Column : Node() {
        var size = 0

        init {
            column = this
        }
    }

    private val root = Column()
    private val columns = Array(columnCount) { Column() }
    private val selectedRows = IntArray(columnCount)
    private var solutionRows = emptyList<Int>()
    private var solutions = 0
    private var triedRows = 0

    init {
        var previous: Node = root
        for (column in columns) {
            linkHorizontally(previous, column)
            previous = column
        }
        linkHorizontally(previous, root)

        rows.forEachIndexed(::addRow)
    }

    private fun addRow(rowIndex: Int, columnIndexes: IntArray) {
        var first: Node? = null
        var previous: Node? = null

        for (columnIndex in columnIndexes) {
            val column = columns[columnIndex]
            val node = Node().apply {
                this.column = column
                this.rowIndex = rowIndex
                down = column
                up = column.up
            }
            column.up.down = node
            column.up = node
            column.size++

            if (first == null) {
                first = node
            } else {
                linkHorizontally(previous!!, node)
            }
            previous = node
        }

        if (first != null) {
            linkHorizontally(previous!!, first)
        }
    }

    private fun search(depth: Int): Boolean {
        if (Thread.currentThread().isInterrupted) {
            return true
        }
        if (root.right === root) {
            solutions++
            solutionRows = selectedRows.take(depth)
            return solutions >= solutionLimit
        }

        val column = smallestColumn()
        if (column.size == 0) {
            return false
        }

        cover(column)
        var row = column.down
        while (row !== column) {
            triedRows++
            selectedRows[depth] = row.rowIndex

            var node = row.right
            while (node !== row) {
                cover(node.column)
                node = node.right
            }

            val limitReached = search(depth + 1)

            node = row.left
            while (node !== row) {
                uncover(node.column)
                node = node.left
            }
            if (limitReached) {
                uncover(column)
                return true
            }
            row = row.down
        }
        uncover(column)
        return false
    }

    private fun smallestColumn(): Column {
        var smallest: Column? = null
        var node = root.right
        while (node !== root) {
            val column = node as Column
            if (smallest == null || column.size < smallest.size) {
                smallest = column
            }
            node = node.right
        }
        return smallest!!
    }

    private fun cover(column: Column) {
        column.right.left = column.left
        column.left.right = column.right
        var row = column.down
        while (row !== column) {
            var node = row.right
            while (node !== row) {
                node.down.up = node.up
                node.up.down = node.down
                node.column.size--
                node = node.right
            }
            row = row.down
        }
    }

    private fun uncover(column: Column) {
        var row = column.up
        while (row !== column) {
            var node = row.left
            while (node !== row) {
                node.column.size++
                node.down.up = node
                node.up.down = node
                node = node.left
            }
            row = row.up
        }
        column.right.left = column
        column.left.right = column
    }

    private fun linkHorizontally(left: Node, right: Node) {
        left.right = right
        right.left = left
    }

    companion object {
        fun count(columnCount: Int, rows: List<IntArray>): ExactCoverResult {
            return run(columnCount, rows, Int.MAX_VALUE)
        }

        fun findOne(columnCount: Int, rows: List<IntArray>): ExactCoverResult {
            return run(columnCount, rows, 1)
        }

        private fun run(columnCount: Int, rows: List<IntArray>, limit: Int): ExactCoverResult {
            val counter = ExactCoverCounter(columnCount, rows, limit)
            counter.search(0)
            return ExactCoverResult(counter.solutions, counter.triedRows, counter.solutionRows)
        }
    }
}
