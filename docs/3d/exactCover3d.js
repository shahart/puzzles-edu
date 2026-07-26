class Node {
    constructor() {
        this.left = this;
        this.right = this;
        this.up = this;
        this.down = this;
        this.column = null;
        this.rowIndex = -1;
    }
}

class Column extends Node {
    constructor() {
        super();
        this.column = this;
        this.size = 0;
    }
}

class ExactCover3d {
    constructor(columnCount, rows) {
        this.root = new Column();
        this.columns = new Array(columnCount);
        this.selectedRows = new Array(columnCount);
        this.solutionRows = [];
        this.triedRows = 0;

        let previous = this.root;
        for (let index = 0; index < columnCount; index++) {
            const column = new Column();
            this.columns[index] = column;
            this.linkHorizontally(previous, column);
            previous = column;
        }
        this.linkHorizontally(previous, this.root);

        rows.forEach((row, rowIndex) => this.addRow(row, rowIndex));
    }

    static findOne(columnCount, rows) {
        const solver = new ExactCover3d(columnCount, rows);
        const solved = solver.search(0);
        return {
            solved,
            triedRows: solver.triedRows,
            selectedRows: solver.solutionRows
        };
    }

    addRow(columnIndexes, rowIndex) {
        let first = null;
        let previous = null;
        for (const columnIndex of columnIndexes) {
            const column = this.columns[columnIndex];
            const node = new Node();
            node.column = column;
            node.rowIndex = rowIndex;

            node.down = column;
            node.up = column.up;
            column.up.down = node;
            column.up = node;
            column.size++;

            if (first === null) {
                first = node;
            } else {
                this.linkHorizontally(previous, node);
            }
            previous = node;
        }
        this.linkHorizontally(previous, first);
    }

    search(depth) {
        if (this.root.right === this.root) {
            this.solutionRows = this.selectedRows.slice(0, depth);
            return true;
        }

        const column = this.smallestColumn();
        if (column.size === 0) {
            return false;
        }

        this.cover(column);
        for (let row = column.down; row !== column; row = row.down) {
            this.triedRows++;
            this.selectedRows[depth] = row.rowIndex;
            for (let node = row.right; node !== row; node = node.right) {
                this.cover(node.column);
            }
            if (this.search(depth + 1)) {
                this.uncover(column);
                return true;
            }
            for (let node = row.left; node !== row; node = node.left) {
                this.uncover(node.column);
            }
        }
        this.uncover(column);
        return false;
    }

    smallestColumn() {
        let smallest = null;
        for (let node = this.root.right; node !== this.root; node = node.right) {
            if (smallest === null || node.size < smallest.size) {
                smallest = node;
            }
        }
        return smallest;
    }

    cover(column) {
        column.right.left = column.left;
        column.left.right = column.right;
        for (let row = column.down; row !== column; row = row.down) {
            for (let node = row.right; node !== row; node = node.right) {
                node.down.up = node.up;
                node.up.down = node.down;
                node.column.size--;
            }
        }
    }

    uncover(column) {
        for (let row = column.up; row !== column; row = row.up) {
            for (let node = row.left; node !== row; node = node.left) {
                node.column.size++;
                node.down.up = node;
                node.up.down = node;
            }
        }
        column.right.left = column;
        column.left.right = column;
    }

    linkHorizontally(left, right) {
        left.right = right;
        right.left = left;
    }
}

export { ExactCover3d };
