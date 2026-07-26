import { ExactCover3d } from "./exactCover3d.js";

const POLY_NAMES = "LUFXYNWPZVTI";
const POLY_LAYOUTS = [
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

class PuzzleInputError extends Error {
    constructor(message, lineNumber) {
        super(lineNumber ? `Line ${lineNumber}: ${message}` : message);
        this.name = "PuzzleInputError";
        this.lineNumber = lineNumber;
    }
}

function coordinateKey(row, column, floor) {
    return `${row},${column},${floor}`;
}

function normalizeCoordinates(cells) {
    const minimum = [Infinity, Infinity, Infinity];
    for (const cell of cells) {
        minimum[0] = Math.min(minimum[0], cell[0]);
        minimum[1] = Math.min(minimum[1], cell[1]);
        minimum[2] = Math.min(minimum[2], cell[2]);
    }
    const normalized = cells.map((cell) => [
        cell[0] - minimum[0],
        cell[1] - minimum[1],
        cell[2] - minimum[2]
    ]);
    normalized.sort((left, right) =>
        left[0] - right[0] || left[1] - right[1] || left[2] - right[2]
    );
    return normalized;
}

function permutationSign(permutation) {
    let inversions = 0;
    for (let first = 0; first < permutation.length; first++) {
        for (let second = first + 1; second < permutation.length; second++) {
            if (permutation[first] > permutation[second]) {
                inversions++;
            }
        }
    }
    return inversions % 2 === 0 ? 1 : -1;
}

function buildOrientations(cells) {
    const permutations = [
        [0, 1, 2], [0, 2, 1], [1, 0, 2],
        [1, 2, 0], [2, 0, 1], [2, 1, 0]
    ];
    const orientations = [];
    const seen = new Set();

    for (const permutation of permutations) {
        const parity = permutationSign(permutation);
        for (const firstSign of [-1, 1]) {
            for (const secondSign of [-1, 1]) {
                for (const thirdSign of [-1, 1]) {
                    if (parity * firstSign * secondSign * thirdSign !== 1) {
                        continue;
                    }
                    const signs = [firstSign, secondSign, thirdSign];
                    const transformed = cells.map((cell) =>
                        signs.map((sign, axis) => sign * cell[permutation[axis]])
                    );
                    const normalized = normalizeCoordinates(transformed);
                    const key = normalized.map((cell) => cell.join(",")).join(";");
                    if (!seen.has(key)) {
                        seen.add(key);
                        orientations.push(normalized);
                    }
                }
            }
        }
    }
    return orientations;
}

function parseHeader(line) {
    const match = line.match(/^#\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)(?:\s|$)/);
    if (!match) {
        throw new PuzzleInputError("expected #rows,columns,floors", 1);
    }
    const dimensions = match.slice(1, 4).map(Number);
    if (dimensions.some((dimension) => !Number.isInteger(dimension) || dimension < 1)) {
        throw new PuzzleInputError("dimensions must be positive integers", 1);
    }
    return dimensions;
}

function parseExplicitGrid(lines, rows, columns, floors, startLine) {
    const usableCells = [];
    let floor = 0;
    let row = 0;

    for (let index = 0; index < lines.length; index++) {
        const line = lines[index];
        if (line === "") {
            if (row > 0) {
                if (row !== rows) {
                    throw new PuzzleInputError(`grid floor has ${row} rows; expected ${rows}`, startLine + index);
                }
                floor++;
                row = 0;
            }
            continue;
        }
        if (floor >= floors) {
            throw new PuzzleInputError(`grid has more than ${floors} floors`, startLine + index);
        }
        if (row >= rows) {
            throw new PuzzleInputError(`grid floor has more than ${rows} rows`, startLine + index);
        }
        if (line.length > columns) {
            throw new PuzzleInputError(`grid row has ${line.length} columns; expected at most ${columns}`, startLine + index);
        }
        for (let column = 0; column < columns; column++) {
            const character = line[column] ?? " ";
            if (character === "x" || character === "X") {
                usableCells.push([row, column, floor]);
            } else if (character !== "-" && character !== "_" && character !== " ") {
                throw new PuzzleInputError(`invalid grid character '${character}'`, startLine + index);
            }
        }
        row++;
    }

    if (row > 0) {
        if (row !== rows) {
            throw new PuzzleInputError(`grid floor has ${row} rows; expected ${rows}`, startLine + lines.length - 1);
        }
        floor++;
    }
    if (floor !== floors) {
        throw new PuzzleInputError(`grid has ${floor} floors; expected ${floors}`, startLine);
    }
    return usableCells;
}

function polyPieces() {
    return POLY_LAYOUTS.map((layout, index) => {
        const cells = [];
        for (let row = 0; row < layout.length; row++) {
            for (let column = 0; column < layout[row].length; column++) {
                if (layout[row][column] === 1) {
                    cells.push([row, column, 0]);
                }
            }
        }
        return {
            name: POLY_NAMES[index],
            typeName: POLY_NAMES[index],
            cells
        };
    });
}

function parsePieces(lines, startLine) {
    const pieces = [];
    const usedNames = new Set();
    let current = null;

    function finishPiece(lineNumber) {
        if (current === null) {
            return;
        }
        if (current.cells.length === 0) {
            throw new PuzzleInputError(`piece ${current.name} is empty`, lineNumber);
        }
        for (let copy = 0; copy < current.multiplier; copy++) {
            const name = String.fromCharCode(current.name.charCodeAt(0) + copy);
            if (usedNames.has(name)) {
                throw new PuzzleInputError(`piece name '${name}' is duplicated`, current.lineNumber);
            }
            usedNames.add(name);
            pieces.push({
                name,
                typeName: current.name,
                cells: current.cells.map((cell) => [...cell])
            });
        }
        current = null;
    }

    let foundEnd = false;
    for (let index = 0; index < lines.length; index++) {
        const line = lines[index];
        const lineNumber = startLine + index;
        if (/^#piece-end(?:\s|$)/i.test(line)) {
            finishPiece(lineNumber);
            foundEnd = true;
            break;
        }
        const directive = line.match(/^#piece(.)?(?:\s+x(\d+))?(?:\s|$)/i);
        if (directive) {
            finishPiece(lineNumber);
            const name = directive[1];
            if (!name || /\s/.test(name)) {
                throw new PuzzleInputError("piece directive requires a one-character name", lineNumber);
            }
            const multiplier = directive[2] === undefined ? 1 : Number(directive[2]);
            if (!Number.isInteger(multiplier) || multiplier < 1) {
                throw new PuzzleInputError("piece multiplier must be a positive integer", lineNumber);
            }
            current = {
                name,
                multiplier,
                cells: [],
                floor: 0,
                row: 0,
                lineNumber
            };
            continue;
        }
        if (line === "") {
            continue;
        }
        if (current === null) {
            throw new PuzzleInputError("expected a #Piece definition", lineNumber);
        }
        if (/^\d+$/.test(line)) {
            const requestedFloor = Number(line);
            if (requestedFloor < 2) {
                throw new PuzzleInputError("piece floor markers start at 2", lineNumber);
            }
            current.floor = requestedFloor - 1;
            current.row = 0;
            continue;
        }
        for (let column = 0; column < line.length; column++) {
            const character = line[column];
            if (character === "x" || character === "X") {
                current.cells.push([current.row, column, current.floor]);
            } else if (character !== "-" && character !== "_" && character !== " ") {
                throw new PuzzleInputError(`invalid piece character '${character}'`, lineNumber);
            }
        }
        current.row++;
    }

    if (!foundEnd) {
        throw new PuzzleInputError("missing #piece-End", startLine + lines.length);
    }
    if (pieces.length === 0) {
        throw new PuzzleInputError("no pieces were defined", startLine);
    }
    return pieces;
}

function parsePuzzle3d(text) {
    if (typeof text !== "string" || text.trim() === "") {
        throw new PuzzleInputError("puzzle input is empty");
    }
    const lines = text.replace(/\r\n?/g, "\n").split("\n");
    const [rows, columns, floors] = parseHeader(lines[0]);
    const endIndex = lines.findIndex((line, index) =>
        index > 0 && /^#end of grid(?:\s|\.|$)/i.test(line)
    );
    if (endIndex === -1) {
        throw new PuzzleInputError("missing #end of grid");
    }

    const gridLines = lines.slice(1, endIndex);
    const hasExplicitGrid = gridLines.some((line) => line !== "");
    const usableCells = hasExplicitGrid
        ? parseExplicitGrid(gridLines, rows, columns, floors, 2)
        : Array.from({ length: rows * columns * floors }, (_, index) => {
            const floor = Math.floor(index / (rows * columns));
            const remainder = index % (rows * columns);
            return [Math.floor(remainder / columns), remainder % columns, floor];
        });

    const pieces = /pieces\s*:\s*poly/i.test(lines[endIndex])
        ? polyPieces()
        : parsePieces(lines.slice(endIndex + 1), endIndex + 2);
    const pieceCells = pieces.reduce((total, piece) => total + piece.cells.length, 0);
    if (pieceCells !== usableCells.length) {
        throw new PuzzleInputError(
            `grid has ${usableCells.length} cells but pieces contain ${pieceCells}`
        );
    }

    return {
        rows,
        columns,
        floors,
        usableCells,
        pieces
    };
}

function buildPlacements(definition) {
    const cellIndex = new Map();
    definition.usableCells.forEach((cell, index) =>
        cellIndex.set(coordinateKey(...cell), index)
    );
    const groups = [];
    const groupIndexByKey = new Map();
    definition.pieces.forEach((piece) => {
        const shapeKey = normalizeCoordinates(piece.cells)
            .map((cell) => cell.join(","))
            .join(";");
        const groupKey = `${piece.typeName}\u0000${shapeKey}`;
        let groupIndex = groupIndexByKey.get(groupKey);
        if (groupIndex === undefined) {
            groupIndex = groups.length;
            groupIndexByKey.set(groupKey, groupIndex);
            groups.push({
                cells: piece.cells,
                names: [],
                placements: []
            });
        }
        groups[groupIndex].names.push(piece.name);
    });

    const placements = [];
    groups.forEach((group, groupIndex) => {
        const seenPlacements = new Set();
        for (const orientation of buildOrientations(group.cells)) {
            const maxRow = Math.max(...orientation.map((cell) => cell[0]));
            const maxColumn = Math.max(...orientation.map((cell) => cell[1]));
            const maxFloor = Math.max(...orientation.map((cell) => cell[2]));
            for (let floorOffset = 0; floorOffset + maxFloor < definition.floors; floorOffset++) {
                for (let rowOffset = 0; rowOffset + maxRow < definition.rows; rowOffset++) {
                    for (let columnOffset = 0; columnOffset + maxColumn < definition.columns; columnOffset++) {
                        const cells = [];
                        let valid = true;
                        for (const [row, column, floor] of orientation) {
                            const index = cellIndex.get(coordinateKey(
                                row + rowOffset,
                                column + columnOffset,
                                floor + floorOffset
                            ));
                            if (index === undefined) {
                                valid = false;
                                break;
                            }
                            cells.push(index);
                        }
                        if (!valid) {
                            continue;
                        }
                        cells.sort((left, right) => left - right);
                        const placementKey = cells.join(",");
                        if (seenPlacements.has(placementKey)) {
                            continue;
                        }
                        seenPlacements.add(placementKey);
                        const placementIndex = placements.length;
                        placements.push({ groupIndex, cells });
                        group.placements.push(placementIndex);
                    }
                }
            }
        }
    });
    return { groups, placements };
}

function findCountedExactCover(cellCount, groups, placements) {
    const placementsByCell = Array.from({ length: cellCount }, () => []);
    placements.forEach((placement, placementIndex) => {
        placement.cells.forEach((cell) => placementsByCell[cell].push(placementIndex));
    });
    const covered = new Uint8Array(cellCount);
    const remaining = groups.map((group) => group.names.length);
    const selected = [];
    let triedRows = 0;

    function isAvailable(placement) {
        if (remaining[placement.groupIndex] === 0) {
            return false;
        }
        return placement.cells.every((cell) => covered[cell] === 0);
    }

    function search(coveredCount) {
        if (coveredCount === cellCount) {
            return remaining.every((count) => count === 0);
        }

        let candidates = null;
        for (let cell = 0; cell < cellCount; cell++) {
            if (covered[cell] !== 0) {
                continue;
            }
            const available = placementsByCell[cell].filter(
                (placementIndex) => isAvailable(placements[placementIndex])
            );
            if (available.length === 0) {
                return false;
            }
            if (candidates === null || available.length < candidates.length) {
                candidates = available;
                if (candidates.length === 1) {
                    break;
                }
            }
        }

        for (const placementIndex of candidates) {
            const placement = placements[placementIndex];
            triedRows++;
            remaining[placement.groupIndex]--;
            placement.cells.forEach((cell) => {
                covered[cell] = 1;
            });
            selected.push(placementIndex);
            if (search(coveredCount + placement.cells.length)) {
                return true;
            }
            selected.pop();
            placement.cells.forEach((cell) => {
                covered[cell] = 0;
            });
            remaining[placement.groupIndex]++;
        }
        return false;
    }

    return {
        solved: search(0),
        selectedRows: selected,
        triedRows
    };
}

function solvePuzzle3d(definition) {
    const startedAt = Date.now();
    const { groups, placements } = buildPlacements(definition);
    const hasMultipliers = groups.some((group) => group.names.length > 1);
    const exactCover = hasMultipliers
        ? findCountedExactCover(definition.usableCells.length, groups, placements)
        : ExactCover3d.findOne(
            definition.usableCells.length + groups.length,
            placements.map((placement) => [
                ...placement.cells,
                definition.usableCells.length + placement.groupIndex
            ])
        );
    const assignments = new Array(definition.usableCells.length).fill(null);

    if (exactCover.solved) {
        const usedNames = groups.map(() => 0);
        for (const rowIndex of exactCover.selectedRows) {
            const placement = placements[rowIndex];
            const group = groups[placement.groupIndex];
            const name = group.names[usedNames[placement.groupIndex]++];
            for (const cell of placement.cells) {
                assignments[cell] = name;
            }
        }
    }
    return {
        solved: exactCover.solved,
        rows: definition.rows,
        columns: definition.columns,
        floors: definition.floors,
        usableCells: definition.usableCells,
        assignments,
        triedRows: exactCover.triedRows,
        elapsedMs: Date.now() - startedAt
    };
}

function formatSolution(result) {
    if (!result.solved) {
        return "Found no solution";
    }
    const values = new Map();
    result.usableCells.forEach((cell, index) =>
        values.set(coordinateKey(...cell), result.assignments[index])
    );
    const lines = [];
    for (let floor = 0; floor < result.floors; floor++) {
        for (let row = 0; row < result.rows; row++) {
            const label = String(row + 1 + floor * result.rows).padStart(2, " ");
            const cells = [];
            for (let column = 0; column < result.columns; column++) {
                cells.push(values.get(coordinateKey(row, column, floor)) ?? "*");
            }
            lines.push(`${label}  ${cells.join("  ")}`);
        }
        if (floor + 1 < result.floors) {
            lines.push("");
        }
    }
    return lines.join("\n");
}

class Puzzle3d {
    constructor(pieces, rows, columns, floors, input) {
        if (input) {
            this.definition = parsePuzzle3d(input);
        } else {
            const usableCells = [];
            for (let floor = 0; floor < floors; floor++) {
                for (let row = 0; row < rows; row++) {
                    for (let column = 0; column < columns; column++) {
                        usableCells.push([row, column, floor]);
                    }
                }
            }
            this.definition = {
                rows,
                columns,
                floors,
                usableCells,
                pieces: polyPieces().slice(0, pieces)
            };
            const pieceCells = this.definition.pieces.reduce(
                (total, piece) => total + piece.cells.length,
                0
            );
            if (pieceCells !== usableCells.length) {
                this.invalidMessage = `grid has ${rows * columns * floors} cells but pieces contain ${pieces * 5}`;
            }
        }
        this.totalSolutions = 0;
        this.triedPieces = 0;
        this.solutionFound = false;
        this.result = null;
    }

    solve() {
        if (this.invalidMessage) {
            return "Invalid input";
        }
        this.result = solvePuzzle3d(this.definition);
        this.totalSolutions = this.result.solved ? 1 : 0;
        this.solutionFound = this.result.solved;
        this.triedPieces = this.result.triedRows;
        return formatSolution(this.result);
    }
}

export {
    Puzzle3d,
    PuzzleInputError,
    buildOrientations,
    formatSolution,
    parsePuzzle3d,
    solvePuzzle3d
};
