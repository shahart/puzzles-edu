import { Piece3d } from "./piece3d.js";
import { Builder3d } from "./builder3d.js";

window.globalTotalFill = 0;

class Puzzle3d {
    constructor(pieces, rows, columns, floors, input) {
        if (columns > rows) {
            console.warn('You might want to switch dimensions.');
        }
        window.globalTotalFill = 0;
        this.allLines = '';
        this.start = new Date().getTime();
        this.PIECES = pieces;
        this.ROWS = rows;
        this.COLUMNS = columns;
        this.FLOORS = floors;
        this.totalSolutions = 0;
        this.EXIT_SIGN = 100000;
        this.triedPieces = 0;
        this.solutionFound = false;
        this.row = 0;
        this.column = 0;
        this.floor = 0;
        this.piecesIndices = new Array();
        this.solution = new Array(this.PIECES).fill(0);
        this.pieces = [this.PIECES];
        this.names;
        if (rows !== rows || columns !== columns) { // NaN check, result of parseInt of Not-a-Number
            console.error('RangeError: Invalid array length');
            alert('RangeError: Invalid array length');
        }
        this.grid = new Array(rows).fill(0).map(_ => new Array(columns).fill(0).map(_ => new Array(floors).fill(0)));
        this.gridCopy = new Array(rows).fill(0).map(_ => new Array(columns).fill(0).map(_ => new Array(floors).fill(0)));
        this.currPiece;
        this.totalFillInGrid = 0;
        this.allPieces = [
            // Poly
            [[1],[1],[1],[1,1]],        //4-2 (2)   L sym = indicates it is symmetric // TODO write flip
            [[1,1],[1],[1,1]],          //4-1 (5)   U
            [[0,1,1],[1,1],[0,1]],      //4-2 (9)   F sym - used to eliminate "dups"
            [[0,1],[1,1,1],[0,1]],      //1-1 (6)   X
            [[1,1,1,1],[0,0,1]],        //4-2 (3)   Y sym
            [[0,1],[1,1],[1],[1]],      //4-2 (12)  N sym
            [[0,0,1],[0,1,1],[1,1]],    //4-1 (7)   W
            [[1],[1,1],[1,1]],          //4-2 (4)   P sym
            [[0,0,1],[1,1,1],[1]],      //2-2 (11)  Z sym ??
            [[0,0,1],[0,0,1],[1,1,1]],  //4-1 (10)  V
            [[0,0,1],[1,1,1],[0,0,1]],  //4-1 (8)   T
            [[1,1,1,1,1]]               //2-1 (1)   I
        ];
        this.names = "LUFXYNWPZVTI"; // Poly
        this.rotations = [4, 4, 2/*4*/, 1, 4, 4, 4, 4, 2, 4, 4, 2];
        this.symmetric = [2, 1, 1/*2*/, 1, 2, 2, 1, 2, 2, 1, 1, 1];
        this.totalFillInGrid = this.ROWS * this.COLUMNS * this.FLOORS;
        if (input) {
            new Builder3d(this, input);
            if (this.PIECES === 12 && this.names === "LUFXYNWPZVTI") {
                console.log("Polyominos");
            }
        }
        else {
            if (this.allPieces.length > this.PIECES) {
                console.warn("using first pieces from the whole set");
            }
            for (let i = 0; i < this.PIECES/*allPieces.length*/; i++) {
                let piece = i;
                this.piecesIndices.push(piece);
                this.pieces[i] = new Piece3d(piece, this.allPieces[i], this.rotations[i], this.symmetric[i], this.names.charAt(i));
                this.pieces[i].shuffle();
            }
            for (let i = 0; i < this.grid.length; i++) {
                for (let j = 0; j < this.grid[i].length; j++) {
                    if (this.grid[i][j] === -1) {
                        this.totalFillInGrid--;
                    }
                }
            }
            console.log("Found " + this.ROWS + " rows, " + this.COLUMNS + " cols, " + this.FLOORS + " floors, with total of cells " + this.totalFillInGrid);
        }
        while (this.grid[0][this.column][this.floor] === -1) {
            this.column++;
        }
        this.piecesIndices = this.piecesIndices.sort(function () { return Math.random() - 0.5; });
    }

