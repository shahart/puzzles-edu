class CoverNode {
  left: CoverNode = this;
  right: CoverNode = this;
  up: CoverNode = this;
  down: CoverNode = this;
  column!: CoverColumn;
  rowIndex = -1;
}

class CoverColumn extends CoverNode {
  size = 0;

  constructor() {
    super();
    this.column = this;
  }
}

export interface ExactCoverResult {
  solutions: number;
  triedRows: number;
  selectedRows: number[];
}

export class ExactCoverCounter {
  private readonly root = new CoverColumn();
  private readonly columns: CoverColumn[];
  private readonly selectedRows: number[];
  private solutionRows: number[] = [];
  private solutions = 0;
  private triedRows = 0;

  private constructor(
    columnCount: number,
    rows: number[][],
    private readonly solutionLimit: number,
  ) {
    this.columns = new Array(columnCount);
    this.selectedRows = new Array(columnCount);

    let previous: CoverNode = this.root;
    for (let index = 0; index < columnCount; index++) {
      const column = new CoverColumn();
      this.columns[index] = column;
      this.linkHorizontally(previous, column);
      previous = column;
    }
    this.linkHorizontally(previous, this.root);

    rows.forEach((row, rowIndex) => this.addRow(row, rowIndex));
  }

  static findOne(columnCount: number, rows: number[][]): ExactCoverResult {
    const counter = new ExactCoverCounter(columnCount, rows, 1);
    counter.search(0);
    return {
      solutions: counter.solutions,
      triedRows: counter.triedRows,
      selectedRows: counter.solutionRows,
    };
  }

  private addRow(columnIndexes: number[], rowIndex: number): void {
    let first: CoverNode | null = null;
    let previous: CoverNode | null = null;

    for (const columnIndex of columnIndexes) {
      const column = this.columns[columnIndex];
      const node = new CoverNode();
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
        this.linkHorizontally(previous!, node);
      }
      previous = node;
    }

    if (first !== null && previous !== null) {
      this.linkHorizontally(previous, first);
    }
  }

  private search(depth: number): boolean {
    if (this.root.right === this.root) {
      this.solutions++;
      this.solutionRows = this.selectedRows.slice(0, depth);
      return this.solutions >= this.solutionLimit;
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

      const limitReached = this.search(depth + 1);

      for (let node = row.left; node !== row; node = node.left) {
        this.uncover(node.column);
      }
      if (limitReached) {
        this.uncover(column);
        return true;
      }
    }
    this.uncover(column);
    return false;
  }

  private smallestColumn(): CoverColumn {
    let smallest: CoverColumn | null = null;
    for (let node = this.root.right; node !== this.root; node = node.right) {
      const column = node as CoverColumn;
      if (smallest === null || column.size < smallest.size) {
        smallest = column;
      }
    }
    return smallest!;
  }

  private cover(column: CoverColumn): void {
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

  private uncover(column: CoverColumn): void {
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

  private linkHorizontally(left: CoverNode, right: CoverNode): void {
    left.right = right;
    right.left = left;
  }
}
