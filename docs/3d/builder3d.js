import { Piece3d } from "./piece3d.js";

class Builder3d {

    constructor(puzzle, input) {
        let lines = input.split('\n');
        let foundCells = 0;
        let header = lines[0];
        if (!header.startsWith("#")) {
            console.error("Found no grid header: #rows, columns, floors");
            alert("Found no grid header: #rows, columns, floors");
            throw new Error("Found no grid header: #rows, columns, floors");
        }
        let declardRows = parseInt(header.substring(1).split(",")[0]);
        let declardColumns = parseInt(header.substring(1).split(",")[1]);
        let declardFloors = parseInt(header.substring(1).split(",")[2]);
        if (isNaN(declardColumns) || isNaN(declardRows) || isNaN(declardFloors)) {
            this.calcRows(lines);
            // console.error('Complete the template');
            // alert('Complete the template');
            declardColumns = this.declaredColumns;
            declardRows = this.declaredRows;
            declardFloors = this.declaredFloors;
        }

        puzzle.grid = new Array(declardRows).fill(0).map(_ => new Array(declardColumns).fill(0).map(_ => new Array(declardFloors).fill(0)));
        puzzle.gridCopy = new Array(declardRows).fill(0).map(_ => new Array(declardColumns).fill(0).map(_ => new Array(declardFloors).fill(0)));
        let rows = 0;
        let columns = 0;
        let floors = 0;
        let floor = 0;
        let r = 0;
        let gridFound = false;
        for (let row = 0; row < lines.length; ++row) {
            let line = lines[row+1];
            let col;
            if (line === undefined) { // todo? enhance
                console.error('Line ' + (row+1) + ' is undefined');
                alert('Line ' + (row+1) + ' is undefined');
            }
            else if (line.toLowerCase().startsWith("#end of grid")) {
                break;
            }
            for (col = 0; col < line.length; ++col) {
                if (line[col] === 'X' || line[col] === 'x') {
                    // Filled by cell 0 by def
                    gridFound = true;
                    ++foundCells;
                } else {
                    // Empty
                    puzzle.grid[r][col][floor] = -1;
                    puzzle.gridCopy[r][col][floor] = -1;
                    if (line[col] !== '_' && line[col] !== '-') {
                        console.warn("Invalid char '" + line[col] + "'");
                    }
                }
            }
            columns = Math.max(columns, col);
            rows = Math.max(rows, r);
            ++r;
            if (line === '') {
                ++floor;
                r = 0;
            }
        }
        let i=1;
        if (!gridFound) {
            columns = declardColumns;
            rows = declardRows;
            floors = declardFloors;
            foundCells = columns * rows * floors;
        }
        else {
            i=declardRows+1;
        }
        puzzle.COLUMNS = columns; // this.grid.length;
        puzzle.ROWS = rows; // this.grid[0].length;
        floors = declardFloors;
        puzzle.FLOORS = floors;
        console.log("Found " + (rows) + " rows, " + columns + " cols, " + floors + " floors, with total of cells " + foundCells);
        puzzle.totalFillInGrid = foundCells;
        // Puzzle2D, line "// Parts2D_Examples"
        puzzle.names = '';
        let pieceIdx = 0;
        let pieceLines = [];
        let doneWithGrid = false;
        let currMultiplier = 1;
        for (; i<lines.length; ++i) {
            let line = lines[i];
            if (line.toLowerCase().startsWith("#end of grid. pieces:poly")) {
                puzzle.PIECES = 12;
                puzzle.names = "LUFXYNWPZVTI";
                for (let i = 0; i < puzzle.PIECES/*allPieces.length*/; i++) {
                    let piece = i;
                    puzzle.piecesIndices.push(piece);

                    // transform 2d into 3d (with one floor)
                    let rows = puzzle.allPieces[i].length;
                    let cols = 1;
                    for (let j=0; j<puzzle.allPieces[i].length; ++j) {
                        cols = Math.max(cols, puzzle.allPieces[i][j].length);
                    }
                    let pieceMax = Math.max(rows, cols);
                    let layout = new Array(pieceMax).fill(0).map(_ => new Array(pieceMax).fill(0).map(_ => new Array(pieceMax).fill(0)));
                    for (let j=0; j<puzzle.allPieces[i].length; ++j) {
                        for (let k=0; k<puzzle.allPieces[i][j].length; ++k) {
                            if (puzzle.allPieces[i][j][k] === 1) {
                                layout[0][j][k] = 1;
                            }
                        }
                    }

                    puzzle.pieces[i] = new Piece3d(piece, layout, puzzle.names.charAt(i));
                    // puzzle.pieces[i].shuffle(); //
                }
                return;
            }
            else if (line.toLowerCase().startsWith("#end of grid")) {
                doneWithGrid = true;
                puzzle.PIECES = input.toLowerCase().split("#piece").length - 2 + this.countXn(lines);
                console.log("Found pieces " + puzzle.PIECES);
                console.debug("(default is Rotations=4, Has no symmetry)")
                if (! puzzle.PIECES) {
                    console.error("NaN-Found no PiecesX");
                    alert("NaN-Found no PiecesX");
                    throw new Error('NaN-Found no PiecesX');
                }
                puzzle.pieces = [puzzle.PIECES];
                for (let k=0; k<puzzle.PIECES; ++k) {
                    puzzle.piecesIndices.push(k);
                }
            }
            else if (line.toLowerCase().startsWith("#piece")) {
                if (pieceLines.length >= 1) {

                    let pieceMaxColumns = 1;
                    let pieceMaxRows = 1;
                    let pieceFloors = 1;
                    let pieceRows = 1;
                    let prevPieceRowStart = 0;
                    for (let pieceRow = 0; pieceRow < pieceLines.length; ++pieceRow) {
                        let pieceLine = pieceLines[pieceRow];
                        pieceMaxColumns = Math.max(pieceLine.length, pieceMaxColumns);
                        if (pieceLine === '2' || pieceLine === '3' || pieceLine === '4') { // todo make it better
                            ++pieceFloors;
                            pieceRows = pieceRow - prevPieceRowStart;
                            pieceMaxRows = Math.max(pieceRows, pieceMaxRows);
                            prevPieceRowStart = pieceRow;
                        }
                    }
                    pieceRows = pieceLines.length - prevPieceRowStart;
                    pieceMaxRows = Math.max(pieceRows, pieceMaxRows);

                    let pieceMax = Math.max(pieceMaxRows, pieceMaxColumns);
                    pieceMax = Math.max(pieceMax, pieceFloors);

                    let layout = new Array(pieceMax).fill(0).map(_ => new Array(pieceMax).fill(0).map(_ => new Array(pieceMax).fill(0)));

                    floor = 0;
                    prevPieceRowStart = 0;
                    for (let j = 0; j < pieceLines.length; ++j) {
                        let pieceLine = pieceLines[j];
                        if (pieceLine === '2' || pieceLine === '3' || pieceLine === '4') { // todo make it better
                            ++floor;
                            prevPieceRowStart = j+1;
                        }
                        else {
                            for (let k = 0; k < pieceLine.length; ++k) {
                                if (pieceLine[k] === 'X' || pieceLine[k] === 'x') {
                                    layout[floor][j-prevPieceRowStart][k] = 1;
                                }
                            }
                        }
                    }

                    if (!puzzle.names[pieceIdx]) {
                        console.error('name undefined-Invalid input, line ' + line);
                        alert("name undefined, line " + line);
                        throw new Error('name undefined-Invalid input, line ' + line);
                    }
                    let globalTotalFill = window.globalTotalFill;
                    window.globalTotalFill = globalTotalFill;
                    for (let z = 0; z < currMultiplier; ++z) {
                        puzzle.pieces[pieceIdx] = new Piece3d(pieceIdx + z, layout, puzzle.names[pieceIdx]);
                        // puzzle.pieces[pieceIdx].shuffle(); //
                        ++pieceIdx;
                    }
                    currMultiplier = 1;
                    pieceLines = [];
                }
                if (line.toLowerCase().startsWith("#piece-end")) {
                    break;
                }
                if (puzzle.names.indexOf(line["#Piece".length]) >= 0) {
                    let msg = "Piece " + line["#Piece".length] + " already defined";
                    console.error(msg);
                    alert(msg);
                    throw new Error(msg);
                }
                puzzle.names += line["#Piece".length];
                currMultiplier = 1;
                if (line.indexOf(" x") > 1) {
                    currMultiplier = parseInt(line.substring(line.indexOf(" x")+2));
                }
                for (let z = 1; z < currMultiplier; ++z) {
                    puzzle.names += String.fromCharCode(line["#Piece".length].charCodeAt(0) + z);
                }
                console.log("Parsing piece " + line["#Piece".length]);
            }
            else if (line.length > 0 && doneWithGrid) {
                pieceLines.push(line);
            }
        }
        if (puzzle.PIECES !== puzzle.names.length) {
            console.error("wrong number of pieces, found " + puzzle.names.length + " declared " + puzzle.PIECES);
            alert("wrong number of pieces, found " + puzzle.names.length + " declared " + puzzle.PIECES);
            throw new Error("wrong number of pieces, found " + puzzle.names.length + " declared " + puzzle.PIECES);
        }
    }

    calcRows(lines) {
        this.declaredRows = 0;
        this.declaredColumns = 0;
        let row = 0;
        for (; row < lines.length; ++row) {
            let line = lines[row+1];
            if (line !== undefined && line.toLowerCase().startsWith("#end of grid")) {
                break;
            }
            this.declaredColumns = Math.max(this.declaredColumns, line.length);
            this.declaredRows = row + 1;
        }
        if (lines[row] === '') {
            this.declaredRows -= 1;
        }
        console.debug("Found " + this.declaredRows + " rows, " + this.declaredColumns + " cols");
    }

    countXn(lines) {
        let res = 0;
        for (let i = 0; i < lines.length; ++i) {
            if (lines[i].toLowerCase().startsWith("#piece") && lines[i].indexOf(" x") > 1) {
                res += parseInt(lines[i].substring(lines[i].indexOf(" x")+2)) - 1;
            }
        }
        return res;
    }

}

export { Builder3d };
