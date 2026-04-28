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
            [[[1],[1],[1],[1,1]]],        //4-2 (2)   L sym = indicates it is symmetric // TODO write flip
            [[[1,1],[1],[1,1]]],          //4-1 (5)   U
            [[[0,1,1],[1,1],[0,1]]],      //4-2 (9)   F sym - used to eliminate "dups"
            [[[0,1],[1,1,1],[0,1]]],      //1-1 (6)   X
            [[[1,1,1,1],[0,0,1]]],        //4-2 (3)   Y sym
            [[[0,1],[1,1],[1],[1]]],      //4-2 (12)  N sym
            [[[0,0,1],[0,1,1],[1,1]]],    //4-1 (7)   W
            [[[1],[1,1],[1,1]]],          //4-2 (4)   P sym
            [[[0,0,1],[1,1,1],[1]]],      //2-2 (11)  Z sym ??
            [[[0,0,1],[0,0,1],[1,1,1]]],  //4-1 (10)  V
            [[[0,0,1],[1,1,1],[0,0,1]]],  //4-1 (8)   T
            [[[1,1,1,1,1]]]               //2-1 (1)   I
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
                // Transform the default (ragged) definitions into a cubic 3D array so `Piece3d` rotations work.
                // Source format here is `[row][col][floor]` (ragged in the floor dimension).
                const src = this.allPieces[i];
                let srcRows = src.length;
                let srcCols = 1;
                let srcFloors = 1;
                for (let r = 0; r < srcRows; r++) {
                    srcCols = Math.max(srcCols, src[r].length);
                    for (let c = 0; c < src[r].length; c++) {
                        srcFloors = Math.max(srcFloors, src[r][c].length);
                    }
                }
                const pieceMax = Math.max(srcRows, srcCols, srcFloors);
                const layout = new Array(pieceMax).fill(0).map(_ =>
                    new Array(pieceMax).fill(0).map(_ =>
                        new Array(pieceMax).fill(0)
                    )
                );
                for (let r = 0; r < srcRows; r++) {
                    for (let c = 0; c < src[r].length; c++) {
                        for (let f = 0; f < src[r][c].length; f++) {
                            if (src[r][c][f] === 1) {
                                // `Piece3d` expects `[floor][row][col]`
                                layout[f][r][c] = 1;
                            }
                        }
                    }
                }
                this.pieces[i] = new Piece3d(piece, layout, this.names.charAt(i));
                // this.pieces[i].shuffle();
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
        this.availInGrid = this.totalFillInGrid;
        // this.piecesIndices = this.piecesIndices.sort(function () { return Math.random() - 0.5; });
    }

    showGrid() {
        if (!window.__PUZZLES_EDU_TEST__) {
            console.log(new Date().getTime() - this.start + " [msec] grid:"); // . Tried Pieces " + this.triedPieces); //  + " leftPieces " + this.piecesIndices.length);
        }
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
                    if (!window.__PUZZLES_EDU_TEST__) {
                        console.log(line);
                    }
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
        if (!window.__PUZZLES_EDU_TEST__) {
            console.debug(new Date().getTime() - this.start + " [msec] canPut " + this.currPiece.name + " R" + this.currPiece.currRotation);
        }
        let setSoFar = 0;
        let columnj;
        let floorj;
        let rowi;
        let coversAnchor = false;

        // Normalize the rotation so the minimum occupied (row/col/floor) aligns to the anchor cell.
        let minRow = Infinity;
        let minCol = Infinity;
        let minFloor = Infinity;
        for (let i = 0; i < this.currPiece.totalThisFill; i++) {
            minRow = Math.min(minRow, this.currPiece.getRowSet(i));
            minCol = Math.min(minCol, this.currPiece.getColumnSet(i));
            minFloor = Math.min(minFloor, this.currPiece.getFloorSet(i));
        }

        for (let i = 0; i < this.currPiece.totalThisFill; i++) {
            rowi = this.row + (this.currPiece.getRowSet(setSoFar) - minRow);
            columnj = this.column + (this.currPiece.getColumnSet(setSoFar) - minCol);
            floorj = this.floor + (this.currPiece.getFloorSet(setSoFar) - minFloor);
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
            // `Piece3d` layout indexing is [floor][row][col]
            let pieceVal = this.currPiece.getLayout()[this.currPiece.getFloorSet(setSoFar)][this.currPiece.getRowSet(setSoFar)][this.currPiece.getColumnSet(setSoFar)];
            if (gridRowiColumnJk === 0 && pieceVal === 1) {
                if (rowi === this.row && columnj === this.column && floorj === this.floor) {
                    coversAnchor = true;
                }
                rowsSet[setSoFar] = rowi;
                columnsSet[setSoFar] = columnj;
                floorsSet[setSoFar] = floorj;
                setSoFar++;
            }
            else {
                return;
            }
        }
        // Anchor coverage is guaranteed by the normalization step (min occupied cell aligns to the anchor).
        this.triedPieces++;
        return true;
    }

    put() {
        if (this.totalSolutions === this.EXIT_SIGN) {
            return;
        }
        let leftPieces = this.piecesIndices.length;
        if (!window.__PUZZLES_EDU_TEST__) {
            console.debug(new Date().getTime() - this.start + " [msec] put, leftPieces " + leftPieces);
        }
        if ((leftPieces === 0)) {
            this.totalSolutions++;
            console.log(new Date().getTime() - this.start + " [msec] Found a solution, click 'Show'");

            if (this.totalSolutions === 1) {
                this.showGrid();
                this.showPieces();
                this.totalSolutions = this.EXIT_SIGN;
                return;
            }
        }
        if (this.totalSolutions === this.EXIT_SIGN) {
            return;
        }
        this.showGrid();
        if (this.totalSolutions > 1) { // replacement of the above throw new Error(notif)
            return;
        }
        let timeoutThreshold = 0;
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

            // 3D rotations are required for the classic 3×4×5 packing (pieces must stand across floors).
            // To keep runtime reasonable, skip duplicate rotations (many are equivalent for symmetric parts).
            const rotations = this.currPiece.getAvailRotations();
            const seen = new Set();
            for (let r = 0; r < rotations; r++) {
                this.currPiece.currRotation = r;

                // Build a normalized signature of occupied coordinates for this rotation.
                let minR = Infinity, minC = Infinity, minF = Infinity;
                for (let t = 0; t < this.currPiece.totalThisFill; t++) {
                    minR = Math.min(minR, this.currPiece.getRowSet(t));
                    minC = Math.min(minC, this.currPiece.getColumnSet(t));
                    minF = Math.min(minF, this.currPiece.getFloorSet(t));
                }
                let sig = '';
                for (let t = 0; t < this.currPiece.totalThisFill; t++) {
                    sig += (this.currPiece.getRowSet(t) - minR) + ',' +
                        (this.currPiece.getColumnSet(t) - minC) + ',' +
                        (this.currPiece.getFloorSet(t) - minF) + ';';
                }
                if (seen.has(sig)) {
                    continue;
                }
                seen.add(sig);

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
        if (!window.__PUZZLES_EDU_TEST__) {
            console.debug(new Date().getTime() - this.start + " [msec] putCurrPiece " + this.currPiece.name);
        }
        this.currPiece.setPosition(this.row, this.column, this.floor);
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
        if (!window.__PUZZLES_EDU_TEST__) {
            console.debug(new Date().getTime() - this.start + " [msec] goForward");
        }
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
        if (!window.__PUZZLES_EDU_TEST__) {
            console.debug(new Date().getTime() - this.start + " [msec] removeLast " + this.currPiece.name);
        }
        // currPieceLayout = this.currPiece.getLayout();
        // getPosition
        this.row = this.currPiece.getRow();
        this.column = this.currPiece.getColumn();
        this.floor = this.currPiece.getFloor();
        // for debug- currPiece.setPosition(-1, -1, -1);
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
            try {
                this.put();
            } catch (err) {
                console.error(err.stack);
            }
        }

        let msg = new Date().getTime() - this.start + " [msec] Ended. Tried pieces ~ " + this.triedPieces;
        console.log(msg);
        // triedPieces is an estimate, because of the back-track from the recursive put.
        // `showGrid()` populates `allLines` even for intermediate states; only treat it as a solution if we actually found one.
        if (!this.solutionFound && this.allLines !== 'Invalid input') {
            this.allLines = '';
        }
        return this.allLines === '' ? "Found no solution" : this.allLines;
    }
}

export { Puzzle3d }
