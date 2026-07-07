export class Piece {
  static totalFill = 0;

  readonly index: number;
  readonly name: string;
  currRotation = 0;
  row = -1;
  column = -1;
  totalThisFill = 0;

  private readonly firstSquarePos: number[];
  private readonly rowsSet: number[][];
  private readonly columnsSet: number[][];

  constructor(
    index: number,
    layout: number[][],
    availRotations: number,
    symmetric: number,
    name: string,
  ) {
    this.index = index;
    this.name = name;

    const totalOrientations = availRotations * symmetric;
    const layouts: number[][][] = new Array(totalOrientations);
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

    this.printPart(0, layout);
    if (availRotations > 1) {
      layouts[1] = this.realRotate(layouts[0], maxColumns, layout.length, 1);
      this.printPart(1, layouts[1]);
      if (availRotations > 2) {
        layouts[2] = this.realRotate(layouts[1], layout.length, maxColumns, 2);
        this.printPart(2, layouts[2]);
        if (availRotations > 3) {
          layouts[3] = this.realRotate(layouts[2], maxColumns, layout.length, 3);
          this.printPart(3, layouts[3]);
        }
      }
    }

    if (symmetric === 2) {
      for (let i = 0; i < availRotations; i++) {
        layouts[i + availRotations] = this.copySymmetric(layouts[i]);
        this.printPart(i + availRotations, layouts[i + availRotations]);
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

  getAvailRotations(): number {
    return this.firstSquarePos.length;
  }

  getFirstSquarePos(): number {
    return this.firstSquarePos[this.currRotation];
  }

  getRowSet(i: number): number {
    return this.rowsSet[this.currRotation][i];
  }

  getColumnSet(i: number): number {
    return this.columnsSet[this.currRotation][i];
  }

  getRow(): number {
    return this.row;
  }

  getColumn(): number {
    return this.column;
  }

  rotate(): void {
    this.currRotation++;
    if (this.currRotation === this.firstSquarePos.length) {
      this.currRotation = 0;
    }
  }

  setPosition(row: number, column: number): void {
    this.row = row;
    this.column = column;
  }

  private copySymmetric(original: number[][]): number[][] {
    const rows = original.length;
    const result: number[][] = new Array(rows);
    for (let i = 0; i < rows; i++) {
      result[i] = new Array(original[rows - i - 1].length);
      for (let j = 0; j < result[i].length; j++) {
        result[i][j] = original[rows - i - 1][j];
      }
    }
    return result;
  }

  private realRotate(original: number[][], rows: number, columns: number, index: number): number[][] {
    const result: number[][] = new Array(rows);
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

  private printPart(l: number, layout: number[][]): void {
    console.log(`    layout=${l}`);
    for (const row of layout) {
      console.log(row.join(''));
    }
  }

  toString(): string {
    return `id ${this.name} rotation ${90 * this.currRotation} used ${this.row !== -1}`;
  }
}
