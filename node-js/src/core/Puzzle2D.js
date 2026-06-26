const Piece = require('./Piece');

class Puzzle2D {
  constructor() {
    this.PIECES = 12;
    this.ROWS = 0;
    this.COLUMNS = 0;
    this.totalSolutions = 0;
    this.triedPieces = 0;
    this.row = 0;
    this.column = 0;
    this.piecesIndices = [];
    this.solution = new Array(this.PIECES);
    this.pieces = new Array(this.PIECES);
    this.names = '';
    this.grid = [];
    this.currPiece = null;
    this.totalFillInGrid = 0;
    this.availInGrid = 0;
    this._aborted = false;
  }

  set(rows, columns) {
    Piece.totalFill = 0;
    this.piecesIndices = [];
    this.solution = new Array(this.PIECES);
    this.totalSolutions = 0;
    this.triedPieces = 0;
    this._aborted = false;

    this.ROWS = rows;
    this.COLUMNS = columns;

    this.grid = new Array(this.ROWS);
    for (let i = 0; i < this.ROWS; i++) {
      this.grid[i] = new Array(this.COLUMNS).fill(0);
    }

    const allPieces = [
      [[1], [1], [1], [1, 1]],
      [[1, 1], [1], [1, 1]],
      [[0, 1, 1], [1, 1], [0, 1]],
      [[0, 1], [1, 1, 1], [0, 1]],
      [[1, 1, 1, 1], [0, 0, 1]],
      [[0, 1], [1, 1], [1], [1]],
      [[0, 0, 1], [0, 1, 1], [1, 1]],
      [[1], [1, 1], [1, 1]],
      [[0, 0, 1], [1, 1, 1], [1]],
      [[0, 0, 1], [0, 0, 1], [1, 1, 1]],
      [[0, 0, 1], [1, 1, 1], [0, 0, 1]],
      [[1, 1, 1, 1, 1]]
    ];

    const rotations = [4, 4, 2, 1, 4, 4, 4, 4, 2, 4, 4, 2];
    const symmetric = [2, 1, 1, 1, 2, 2, 1, 2, 2, 1, 1, 1];
    this.names = 'LUFXYNWPZVTI';

    if (this.ROWS === this.COLUMNS && this.ROWS === 8) {
      rotations[2] = 1;
    }

    if (this.ROWS === 0 && this.COLUMNS === 0) {
      throw new Error('not supported yet');
    } else {
      for (let i = 0; i < this.PIECES; i++) {
        this.piecesIndices.push(i);
        this.pieces[i] = new Piece(i, allPieces[i], rotations[i], symmetric[i], this.names[i]);
      }
      if (this.ROWS === this.COLUMNS && this.ROWS === 8) {
        this.grid[0][0] = -1;
        this.grid[7][0] = -1;
        this.grid[0][7] = -1;
        this.grid[7][7] = -1;
      }
      while (this.grid[0][this.column] === -1) {
        this.column++;
      }
    }

    this.totalFillInGrid = this.ROWS * this.COLUMNS;
    for (const row of this.grid) {
      for (const val of row) {
        if (val === -1) {
          this.totalFillInGrid--;
        }
      }
    }

    this.availInGrid = this.totalFillInGrid;
    console.log(`Found ${this.ROWS} rows, ${this.COLUMNS} cols, with total of cells ${this.availInGrid}`);
    this.showGrid();
  }

  showGrid() {
    console.log(`[msec] showGrid. Tried Pieces ${this.triedPieces} leftPieces ${this.piecesIndices.length}`);
    for (let i = 0; i < this.ROWS; i++) {
      let line = '';
      for (let j = 0; j < this.COLUMNS; j++) {
        if (this.grid[i][j] === -1) {
          line += '*  ';
        } else if (this.grid[i][j] === 0) {
          line += '-  ';
        } else {
          line += `${this.names[this.grid[i][j] - 1]} `;
        }
      }
      console.log(line);
    }
  }

  showPieces() {
    let line = '';
    for (let i = 0; i < this.PIECES - this.piecesIndices.length; i++) {
      line += `${this.names[this.solution[i]]} `;
    }
    console.log(line);
  }

