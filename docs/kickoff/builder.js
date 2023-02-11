
class Builder {

    constructor(puzzle, input) {
        let lines = input.split('\n');
        let foundCells = 0;
        let header = lines[0];
        if (!header.startsWith("#")) {
            console.error("Found no grid header");
        }
        let declardColumns = parseInt(header.substring(1).split(",")[0]);
        let declardRows= parseInt(header.substring(1).split(",")[1]);
        puzzle.grid = new Array(declardRows).fill(0).map(_ => new Array(declardColumns).fill(0));

        for (let row = 0; row < lines.length; ++row) {
            let cellFound = false;
            let line = lines[row+1];
            let col;
            if (line === undefined) { // todo? enhance
                console.error('Row ' + (row+1) + ' is undefined');
                alert('Row ' + (row+1) + ' is undefined');
            }
            for (col = 0; col < line.length; ++col) {
                if (line[col] === 'X' || line[col] === 'x') {
                    cellFound = true;
                    ++ foundCells;
                } else {
                    puzzle.grid[row][col] = -1;
                    if (line[col] !== '_') {
                        console.warn("Invalid char " + line[col]);
                    }
                }
            }
            // columns = Math.max(columns, col); // todo handle
            // rows = row + 1;
            if (!cellFound) {
                break;
            }
        }
        // todo Parts2D_Examples.buildFromFile
    }
}

export { Builder };
