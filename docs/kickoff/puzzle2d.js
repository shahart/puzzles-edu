import { Piece } from "./piece.js";

window.globalTotalFill = 0;

class Puzzle2d {
    constructor(pieces, rows, columns, input) {
        if (columns > rows) {
            console.warn('You might want to switch dimensions.');
        }
        window.globalTotalFill = 0;
        this.allLines = '';
        this.start = new Date().getTime();
        this.PIECES = pieces; // 3; // 12 Poly // 10 6x6
        this.ROWS = rows;
        this.COLUMNS = columns;
        this.totalSolutions = 0;
        this.triedPieces = 0;
        this.solutionFound = false;
        this.row = 0;
        this.column = 0;
        this.piecesIndices = new Array();
        this.solution = new Array(this.PIECES).fill(0);
        this.pieces = [this.PIECES];
        this.names;
        this.grid = new Array(rows).fill(0).map(_ => new Array(columns).fill(0));
        this.currPiece;
        this.totalFillInGrid = 0;
        this.availInGrid = 0;
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

        if (pieces === 3) {
            this.allPieces = [
                /* no solution - test
                [[0,0,1],[1,1,1],[1]],
                [[1,1],[1]],
                [[1]]
                 */
                 [[0,1]],
                 [[1,1],[1]],
                 [[1,1,1],[1],[1]]
            ];
        }
        else if (pieces === 10) {
        this.allPieces = [
            // 6x6
            [[1, 1], [1], [1, 1]],
            [[0, 1, 1], [1, 1]],
            [[1, 1, 1], [0, 1]],
            [[0, 1, 1], [1, 1]],
            [[1, 1], [1]],
            [[1, 1]],
            [[0, 0, 1], [1, 1, 1]],
            [[1, 1], [1, 1]],
            [[1, 1]],
            [[1, 1, 1, 1]],

            // my first Ascii
            /*
                       // [[0,1]],
                       // [[1,1],[1]],
                       // [[1,1,1],[1],[1]]
            */
        ];
        }

        this.names = "ABCDEFGHIJ"; // 6x6
        this.rotations = [ 4,4,4/*4*/,4,4,2,4,1,2,2 ];
        this.symmetric = [ 1,2,1/*2*/,2,1,1,2,1,1,1 ];

        // Poly
        if (pieces === 12) {
            console.log("Polyominos");
            this.names = "LUFXYNWPZVTI"; // Poly
            this.rotations = [4, 4, 2/*4*/, 1, 4, 4, 4, 4, 2, 4, 4, 2];
            this.symmetric = [2, 1, 1/*2*/, 1, 2, 2, 1, 2, 2, 1, 1, 1];
        }
        else if (pieces === 3) {
            console.log("3x3");
            this.names = "XYZ";
            this.rotations = [4, 4, 1];
            this.symmetric = [2, 1, 1];
        }

        if (this.ROWS === this.COLUMNS && this.ROWS === 8) {
            this.rotations[2] = 1;
        }

        if (this.allPieces.length > this.PIECES) {
            console.warn("using first pieces from the whole set");
        }

        // prepare grid (the board)

        if (input) {
            this.buildFromFile(input);
        }
        else {
            for (let i=0; i<this.PIECES/*allPieces.length*/; i++) {
                let piece = i;
                this.piecesIndices.push(piece);
                this.pieces[i]= (new Piece(piece, this.allPieces[i], this.rotations[i], this.symmetric[i], this.names.charAt(i)));
            }
            if (this.ROWS === this.COLUMNS && this.ROWS === 8) {
                this.grid[0][0]=-1;
                this.grid[7][0]=-1;
                this.grid[0][7]=-1;
                this.grid[7][7]=-1;
            }
            while (this.grid[0][this.column] === -1) {
                this.column++;
            }
        }

        this.totalFillInGrid = this.ROWS * this.COLUMNS;
        for (let i=0; i<this.grid.length; i++) {
            for (let j = 0; j < this.grid[i].length; j++) {
                if (this.grid[i][j] === -1) {
                    this.totalFillInGrid--;
                }
            }
        }

        this.availInGrid = this.totalFillInGrid;
        console.info("Found " + this.ROWS + " rows, " + this.COLUMNS + " cols, with total of cells " + this.availInGrid);
        // this.showGrid();

        // todo ChatGPT said to sort by piece size, for now do simple shuffle as in Java - irrelevant at Poly
        this.piecesIndices = this.piecesIndices.sort(function () { return Math.random() - 0.5; });
    }