    showGrid() {
        // console.log(new Date().getTime() - this.start + " [msec] grid:"); // . Tried Pieces " + this.triedPieces); //  + " leftPieces " + this.piecesIndices.length);
        this.allLines = '';
        for (let k=0; k<this.FLOORS; k++) {
            for (let i = 0; i < this.ROWS; i++) {
                let line = "";
                if (i + 1 < 10) {
                    line += " " + (i + 1);
                } else {
                    line += (i + 1);
                }
                line += "  ";
                for (let j = 0; j < this.COLUMNS; j++) {
                    if (this.grid[i] === undefined || this.grid[i][j] === undefined || this.grid[i][j][k] === undefined) {
                        console.error("undefined grid cell, row " + i + " column " + j + " floor " + k + " make sure `#rows,columns,floors` is correct");
                        alert("undefined grid cell, row " + i + " column " + j + " floor " + k + " make sure `#rows,columns,floors` is correct");
                        throw new Error("undefined grid cell, row " + i + " column " + j + " floor " + k + " make sure `#rows,columns,floors` is correct");
                    }
                    if (this.grid[i][j][k] === -1) {
                        line += "*  ";
                        // } else if (this.totalSolutions === 0) {
                    } else if (this.grid[i][j][k] === 0) {
                        line += "-  ";
                    } else {
                        let ch = this.names.charAt(this.grid[i][j][k] - 1);
                        line += ch + "  ";
                    }
                }
                if (this.totalSolutions === 0) {
                    console.log(line);
                }
                if (!this.solutionFound) {
                    this.allLines += line + "\n";
                }
            }
        }
        if (this.totalSolutions > 0 && this.totalSolutions < this.EXIT_SIGN) {
            this.solutionFound = true;
        }
    }

    canPut(rowsSet, columnsSet, floorsSet) {
        // console.debug(new Date().getTime() - this.start + " [msec] canPut " + this.currPiece.name + " R" + this.currPiece.currRotation);
        let setSoFar = 0;
        let columnj = this.column;
        let j = this.currPiece.getFirstSquarePos();
        let columnjj = this.column - j;
        let floorj; // todo
        let rowi;

        for (let i = 0; i < this.currPiece.totalThisFill; i++) {
            rowi = this.row + this.currPiece.getRowSet(setSoFar);
            columnj = columnjj + this.currPiece.getColumnSet(setSoFar); // column + currPiece.getColumnSet(setSoFar) - j;
            floorj = 0 + this.currPiece.getFloorSet(setSoFar); // todo
            if (this.grid[rowi] === undefined) {
                return false;
            }
            let gridRowiColumnJ = this.grid[rowi][columnj];
            if (gridRowiColumnJ === undefined) {
                return false;
            }
            let gridRowiColumnJk = this.grid[rowi][columnj][floorj];
            if (gridRowiColumnJk === undefined) {
                return false;
            }
            if (gridRowiColumnJk !== 0) {
                return false;
            }
            let pieceVal = this.currPiece.getLayout() [this.currPiece.getRowSet(setSoFar)] [this.currPiece.getColumnSet(setSoFar)][this.currPiece.getFloorSet(setSoFar)];
            if (gridRowiColumnJ === 0 && pieceVal === 1) {
                rowsSet[setSoFar] = rowi;
                columnsSet[setSoFar] = columnj;
                floorsSet[setSoFar] = floorj;
                setSoFar++;
            }
            else {
                return;
            }
        }
        this.triedPieces++;
        return true;
    }

