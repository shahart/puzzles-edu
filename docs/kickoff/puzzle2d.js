import { Piece } from "./piece.js";
import { Builder } from "./builder.js";

window.globalTotalFill = 0;

class Puzzle2d {
    constructor(pieces, rows, columns, input) {
        if (columns > rows) {
            console.warn('You might want to switch dimensions.');
        }
        window.globalTotalFill = 0;
        this.isKatamino = false;
        this.isAquaBelle = false;
        this.allLines = '';
        this.start = new Date().getTime();
        this.PIECES = pieces;
        this.ROWS = rows;
        this.COLUMNS = columns;
        this.totalSolutions = 0;
        this.EXIT_SIGN = 100000;
        this.triedPieces = 0;
        this.solutionFound = false;
        this.row = 0;
        this.column = 0;
        this.piecesIndices = new Array();
        this.solution = new Array(this.PIECES).fill(0);
        this.pieces = [this.PIECES];
        this.names;
        if (rows !== rows || columns !== columns) { // NaN check, result of parseInt of Not-a-Number
            console.error('RangeError: Invalid array length');
            alert('RangeError: Invalid array length');
        }
        this.grid = new Array(rows).fill(0).map(_ => new Array(columns).fill(0));
        this.gridCopy = new Array(rows).fill(0).map(_ => new Array(columns).fill(0));
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
        this.totalFillInGrid = this.ROWS * this.COLUMNS;
        if (input) {
            new Builder(this, input);
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
                this.pieces[i] = new Piece(piece, this.allPieces[i], this.rotations[i], this.symmetric[i], this.names.charAt(i));
                this.pieces[i].shuffle(); //
            }
            for (let i = 0; i < this.grid.length; i++) {
                for (let j = 0; j < this.grid[i].length; j++) {
                    if (this.grid[i][j] === -1) {
                        this.totalFillInGrid--;
                    }
                }
            }
            console.log("Found " + this.ROWS + " rows, " + this.COLUMNS + " cols, with total of cells " + this.totalFillInGrid);
        }
        while (this.grid[0][this.column] === -1) {
            this.column++;
        }
        this.availInGrid = this.totalFillInGrid;
        // this.showGrid();
        // todo ChatGPT said to sort by piece size, for now do simple shuffle as in Java - irrelevant at Poly
        this.piecesIndices = this.piecesIndices.sort(function () { return Math.random() - 0.5; });
    }

    getGrid() {
        return this.grid;
    }

    showGrid() {
        // console.log(new Date().getTime() - this.start + " [msec] grid:"); // . Tried Pieces " + this.triedPieces); //  + " leftPieces " + this.piecesIndices.length);
        this.allLines = '';
        for (let i=0; i<this.ROWS; i++) {
            let line = "";
            if (i+1 < 10) {
                line += " " + (i+1);
            }
            else {
                line += (i+1);
            }
            line += "  ";
            for (let j=0;  j<this.COLUMNS; j++) {
                if (this.grid[i] === undefined || this.grid[i][j] === undefined) {
                    console.error("undefined grid cell, row " + i + " column " + j + " make sure `#rows,columns` is correct");
                    alert("undefined grid cell, row " + i + " column " + j + " make sure `#rows,columns` is correct");
                    throw new Error("undefined grid cell, row " + i + " column " + j + " make sure `#rows,columns` is correct");
                }
                if (this.grid[i][j] === -1) {
                    line += "*  ";
                // } else if (this.totalSolutions === 0) {
                } else if (this.grid[i][j] === 0) {
                    line += "-  ";
                } else if (this.grid[i][j] === -10) { // 2nd type of grid cell
                     line += "o  ";
                } else if (this.grid[i][j] === -15) { // good fish (AquaBelle)
                    line += "g  ";
                } else {
                    let ch = this.names.charAt(this.grid[i][j] - 1);
                    if (this.gridCopy[i][j] <= -10) {
                        ch = ch.toLowerCase();
                    }
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
        if (this.totalSolutions > 0 && this.totalSolutions < this.EXIT_SIGN) {
            this.solutionFound = true;
        }
    }

    canPut(rowsSet, columnsSet) {
        // console.debug(new Date().getTime() - this.start + " [msec] canPut " + this.currPiece.name + " R" + this.currPiece.currRotation);
        // this.showGrid();
        let setSoFar = 0;
        let columnj = this.column;
        let j = this.currPiece.getFirstSquarePos();
        let columnjj = this.column - j;
        let rowi;

        for (let i = 0; i < this.currPiece.totalThisFill; i++) {
            rowi = this.row + this.currPiece.getRowSet(setSoFar);
            columnj = columnjj + this.currPiece.getColumnSet(setSoFar); // column + currPiece.getColumnSet(setSoFar) - j;
            if (this.grid[rowi] === undefined) {
                return false;
            }
            let gridRowiColumnJ = this.grid[rowi][columnj];
            if (gridRowiColumnJ === undefined) {
                return false;
            }
            if (gridRowiColumnJ !== 0 &&
                gridRowiColumnJ !== -15 &&
                gridRowiColumnJ !== -10) {
                // not included as in comments at Puzzle2D.java
                return false;
            }
            let pieceVal = this.currPiece.getLayout() [this.currPiece.getRowSet(setSoFar)] [this.currPiece.getColumnSet(setSoFar)];
            if (((gridRowiColumnJ === 0 || gridRowiColumnJ === -15) && pieceVal === 1) || // regular cover, or on good fish
                ((gridRowiColumnJ === -10 || gridRowiColumnJ === 0) && pieceVal === 2))  // bubble on nothing/ bad-fish
            {
                rowsSet[setSoFar] = rowi;
                columnsSet[setSoFar] = columnj;
                setSoFar++;
            }
            else {
                return;
            }
            // not included as in comments at Puzzle2D
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
        if ((leftPieces === 0) ||
            (this.isKatamino && leftPieces === (window.globalTotalFill - this.totalFillInGrid) / 5)) { //  && availInGrid == 0) {
            this.totalSolutions++;
            console.log(new Date().getTime() - this.start + " [msec] Found a solution, click 'Show'");

            if (this.totalSolutions === 1) {
                this.showGrid();
                this.showPieces();
                this.totalSolutions = this.EXIT_SIGN;
                // let notif = new Date().getTime() - this.start + " [msec] Found a solution\n" + this.allLines;
                // console.log(this.allLines);
                // navigator.serviceWorker.register('sw.js');
                // Notification.requestPermission(function (result) {
                //     if (result === 'granted') {
                //         navigator.serviceWorker.ready.then(function (registration) {
                //             registration.showNotification(notif);
                //         });
                //     }
                // });
                // alert(notif);
                // throw new Error(notif); // todo? if !isMobile() /* at custom .html */ continue to find all solutions
            }
        }
        // this.showGrid();
        if (this.totalSolutions > 1) { // replacement of the above throw new Error(notif)
            return;
        }
        // firefox's dom.max_script_run_time = 20 sec
        let timeoutThreshold = 1500; // * 100
        if (new Date().getTime() - this.start > timeoutThreshold && timeoutThreshold > 0) {
            this.showGrid();
            let msg = new Date().toISOString() + ' Timeout, pieces per sec ' + Math.trunc(this.triedPieces / timeoutThreshold) + 'K';
            console.warn(msg);
            // alert(new Date().getTime() - this.start + " [msec] Timeout, check the browser's console");
            this.totalSolutions = this.EXIT_SIGN; // fake exit - throw new Error(new Date().toISOString() + " Timeout, check the browser's console");
            this.allLines = msg;
        }
        let rowsSet = new Array(5).fill(0); // TODO dynamic, per the rows in the piece
        let columnsSet = new Array(5).fill(0);

        // this.showGrid();
        for (let i=0; i< leftPieces; i++) {
            let piece = this.piecesIndices[i];
            this.currPiece = this.pieces[piece];
            for (let r = this.pieces[piece].getAvailRotations(); r > 0; r--, this.currPiece.rotate()) {
                // currPieceLayout = currPiece.getLayout();
                if (this.canPut(rowsSet, columnsSet)) {
                    this.piecesIndices.splice(i, 1);
                    this.solution[this.PIECES-leftPieces] = piece; // solution.add(piece);
                    this.putCurrPiece(rowsSet, columnsSet);

                    // "benchmarks", thanks to a Poly solution not found bug. avg of 3 runs, because of the shuffle. Timeout of 1.5 sec
                    // see speed.js: Poly #3,20
                    // Chrome:
                    //      Core i7, 8th Gen, 8665U (Q2 2019) - 220, 227, 217 -> 221k pieces per sec,
                    //      Core i7, 12th Gen, 1265U (Q1 2022) - 647, 581, 676 -> 634k
                    //      Mediatek MT6769T Helio G80 (Q1 2020) - 108, 119, 110-> ~112k >> 210k
                    //      Exynos 1380 (Q1 2023) -> 460k
                    //      iPhone 15 -> 1520k
                    // Firefox Focus:
                    //      i7, 8th - 80, 76, 73 -> ~76k
                    //      Cell - 79, 72, 90 -> ~80k
                    //      iPhone -> 1250k
                    // Samsung Internet:
                    //      no desktop.
                    //      Cell Helio G80 - 180, 176, 169  -> ~175k
                    //      Cell Exynos 1380 - 430k
                    // Safari:
                    //      iPhone - 1355k
                    //      iWatch9 - 20k

                    if (this.triedPieces % 50000 === 0 /* || leftPieces <= 2 */) { //
                        this.showGrid();
                        this.showPieces();
                    } //
                    // this.showPieces();

                    // this.showGrid();
                    this.put(); // the recurse
                    this.removeLast(piece, rowsSet, columnsSet);
                    // this.showGrid();
                    //solution.remove(piece); // like removeLast
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

    putCurrPiece(rowsSet, columnsSet) {
        // console.debug(new Date().getTime() - this.start + " [msec] putCurrPiece " + this.currPiece.name);
        this.currPiece.setPosition(this.row, this.column);
        let currIndex = this.currPiece.index+1;
        for (let i=0; i<this.currPiece.totalThisFill; i++) {
            this.grid[rowsSet[i]][columnsSet[i]] = currIndex;
        }
        // this.showGrid();
        // find next avail free position
        this.goForward();
        this.availInGrid -= this.currPiece.totalThisFill;
    }

    goForward() {
        // console.debug(new Date().getTime() - this.start + " [msec] goForward");
        for (; this.row<this.ROWS; this.row++) {
            for (; this.column<this.COLUMNS; this.column++) {
                if (this.grid[this.row][this.column] === 0 ||
                    this.grid[this.row][this.column] === -15 ||
                    this.grid[this.row][this.column] === -10) {
                    return;
                }
            }
            this.column = 0;
        }
    }

    removeLast(piece, rowsSet, columnsSet) {
        this.currPiece = this.pieces[piece];
        // console.debug(new Date().getTime() - this.start + " [msec] removeLast " + this.currPiece.name);
        // currPieceLayout = this.currPiece.getLayout();
        // getPosition
        this.row = this.currPiece.getRow();
        this.column = this.currPiece.getColumn();
        // for debug- currPiece.setPosition(-1, -1);
        for (let i=0; i<this.currPiece.totalThisFill; i++) {
            this.grid[rowsSet[i]][columnsSet[i]] = this.gridCopy[rowsSet[i]][columnsSet[i]];
        }
        this.availInGrid += this.currPiece.totalThisFill;
    }

    solve() {
        this.start = new Date().getTime();
        this.triedPieces = 0;

        if ((!this.isKatamino && this.totalFillInGrid !== window.globalTotalFill) ||
            ( this.isKatamino && this.totalFillInGrid > 60)) {
            let msg = "invalid config, grid " + this.totalFillInGrid + " pieces " + window.globalTotalFill + " - make sure #rows,columns is in the correct order";
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
        // triedPieces is an estimate, because of the back-track from the recursive put, note also the shuffle impacts the results.
        return this.allLines === '' ?
             "Found no solution" /* + ", a retry might be more lucky" */
            : this.allLines;
    }
}

export { Puzzle2d }