  put() {
    const leftPieces = this.piecesIndices.length;

    if (leftPieces === 0) {
      this.totalSolutions++;
      console.log(`totalSolutions ${this.totalSolutions}`);
      if (this.totalSolutions === 1) {
        console.log('Found a solution');
        this.showGrid();
        this.showPieces();
      }
    }

    if (this.totalSolutions >= 1) return;

    const rowsSet = new Array(5);
    const columnsSet = new Array(5);

    for (let i = 0; i < leftPieces; i++) {
      const pieceIdx = this.piecesIndices[i];
      this.currPiece = this.pieces[pieceIdx];
      for (let r = this.currPiece.getAvailRotations(); r >= 1; r--) {
        if (this.canPut(rowsSet, columnsSet)) {
          this.piecesIndices.splice(i, 1);
          this.solution[this.PIECES - leftPieces] = pieceIdx;
          this.putCurrPiece(rowsSet, columnsSet);

          if (this.triedPieces % 50000 === 0) {
            this.showGrid();
            this.showPieces();

            if (this._aborted) {
              console.log(`Id ${this.ROWS}_${this.COLUMNS}. Signaled timed out! totalSolutions ${this.totalSolutions}`);
              return;
            }
          }

          this.put();
          this.removeLast(pieceIdx, rowsSet, columnsSet);
          this.piecesIndices.splice(i, 0, pieceIdx);
        }
        this.currPiece.rotate();
      }
    }
  }

  putCurrPiece(rowsSet, columnsSet) {
    this.currPiece.setPosition(this.row, this.column);

    const currIndex = this.currPiece.index + 1;
    for (let i = 0; i < this.currPiece.totalThisFill; i++) {
      this.grid[rowsSet[i]][columnsSet[i]] = currIndex;
    }

    this.goForward();
    this.availInGrid -= this.currPiece.totalThisFill;
  }

  goForward() {
    while (this.row < this.ROWS) {
      while (this.column < this.COLUMNS) {
        if (this.grid[this.row][this.column] === 0) return;
        this.column++;
      }
      this.column = 0;
      this.row++;
    }
  }

  removeLast(piece, rowsSet, columnsSet) {
    this.currPiece = this.pieces[piece];

    this.row = this.currPiece.getRow();
    this.column = this.currPiece.getColumn();

    for (let i = 0; i < this.currPiece.totalThisFill; i++) {
      this.grid[rowsSet[i]][columnsSet[i]] = 0;
    }

    this.availInGrid += this.currPiece.totalThisFill;
  }

  canPut(rowsSet, columnsSet) {
    try {
      let setSoFar = 0;
      const j = this.currPiece.getFirstSquarePos();
      const columnjj = this.column - j;
      for (let i = 0; i < this.currPiece.totalThisFill; i++) {
        const rowi = this.row + this.currPiece.getRowSet(setSoFar);
        const columnj = columnjj + this.currPiece.getColumnSet(setSoFar);
        if (this.grid[rowi][columnj] !== 0) {
          return false;
        } else {
          rowsSet[setSoFar] = rowi;
          columnsSet[setSoFar] = columnj;
          setSoFar++;
        }
      }
    } catch (_) {
      return false;
    }

    this.triedPieces++;
    return true;
  }

  solve(signal) {
    const start = Date.now();

    if (signal) {
      signal.addEventListener('abort', () => {
        this._aborted = true;
      });
    }

    if (this.totalFillInGrid !== Piece.totalFill) {
      console.error(`Invalid config, grid ${this.totalFillInGrid} pieces ${Piece.totalFill}`);
    } else {
      Piece.totalFill = 0;
      console.log(`Starting rows ${this.ROWS} cols ${this.COLUMNS}`);
      this.put();
    }

    const elapsedTime = Math.floor((Date.now() - start) / 1000);
    console.log(`tried ${this.triedPieces.toLocaleString()} pieces`);
    if (elapsedTime > 0) {
      console.log(`at ${(this.triedPieces / elapsedTime).toLocaleString()} pieces per sec`);
    }
    console.log(`number of solutions ${this.totalSolutions * (this.ROWS === this.COLUMNS ? 8 : 4)}`);

    return this.totalSolutions;
  }
}

module.exports = Puzzle2D;
