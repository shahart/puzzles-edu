import { Piece } from "./piece.js";

class Builder {

    constructor(puzzle, input) {
        let lines = input.split('\n');
        let foundCells = 0;
        let header = lines[0];
        if (!header.startsWith("#")) {
            console.error("Found no grid header");
        }
        let declardRows= parseInt(header.substring(1).split(",")[0]);
        let declardColumns = parseInt(header.substring(1).split(",")[1]);
        puzzle.grid = new Array(declardRows).fill(0).map(_ => new Array(declardColumns).fill(0));
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
            else if (line.startsWith("#end of grid. Pieces")) {
                break;
            }
            for (col = 0; col < line.length; ++col) {
                if (line[col] === 'X' || line[col] === 'x') {
                    cellFound = true;
                    gridFound = true;
                    ++ foundCells;
                } else {
                    puzzle.grid[row][col] = -1;
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
        let rotations = 4;
        let symmetric = 2;
        let pieceLines = [];
        let doneWithGrid = false;
        for (; i<lines.length; ++i) {
            let line = lines[i];
            if (line.startsWith("#end of grid. Pieces:Poly")) {
                puzzle.PIECES = 12;
                puzzle.names = "LUFXYNWPZVTI";
                for (let i = 0; i < puzzle.PIECES/*allPieces.length*/; i++) {
                    let piece = i;
                    puzzle.piecesIndices.push(piece);
                    puzzle.pieces[i] = new Piece(piece, puzzle.allPieces[i], puzzle.rotations[i], puzzle.symmetric[i], puzzle.names.charAt(i));
                }
                return;
            }
            else if (line.startsWith("#end of grid. Pieces")) {
                doneWithGrid = true;
                puzzle.PIECES = parseInt(line.substring("#end of grid. Pieces".length));
                console.log("Found pieces " + puzzle.PIECES);
                if (! puzzle.PIECES) {
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
                            layout[j][k] = pieceLines[j][k] !== ' ' ? 1 : 0;
                        }
                    }
                    if (!puzzle.names[pieceIdx]) {
                        alert("name undefined, line " + line);
                        throw new Error('name undefined-Invalid input, line ' + line);
                    }
                    puzzle.pieces[pieceIdx] = new Piece(pieceIdx, layout, rotations, symmetric, puzzle.names[pieceIdx]);
                    ++pieceIdx;
                    pieceLines = [];
                    rotations = 4;
                    symmetric = 2;
                }
                let s = line.split(" ");
                if (s.length > 1) {
                    if (s[1].startsWith("R")) {
                        rotations = parseInt(s[1].substring("R".length));
                        if (rotations < 1 || rotations > 4) {
                            console.error("Wrong rotations 1..4");
                        }
                    }
                    else if (s[1].startsWith("S")) {
                        symmetric = parseInt(s[1].substring("S".length));
                    }
                    if (s.length > 2 && s[2].startsWith("S")) {
                        symmetric = parseInt(s[2].substring("S".length));
                    }
                    if (symmetric !== 1 && symmetric !== 2) {
                        console.error("Wrong symmetric 1..2");
                    }
                }
                if (line.toLowerCase().startsWith("#piece-end")) {
                    break;
                }
                puzzle.names += line["#Piece".length];
                // console.log("Parsing piece " + line["#Piece".length]);
            }
            else if (line.length > 0 && doneWithGrid) {
                if (line.toLowerCase().startsWith("#unique=")) {
                    // let uniqueId = line["#unique=".length];
                    console.log(line + ">>make sure you have R1 S1");
                    // rotations = 1; // todo handle, don't require to add R1 S1
                    // symmetric = 1;
                }
                else {
                    pieceLines.push(line);
                }
            }
        }
    }
}

export { Builder };
