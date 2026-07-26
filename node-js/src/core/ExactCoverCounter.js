class Node {
  constructor() {
    this.left = this;
    this.right = this;
    this.up = this;
    this.down = this;
    this.column = null;
  }
}

class Column extends Node {
  constructor() {
    super();
    this.column = this;
    this.size = 0;
  }
}

class ExactCoverCounter {
  constructor(columnCount, rows) {
    this.root = new Column();
    this.columns = new Array(columnCount);
    this.solutions = 0;
    this.triedRows = 0;

    let previous = this.root;
    for (let index = 0; index < columnCount; index++) {
      const column = new Column();
      this.columns[index] = column;
      this._linkHorizontally(previous, column);
      previous = column;
    }
    this._linkHorizontally(previous, this.root);

    for (const row of rows) {
      this._addRow(row);
    }
  }

  static count(columnCount, rows) {
    const counter = new ExactCoverCounter(columnCount, rows);
    counter._search();
    return { solutions: counter.solutions, triedRows: counter.triedRows };
  }

  _addRow(columnIndexes) {
    let first = null;
    let previous = null;

    for (const columnIndex of columnIndexes) {
      const column = this.columns[columnIndex];
      const node = new Node();
      node.column = column;

      node.down = column;
      node.up = column.up;
      column.up.down = node;
      column.up = node;
      column.size++;

      if (first === null) {
        first = node;
      } else {
        this._linkHorizontally(previous, node);
      }
      previous = node;
    }
    this._linkHorizontally(previous, first);
  }

  _search() {
    if (this.root.right === this.root) {
      this.solutions++;
      return;
    }

    const column = this._smallestColumn();
    if (column.size === 0) return;

    this._cover(column);
    for (let row = column.down; row !== column; row = row.down) {
      this.triedRows++;
      for (let node = row.right; node !== row; node = node.right) {
        this._cover(node.column);
      }
      this._search();
      for (let node = row.left; node !== row; node = node.left) {
        this._uncover(node.column);
      }
    }
    this._uncover(column);
  }

  _smallestColumn() {
    let smallest = null;
    for (let node = this.root.right; node !== this.root; node = node.right) {
      if (smallest === null || node.size < smallest.size) {
        smallest = node;
      }
    }
    return smallest;
  }

  _cover(column) {
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

  _uncover(column) {
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

  _linkHorizontally(left, right) {
    left.right = right;
    right.left = left;
  }
}

module.exports = ExactCoverCounter;
