package edu.generalpuzzle.kickoff;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Counts exact covers using Knuth's Algorithm X with dancing links.
 */
final class ExactCoverCounter {

    record Result(long solutions, long triedRows) {
    }

    private final Column root = new Column();
    private final Column[] columns;
    private final Predicate<int[]> solutionFilter;
    private final int[] selectedRows;
    private long solutions;
    private long triedRows;

    private ExactCoverCounter(
            int columnCount, List<int[]> rows, Predicate<int[]> solutionFilter) {
        columns = new Column[columnCount];
        this.solutionFilter = solutionFilter;
        selectedRows = new int[columnCount];
        Node previous = root;
        for (int index = 0; index < columnCount; index++) {
            Column column = new Column();
            columns[index] = column;
            linkHorizontally(previous, column);
            previous = column;
        }
        linkHorizontally(previous, root);

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            addRow(rows.get(rowIndex), rowIndex);
        }
    }

    static Result count(int columnCount, List<int[]> rows) {
        return count(columnCount, rows, ignored -> true);
    }

    static Result count(
            int columnCount, List<int[]> rows, Predicate<int[]> solutionFilter) {
        ExactCoverCounter counter = new ExactCoverCounter(columnCount, rows, solutionFilter);
        counter.search(0);
        return new Result(counter.solutions, counter.triedRows);
    }

    private void addRow(int[] columnIndexes, int rowIndex) {
        Node first = null;
        Node previous = null;
        for (int columnIndex : columnIndexes) {
            Column column = columns[columnIndex];
            Node node = new Node();
            node.column = column;
            node.rowIndex = rowIndex;

            node.down = column;
            node.up = column.up;
            column.up.down = node;
            column.up = node;
            column.size++;

            if (first == null) {
                first = node;
            } else {
                linkHorizontally(previous, node);
            }
            previous = node;
        }
        linkHorizontally(previous, first);
    }

    private void search(int depth) {
        if (root.right == root) {
            if (solutionFilter.test(Arrays.copyOf(selectedRows, depth))) {
                solutions++;
            }
            return;
        }

        Column column = smallestColumn();
        if (column.size == 0) {
            return;
        }
        cover(column);
        for (Node row = column.down; row != column; row = row.down) {
            triedRows++;
            selectedRows[depth] = row.rowIndex;
            for (Node node = row.right; node != row; node = node.right) {
                cover(node.column);
            }
            search(depth + 1);
            for (Node node = row.left; node != row; node = node.left) {
                uncover(node.column);
            }
        }
        uncover(column);
    }

    private Column smallestColumn() {
        Column smallest = null;
        for (Node node = root.right; node != root; node = node.right) {
            Column column = (Column) node;
            if (smallest == null || column.size < smallest.size) {
                smallest = column;
            }
        }
        return smallest;
    }

    private static void cover(Column column) {
        column.right.left = column.left;
        column.left.right = column.right;
        for (Node row = column.down; row != column; row = row.down) {
            for (Node node = row.right; node != row; node = node.right) {
                node.down.up = node.up;
                node.up.down = node.down;
                node.column.size--;
            }
        }
    }

    private static void uncover(Column column) {
        for (Node row = column.up; row != column; row = row.up) {
            for (Node node = row.left; node != row; node = node.left) {
                node.column.size++;
                node.down.up = node;
                node.up.down = node;
            }
        }
        column.right.left = column;
        column.left.right = column;
    }

    private static void linkHorizontally(Node left, Node right) {
        left.right = right;
        right.left = left;
    }

    private static class Node {
        Node left = this;
        Node right = this;
        Node up = this;
        Node down = this;
        Column column;
        int rowIndex;
    }

    private static final class Column extends Node {
        int size;

        private Column() {
            column = this;
        }
    }
}