    put() {
        if (this.totalSolutions === this.EXIT_SIGN) {
            return;
        }
        let leftPieces = this.piecesIndices.length;
        // console.debug(new Date().getTime() - this.start + " [msec] put, leftPieces " + leftPieces);
        if ((leftPieces === 0)) {
            this.totalSolutions++;
            console.log(new Date().getTime() - this.start + " [msec] Found a solution, click 'Show'");

            if (this.totalSolutions === 1) {
                this.showGrid();
                this.showPieces();
                this.totalSolutions = this.EXIT_SIGN;
            }
        }
        // this.showGrid();
        if (this.totalSolutions > 1) { // replacement of the above throw new Error(notif)
            return;
        }
        let timeoutThreshold = 1500;
        if (new Date().getTime() - this.start > timeoutThreshold && timeoutThreshold > 0) {
            this.showGrid();
            let msg = new Date().toISOString() + ' Timeout, pieces per sec ' + Math.trunc(this.triedPieces / timeoutThreshold) + 'K';
            console.warn(msg);
            this.totalSolutions = this.EXIT_SIGN;
            this.allLines = msg;
        }
        let rowsSet = new Array(5).fill(0); // TODO dynamic, per the rows in the piece
        let columnsSet = new Array(5).fill(0);
        let floorsSet = new Array(5).fill(0);

        for (let i=0; i< leftPieces; i++) {
            let piece = this.piecesIndices[i];
            this.currPiece = this.pieces[piece];
            for (let r = this.pieces[piece].getAvailRotations(); r > 0; r--, this.currPiece.rotate()) {
                // currPieceLayout = currPiece.getLayout();
                if (this.canPut(rowsSet, columnsSet, floorsSet)) {
                    this.piecesIndices.splice(i, 1);
                    this.solution[this.PIECES-leftPieces] = piece; // solution.add(piece);
                    this.putCurrPiece(rowsSet, columnsSet, floorsSet);
                    if (this.triedPieces % 50000 === 0 /* || leftPieces <= 2 */) {
                        this.showGrid();
                        this.showPieces();
                    }
                    this.put(); // the recurse
                    this.removeLast(piece, rowsSet, columnsSet, floorsSet);
                    this.piecesIndices.splice(i, 0, piece);
                }
            }
        }
    }

    showPieces() {
        let line = '';
        for (let i=0; i<this.PIECES - this.piecesIndices.length; i++) {
            line += this.names.charAt(this.solution[i]) + " ";
        }
        console.log(new Date().getTime() - this.start + " [msec] pieces " + line + " tried pieces " + this.triedPieces);
    }

    putCurrPiece(rowsSet, columnsSet, floorsSet) {
        // console.debug(new Date().getTime() - this.start + " [msec] putCurrPiece " + this.currPiece.name);
        this.currPiece.setPosition(this.row, this.column);
        let currIndex = this.currPiece.index+1;
        for (let i=0; i<this.currPiece.totalThisFill; i++) {
            this.grid[rowsSet[i]][columnsSet[i]][floorsSet[i]] = currIndex;
        }
        // this.showGrid();
        // find next avail free position
        this.goForward();
        this.availInGrid -= this.currPiece.totalThisFill;
    }

    goForward() {
        // console.debug(new Date().getTime() - this.start + " [msec] goForward");
        for (; this.floor<this.FLOORS; this.floor++) {
            for (; this.row < this.ROWS; this.row++) {
                for (; this.column < this.COLUMNS; this.column++) {
                    if (this.grid[this.row][this.column][this.floor] === 0) {
                        return;
                    }
                }
                this.column = 0;
            }
            this.row = 0;
        }
    }

    removeLast(piece, rowsSet, columnsSet, floorsSet) {
        this.currPiece = this.pieces[piece];
        // console.debug(new Date().getTime() - this.start + " [msec] removeLast " + this.currPiece.name);
        // currPieceLayout = this.currPiece.getLayout();
        // getPosition
        this.row = this.currPiece.getRow();
        this.column = this.currPiece.getColumn();
        this.floor = this.currPiece.getFloor();
        // for debug- currPiece.setPosition(-1, -1);
        for (let i=0; i<this.currPiece.totalThisFill; i++) {
            this.grid[rowsSet[i]][columnsSet[i]][floorsSet[i]] = this.gridCopy[rowsSet[i]][columnsSet[i]][floorsSet[i]];
        }
        this.availInGrid += this.currPiece.totalThisFill;
    }

    solve() {
        this.start = new Date().getTime();
        this.triedPieces = 0;

        if (this.totalFillInGrid !== window.globalTotalFill) {
            let msg = "invalid config, grid " + this.totalFillInGrid + " pieces " + window.globalTotalFill + " - make sure #rows,columns,floors is in the correct order";
            this.allLines = 'Invalid input';
            console.error(msg);
            alert(msg);
        } else {
            this.put();
        }

        let msg = new Date().getTime() - this.start + " [msec] Ended. Tried pieces ~ " + this.triedPieces;
        console.log(msg);
        // triedPieces is an estimate, because of the back-track from the recursive put, note also the shuffle impacts the results.
        return this.allLines === '' ?
             "Found no solution" /* + ", a retry might be more lucky" */
            : this.allLines;
    }
}

export { Puzzle3d }
