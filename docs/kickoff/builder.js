import { Piece } from "./piece.js";

class Builder {

    constructor(puzzle, input) {
        let lines = input.split('\n');
        let foundCells = 0;
        let header = lines[0];
        if (!header.startsWith("#")) {
            console.error("Found no grid header: #rows, columns");
            alert("Found no grid header: #rows, columns");
            throw new Error("Found no grid header: #rows, columns");
        }
        let declardRows= parseInt(header.substring(1).split(",")[0]);
        let declardColumns = parseInt(header.substring(1).split(",")[1]);
        if (isNaN(declardColumns) || isNaN(declardRows)) {
            console.error('Complete the template');
            alert('Complete the template');
        }
        puzzle.grid = new Array(declardRows).fill(0).map(_ => new Array(declardColumns).fill(0));
        puzzle.gridCopy = new Array(declardRows).fill(0).map(_ => new Array(declardColumns).fill(0));
        let rows = 0;
        let columns = 0;
        let gridFound = false;
        for (let row = 0; row < lines.length; ++row) {
            let cellFound = false;
            let line = lines[row+1];
            let col;
            if (line === undefined) { // todo? enhance
                console.error('Row ' + (row+1) + ' is undefined');
                alert('Row ' + (row+1) + ' is undefined');
            }
            else if (line.toLowerCase().startsWith("#end of grid")) {
                break;
            }
            for (col = 0; col < line.length; ++col) {
                if (line[col] === 'X' || line[col] === 'x') {
                    // Filled by cell 0 by def
                    cellFound = true;
                    gridFound = true;
                    ++foundCells;
                } else if (line[col] === 'o' || line[col] === 'O') {
                    puzzle.grid[row][col] = -10;
                    puzzle.gridCopy[row][col] = -10;
                    cellFound = true;
                    gridFound = true;
                    ++foundCells;
                } else {
                    // Empty
                    puzzle.grid[row][col] = -1;
                    puzzle.gridCopy[row][col] = -1;
                    if (line[col] !== '_' && line[col] !== '-') {
                        console.warn("Invalid char " + line[col]);
                    }
                }
            }
            columns = Math.max(columns, col);
            rows = row + 1;
            if (!cellFound) {
                break;
            }
        }
        rows -= 1;
        let i=1;
        if (!gridFound) {
            columns = declardColumns;
            rows = declardRows;
            foundCells = columns * rows;
        }
        else {
            i=declardRows+1;
        }
        puzzle.COLUMNS = columns; // this.grid.length;
        puzzle.ROWS = rows; // this.grid[0].length;
        console.log("Found " + (rows) + " rows, " + columns + " cols, with total of cells " + foundCells);
        puzzle.totalFillInGrid = foundCells;
        // Puzzle2D, line "// Parts2D_Examples"
        puzzle.names = '';
        let pieceIdx = 0;
        let pieceLines = [];
        let doneWithGrid = false;
        let unique = '';
        for (; i<lines.length; ++i) {
            let line = lines[i];
            if (line.toLowerCase().startsWith("#end of grid. pieces:poly")) {
                puzzle.PIECES = 12;
                puzzle.names = "LUFXYNWPZVTI";
                for (let i = 0; i < puzzle.PIECES/*allPieces.length*/; i++) {
                    let piece = i;
                    puzzle.piecesIndices.push(piece);
                    puzzle.pieces[i] = new Piece(piece, puzzle.allPieces[i], puzzle.rotations[i], puzzle.symmetric[i], puzzle.names.charAt(i));
                    puzzle.pieces[i].shuffle();
                }
                return;
            }
            else if (line.toLowerCase().startsWith("#end of grid")) {
                doneWithGrid = true;
                puzzle.PIECES = input.toLowerCase().split("#piece").length - 2;
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
                    let layout = new Array(pieceLines.length).fill(0).map(_ => new Array(9).fill(0));
                    for (let j = 0; j < pieceLines.length; ++j) {
                        layout[j] = [pieceLines[j].length];
                        for (let k = 0; k < pieceLines[j].length; ++k) {
                            layout[j][k] = 0;
                            if (pieceLines[j][k] === 'X' || pieceLines[j][k] === 'x') {
                                layout[j][k] = 1;
                            }
                            else if (pieceLines[j][k] === 'O' || pieceLines[j][k] === 'o') {
                                layout[j][k] = 2;
                            }
                            else if (pieceLines[j][k] !== ' ') {
                                console.warn('invalid char: ' + pieceLines[j][k]);
                            }
                        }
                    }
                    if (!puzzle.names[pieceIdx]) {
                        console.error('name undefined-Invalid input, line ' + line);
                        alert("name undefined, line " + line);
                        throw new Error('name undefined-Invalid input, line ' + line);
                    }
                    let globalTotalFill = window.globalTotalFill;
                    let fakePiece = new Piece(1000, layout, 4, 2, "_");
                    let rotations = fakePiece.calcRotations();
                    let fakePiece2 = new Piece(1000, layout, rotations, 2, "_");
                    let symmetric = fakePiece2.calcSymmetric();
                    if (unique === puzzle.names[pieceIdx]) {
                        rotations = 1;
                        symmetric = 1;
                        console.debug("unique=" + unique);
                    }
                    window.globalTotalFill = globalTotalFill;
                    puzzle.pieces[pieceIdx] = new Piece(pieceIdx, layout, rotations, symmetric, puzzle.names[pieceIdx]);
                    puzzle.pieces[pieceIdx].shuffle();
                    ++pieceIdx;
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
                // console.log("Parsing piece " + line["#Piece".length]);
            }
            else if (line.length > 0 && doneWithGrid) {
                if (line.toLowerCase().startsWith("#unique=")) {
                    if (unique !== '' && unique !== line["#unique=".length]) {
                        console.warn("Only single unique is possible");
                    } else {
                        unique = line["#unique=".length];
                    }
                }
                else {
                    pieceLines.push(line);
                }
            }
        }
        if (puzzle.PIECES !== puzzle.names.length) {
            console.error("wrong number of pieces, found " + puzzle.names.length + " declared " + puzzle.PIECES);
            alert("wrong number of pieces, found " + puzzle.names.length + " declared " + puzzle.PIECES);
            throw new Error("wrong number of pieces, found " + puzzle.names.length + " declared " + puzzle.PIECES);
        }
    }
}

export { Builder };