    showGrid() {
        console.info(new Date().getTime() - this.start + " [msec] showGrid. Tried Pieces " + this.triedPieces + " leftPieces " + this.piecesIndices.length);
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
                if (this.grid[i][j] === -1) {
                    line += "*  ";
                // } else if (this.totalSolutions === 0) {
                } else if (this.grid[i][j] === 0) {
                    line += "-  ";
                } else {
                    line += this.names.charAt(this.grid[i][j] - 1) + "  ";
                }
            }
            console.log(line);
            if (!this.solutionFound) {
                this.allLines += line + "\n";
            }
        }
        this.solutionFound = true;
    }

    buildFromFile(input) {
        // next todo
        console.debug('buildFromFile, lines ' + input.split('\n').length);
    }

    canPut(rowsSet, columnsSet) {
        // console.debug(new Date().getTime() - this.start + " [msec] canPut " + this.currPiece.name + " R" + this.currPiece.currRotation);
        let setSoFar = 0;
        let columnj = this.column;
        let j = this.currPiece.getFirstSquarePos();
        let columnjj = this.column - j;
        let rowi;

        for (let i=0; i<this.currPiece.totalThisFill; i++) {
            rowi = this.row+this.currPiece.getRowSet(setSoFar);
            columnj = columnjj+this.currPiece.getColumnSet(setSoFar); // column+currPiece.getColumnSet(setSoFar)-j;
            if (this.grid[rowi] === undefined) {
                return false;
            }
            let gridRowiColumnJ = this.grid[rowi][columnj];
            if (gridRowiColumnJ === undefined) {
                return false;
            }
            if (gridRowiColumnJ !== 0) {
                // not included as in comments at Puzzle2D.java
                return false;
            }
            else {
                rowsSet[setSoFar] = rowi;
                columnsSet[setSoFar] = columnj;
                setSoFar++;
            }
            // not included as in comments at Puzzle2D
        }
        this.triedPieces++;
        return true;
    }

    put() {
        let leftPieces = this.piecesIndices.length;
        // console.debug(new Date().getTime() - this.start + " [msec] put, leftPieces " + leftPieces);
        if (leftPieces === 0) { //  && availInGrid == 0) {
            this.totalSolutions++;
            console.log(new Date().getTime() - this.start + " [msec] Found a solution");

            if (this.totalSolutions === 1) {
                this.showGrid();
                this.showPieces();
                this.totalSolutions = 100000;
                let notif = new Date().getTime() - this.start + " [msec] Found a solution, check the browser's console \n" + this.allLines;
                navigator.serviceWorker.register('sw.js');
                Notification.requestPermission(function (result) {
                    if (result === 'granted') {
                        navigator.serviceWorker.ready.then(function (registration) {
                            registration.showNotification(notif);
                        });
                    }
                });
                alert(notif);
                // throw new Error(notif); // todo? if !isMobile() /* at custom .html */ continue to find all solutions
            }
        }

        // this.showGrid();

        // firefox's dom.max_script_run_time = 20 sec // todo async..
        if (new Date().getTime() - this.start > 1500) {
            this.showGrid();
            console.warn(new Date().toISOString() + " Timeout");
            alert(new Date().getTime() - this.start + " [msec] Timeout, check the browser's console");
            throw new Error(new Date().toISOString() + " Timeout, check the browser's console");
        }

        if (this.totalSolutions > 1) { // replacement of the above throw new Error(notif)
            return;
        }

        let rowsSet = new Array(5).fill(0); // TODO dynamic, per the rows in the piece
        let columnsSet = new Array(5).fill(0);

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
                    // Chrome:
                    //      Core i7, 8th Gen, 8665U (Q2 2019) - 220, 227, 217 -> 221k pieces per sec,
                    //      Mediatek MT6769T Helio G80 (Q1 2020) - 108, 119, 110-> ~112k
                    // Firefox Focus:
                    //      i7 - 80, 76, 73 -> ~76k
                    //      Cell - 79, 72, 90 -> ~80k
                    // Samsung Internet:
                    //      no desktop.
                    //      Cell - 180, 176, 169  -> ~175k

                    if (this.triedPieces % 50000 === 0 /* || leftPieces <= 2 */) {
                        this.showGrid();
                        this.showPieces();
                    }
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
                if (this.grid[this.row][this.column] === 0) {
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
            this.grid[rowsSet[i]][columnsSet[i]] = 0;
        }
        /*
                for (let i=0, rowi=row; i<currPieceLayout.length; i++, rowi++) {
                    let columnj = column-this.currPiece.firstSquarePos;
                    for (let j=0; j<currPieceLayout[i].length; j++, columnj++)
                        if (currPieceLayout[i][j] === 1)
                            grid[rowi][columnj] = 0;
                }
        */
        this.availInGrid += this.currPiece.totalThisFill;
    }

    solve() {
        this.start = new Date().getTime();
        this.triedPieces = 0;
        console.log(new Date().toISOString());
        console.log(new Date().getTime() - this.start + " [msec] Started");

        if (this.totalFillInGrid !== window.globalTotalFill) {
            console.error("invalid config, grid " + this.totalFillInGrid + " pieces " + window.globalTotalFill);
        } else {
            this.put();
        }

        console.log(new Date().getTime() - this.start + " [msec] Ended. Tried pieces ~ " + this.triedPieces);
        // triedPieces is an estimate, because of the back-track from the recursive put, note also the shuffle impacts the results.
        return this.allLines;
    }
}

export { Puzzle2d }
