class Piece {
  static totalFill = 0;

  constructor(index, layout, availRotations, symmetric, name) {
    this.index = index;
    this.name = name;
    this.currRotation = 0;
    this.row = -1;
    this.column = -1;
    this.totalThisFill = 0;

    const totalOrientations = availRotations * symmetric;
    const layouts = new Array(totalOrientations);
    this.firstSquarePos = new Array(totalOrientations);
    layouts[0] = layout;

    this.rowsSet = new Array(totalOrientations);
    this.columnsSet = new Array(totalOrientations);

    this.firstSquarePos[0] = 0;
    while (
      this.firstSquarePos[0] < layout[0].length &&
      layout[0][this.firstSquarePos[0]] === 0
    ) {
      this.firstSquarePos[0]++;
    }

    let maxColumns = -1;
    for (const row of layout) {
      for (const value of row) {
        if (value === 1) {
          Piece.totalFill++;
          this.totalThisFill++;
        }
      }
      if (row.length > maxColumns) {
        maxColumns = row.length;
      }
    }

    this._printPart(0, layout);
    if (availRotations > 1) {
      layouts[1] = this._realRotate(layouts[0], maxColumns, layout.length, 1);
      this._printPart(1, layouts[1]);
      if (availRotations > 2) {
        layouts[2] = this._realRotate(layouts[1], layout.length, maxColumns, 2);
        this._printPart(2, layouts[2]);
        if (availRotations > 3) {
          layouts[3] = this._realRotate(layouts[2], maxColumns, layout.length, 3);
          this._printPart(3, layouts[3]);
        }
      }
    }

    if (symmetric === 2) {
      for (let i = 0; i < availRotations; i++) {
        layouts[i + availRotations] = this._copySymmetric(layouts[i]);
        this._printPart(i + availRotations, layouts[i + availRotations]);
        this.firstSquarePos[i + availRotations] = 0;
        while (
          this.firstSquarePos[i + availRotations] < layouts[i + availRotations][0].length &&
          layouts[i + availRotations][0][this.firstSquarePos[i + availRotations]] === 0
        ) {
          this.firstSquarePos[i + availRotations]++;
        }
      }
    }

    for (let rot = 0; rot < totalOrientations; rot++) {
      this.rowsSet[rot] = new Array(this.totalThisFill);
      this.columnsSet[rot] = new Array(this.totalThisFill);
      let setSoFar = 0;
      for (let i = 0; i < layouts[rot].length; i++) {
        for (let j = 0; j < layouts[rot][i].length; j++) {
          if (layouts[rot][i][j] === 1) {
            this.rowsSet[rot][setSoFar] = i;
            this.columnsSet[rot][setSoFar] = j;
            setSoFar++;
          }
        }
      }
    }
  }

  getAvailRotations() {
    return this.firstSquarePos.length;
  }

  getFirstSquarePos() {
    return this.firstSquarePos[this.currRotation];
  }

  getRowSet(i) {
    return this.rowsSet[this.currRotation][i];
  }

  getColumnSet(i) {
    return this.columnsSet[this.currRotation][i];
  }

  getRow() {
    return this.row;
  }

  getColumn() {
    return this.column;
  }

  rotate() {
    this.currRotation++;
    if (this.currRotation === this.firstSquarePos.length) {
      this.currRotation = 0;
    }
  }

  setPosition(row, column) {
    this.row = row;
    this.column = column;
  }

  _copySymmetric(original) {
    const rows = original.length;
    const result = new Array(rows);
    for (let i = 0; i < rows; i++) {
      result[i] = new Array(original[rows - i - 1].length);
      for (let j = 0; j < result[i].length; j++) {
        result[i][j] = original[rows - i - 1][j];
      }
    }
    return result;
  }

  _realRotate(original, rows, columns, index) {
    const result = new Array(rows);
    for (let i = 0; i < rows; i++) {
      result[i] = new Array(columns).fill(0);
    }

    for (let i = 0; i < columns; i++) {
      for (let j = 0; j < rows; j++) {
        try {
          result[rows - j - 1][i] = original[i][j];
        } catch (_) {
          // out of bounds
        }
      }
    }

    this.firstSquarePos[index] = 0;
    while (
      this.firstSquarePos[index] < result[0].length &&
      result[0][this.firstSquarePos[index]] === 0
    ) {
      this.firstSquarePos[index]++;
    }

    return result;
  }

  _printPart(l, layout) {
    console.log(`    layout=${l}`);
    for (const row of layout) {
      console.log(row.join(''));
    }
  }

  toString() {
    return `id ${this.name} rotation ${90 * this.currRotation} used ${this.row !== -1}`;
  }
}

module.exports = Piece;
