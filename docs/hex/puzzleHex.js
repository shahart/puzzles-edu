import { ExactCover3d } from "../3d/exactCover3d.js";

const HEX_NEIGHBOR_OFFSETS = Object.freeze([
    [1, 0, 0],
    [1, -1, 0],
    [0, -1, 0],
    [-1, 0, 0],
    [-1, 1, 0],
    [0, 1, 0],
    [0, 0, 1],
    [0, 0, -1]
].map((offset) => Object.freeze(offset)));

class HexPuzzleInputError extends Error {
    constructor(message, lineNumber) {
        super(lineNumber ? `Line ${lineNumber}: ${message}` : message);
        this.name = "HexPuzzleInputError";
        this.lineNumber = lineNumber;
    }
}

function coordinateKey(q, r, z) {
    return `${q},${r},${z}`;
}

function compareCoordinates(left, right) {
    return left[2] - right[2] || left[1] - right[1] || left[0] - right[0];
}

function normalizeCoordinates(cells) {
    const minimum = [Infinity, Infinity, Infinity];
    for (const cell of cells) {
        minimum[0] = Math.min(minimum[0], cell[0]);
        minimum[1] = Math.min(minimum[1], cell[1]);
        minimum[2] = Math.min(minimum[2], cell[2]);
    }
    return cells
        .map((cell) => [
            cell[0] - minimum[0],
            cell[1] - minimum[1],
            cell[2] - minimum[2]
        ])
        .sort(compareCoordinates);
}

function rotate60([q, r, z]) {
    return [-r, q + r, z];
}

function turnFrontToBack([q, r, z]) {
    return [q + r, -r, -z];
}

function buildHexOrientations(cells) {
    const orientations = [];
    const seen = new Set();

    for (const turned of [false, true]) {
        let rotated = cells.map((cell) => turned ? turnFrontToBack(cell) : [...cell]);
        for (let rotation = 0; rotation < 6; rotation++) {
            const normalized = normalizeCoordinates(rotated);
            const key = normalized.map((cell) => cell.join(",")).join(";");
            if (!seen.has(key)) {
                seen.add(key);
                orientations.push(normalized);
            }
            rotated = rotated.map(rotate60);
        }
    }
    return orientations;
}

function parseHeader(line) {
    if (/^#\s*coordinates(?:\s|$)/i.test(line)) {
        return { mode: "coordinates" };
    }
    const match = line.match(/^#\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)(?:\s|$)/);
    if (!match) {
        throw new HexPuzzleInputError(
            "expected #rows,columns,layers or #coordinates",
            1
        );
    }
    const [rows, columns, layers] = match.slice(1, 4).map(Number);
    if ([rows, columns, layers].some((value) => !Number.isInteger(value) || value < 1)) {
        throw new HexPuzzleInputError("dimensions must be positive integers", 1);
    }
    return { mode: "ascii", rows, columns, layers };
}

function splitAsciiLayers(lines, startLine) {
    const layers = [];
    let current = [];
    let currentStart = startLine;

    lines.forEach((line, index) => {
        if (line === "") {
            if (current.length > 0) {
                layers.push({ rows: current, startLine: currentStart });
                current = [];
            }
            currentStart = startLine + index + 1;
        } else {
            if (current.length === 0) {
                currentStart = startLine + index;
            }
            current.push(line);
        }
    });
    if (current.length > 0) {
        layers.push({ rows: current, startLine: currentStart });
    }
    return layers;
}

function parseAsciiGrid(lines, header, startLine) {
    const parsedLayers = splitAsciiLayers(lines, startLine);
    if (parsedLayers.length !== header.layers) {
        throw new HexPuzzleInputError(
            `grid has ${parsedLayers.length} layers; expected ${header.layers}`,
            startLine
        );
    }

    const cells = [];
    parsedLayers.forEach((layer, z) => {
        if (layer.rows.length !== header.rows) {
            throw new HexPuzzleInputError(
                `grid layer has ${layer.rows.length} rows; expected ${header.rows}`,
                layer.startLine
            );
        }
        layer.rows.forEach((line, r) => {
            if (line.length > header.columns) {
                throw new HexPuzzleInputError(
                    `grid row has ${line.length} columns; expected at most ${header.columns}`,
                    layer.startLine + r
                );
            }
            for (let q = 0; q < header.columns; q++) {
                const character = line[q] ?? " ";
                if (character === "x" || character === "X") {
                    cells.push([q, r, z]);
                } else if (character !== "-" && character !== "_" && character !== " ") {
                    throw new HexPuzzleInputError(
                        `invalid grid character '${character}'`,
                        layer.startLine + r
                    );
                }
            }
        });
    });
    return cells;
}

function parseCoordinate(line, lineNumber) {
    const match = line.match(/^\s*(-?\d+)\s*,\s*(-?\d+)\s*,\s*(-?\d+)\s*$/);
    if (!match) {
        throw new HexPuzzleInputError("expected q,r,z integer coordinates", lineNumber);
    }
    return match.slice(1, 4).map(Number);
}

function parseCoordinateCells(lines, startLine, label) {
    const cells = [];
    const seen = new Set();
    lines.forEach((line, index) => {
        if (line === "") {
            return;
        }
        const cell = parseCoordinate(line, startLine + index);
        const key = coordinateKey(...cell);
        if (seen.has(key)) {
            throw new HexPuzzleInputError(
                `duplicate ${label} coordinate ${key}`,
                startLine + index
            );
        }
        seen.add(key);
        cells.push(cell);
    });
    return cells;
}

function parsePieceDirective(line, lineNumber) {
    const match = line.match(/^#piece(\S)(?:\s+x(\d+))?(?:\s|$)/i);
    if (!match) {
        throw new HexPuzzleInputError(
            "piece directive requires a one-character name",
            lineNumber
        );
    }
    const multiplier = match[2] === undefined ? 1 : Number(match[2]);
    if (!Number.isInteger(multiplier) || multiplier < 1) {
        throw new HexPuzzleInputError("piece multiplier must be a positive integer", lineNumber);
    }
    return { name: match[1], multiplier };
}

function parsePieces(lines, startLine, mode) {
    const pieces = [];
    const usedNames = new Set();
    let current = null;
    let foundEnd = false;

    function finishPiece(lineNumber) {
        if (current === null) {
            return;
        }
        let cells;
        if (mode === "coordinates") {
            cells = parseCoordinateCells(current.lines, current.contentStart, "piece");
        } else {
            cells = [];
            let z = 0;
            let r = 0;
            current.lines.forEach((line, index) => {
                const actualLine = current.contentStart + index;
                if (line === "") {
                    return;
                }
                if (/^\d+$/.test(line)) {
                    const layer = Number(line);
                    if (layer < 2) {
                        throw new HexPuzzleInputError(
                            "piece layer markers start at 2",
                            actualLine
                        );
                    }
                    z = layer - 1;
                    r = 0;
                    return;
                }
                for (let q = 0; q < line.length; q++) {
                    const character = line[q];
                    if (character === "x" || character === "X") {
                        cells.push([q, r, z]);
                    } else if (character !== "-" && character !== "_" && character !== " ") {
                        throw new HexPuzzleInputError(
                            `invalid piece character '${character}'`,
                            actualLine
                        );
                    }
                }
                r++;
            });
        }

        if (cells.length === 0) {
            throw new HexPuzzleInputError(`piece ${current.name} is empty`, lineNumber);
        }
        const seenCells = new Set();
        for (const cell of cells) {
            const key = coordinateKey(...cell);
            if (seenCells.has(key)) {
                throw new HexPuzzleInputError(
                    `duplicate piece coordinate ${key}`,
                    current.lineNumber
                );
            }
            seenCells.add(key);
        }
        const normalized = normalizeCoordinates(cells);
        for (let copy = 0; copy < current.multiplier; copy++) {
            const name = String.fromCharCode(current.name.charCodeAt(0) + copy);
            if (usedNames.has(name)) {
                throw new HexPuzzleInputError(
                    `piece name '${name}' is duplicated`,
                    current.lineNumber
                );
            }
            usedNames.add(name);
            pieces.push({
                name,
                typeName: current.name,
                cells: normalized.map((cell) => [...cell])
            });
        }
        current = null;
    }

    for (let index = 0; index < lines.length; index++) {
        const line = lines[index];
        const lineNumber = startLine + index;
        if (/^#piece-end(?:\s|$)/i.test(line)) {
            finishPiece(lineNumber);
            foundEnd = true;
            break;
        }
        if (/^#piece/i.test(line)) {
            finishPiece(lineNumber);
            const directive = parsePieceDirective(line, lineNumber);
            current = {
                ...directive,
                lineNumber,
                contentStart: lineNumber + 1,
                lines: []
            };
            continue;
        }
        if (current === null) {
            if (line === "") {
                continue;
            }
            throw new HexPuzzleInputError("expected a #Piece definition", lineNumber);
        }
        current.lines.push(line);
    }

    if (!foundEnd) {
        throw new HexPuzzleInputError("missing #piece-End", startLine + lines.length);
    }
    if (pieces.length === 0) {
        throw new HexPuzzleInputError("no pieces were defined", startLine);
    }
    return pieces;
}

function boundsFor(cells, header) {
    if (header.mode === "ascii") {
        return {
            qMin: 0,
            qMax: header.columns - 1,
            rMin: 0,
            rMax: header.rows - 1,
            zMin: 0,
            zMax: header.layers - 1
        };
    }
    return {
        qMin: Math.min(...cells.map((cell) => cell[0])),
        qMax: Math.max(...cells.map((cell) => cell[0])),
        rMin: Math.min(...cells.map((cell) => cell[1])),
        rMax: Math.max(...cells.map((cell) => cell[1])),
        zMin: Math.min(...cells.map((cell) => cell[2])),
        zMax: Math.max(...cells.map((cell) => cell[2]))
    };
}

function parsePuzzleHex(text) {
    if (typeof text !== "string" || text.trim() === "") {
        throw new HexPuzzleInputError("puzzle input is empty");
    }
    const lines = text.replace(/\r\n?/g, "\n").split("\n");
    const header = parseHeader(lines[0]);
    const endIndex = lines.findIndex((line, index) =>
        index > 0 && /^#end of grid(?:\s|\.|$)/i.test(line)
    );
    if (endIndex === -1) {
        throw new HexPuzzleInputError("missing #end of grid");
    }

    const gridLines = lines.slice(1, endIndex);
    const usableCells = header.mode === "ascii"
        ? parseAsciiGrid(gridLines, header, 2)
        : parseCoordinateCells(gridLines, 2, "grid");
    if (usableCells.length === 0) {
        throw new HexPuzzleInputError("grid has no usable cells", 2);
    }

    const pieces = parsePieces(lines.slice(endIndex + 1), endIndex + 2, header.mode);
    const pieceCells = pieces.reduce((total, piece) => total + piece.cells.length, 0);
    if (pieceCells !== usableCells.length) {
        throw new HexPuzzleInputError(
            `grid has ${usableCells.length} cells but pieces contain ${pieceCells}`
        );
    }

    usableCells.sort(compareCoordinates);
    return {
        mode: header.mode,
        usableCells,
        pieces,
        bounds: boundsFor(usableCells, header)
    };
}

function buildPlacements(definition) {
    const cellIndex = new Map();
    definition.usableCells.forEach((cell, index) => {
        cellIndex.set(coordinateKey(...cell), index);
    });

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
            groups.push({ cells: piece.cells, names: [], placements: [] });
        }
        groups[groupIndex].names.push(piece.name);
    });

    const placements = [];
    groups.forEach((group, groupIndex) => {
        const seenPlacements = new Set();
        for (const orientation of buildHexOrientations(group.cells)) {
            const anchor = orientation[0];
            for (const target of definition.usableCells) {
                const offset = [
                    target[0] - anchor[0],
                    target[1] - anchor[1],
                    target[2] - anchor[2]
                ];
                const cells = [];
                let valid = true;
                for (const cell of orientation) {
                    const index = cellIndex.get(coordinateKey(
                        cell[0] + offset[0],
                        cell[1] + offset[1],
                        cell[2] + offset[2]
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
    const selectedRows = [];
    let triedRows = 0;

    function isAvailable(placement) {
        return remaining[placement.groupIndex] > 0 &&
            placement.cells.every((cell) => covered[cell] === 0);
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
            placement.cells.forEach((cell) => { covered[cell] = 1; });
            selectedRows.push(placementIndex);
            if (search(coveredCount + placement.cells.length)) {
                return true;
            }
            selectedRows.pop();
            placement.cells.forEach((cell) => { covered[cell] = 0; });
            remaining[placement.groupIndex]++;
        }
        return false;
    }

    return { solved: search(0), selectedRows, triedRows };
}

function solvePuzzleHex(definition) {
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
            placement.cells.forEach((cell) => { assignments[cell] = name; });
        }
    }
    return {
        solved: exactCover.solved,
        mode: definition.mode,
        usableCells: definition.usableCells,
        assignments,
        bounds: definition.bounds,
        triedRows: exactCover.triedRows,
        elapsedMs: Date.now() - startedAt
    };
}

function formatHexSolution(result) {
    if (!result.solved) {
        return "Found no solution";
    }
    const values = new Map();
    result.usableCells.forEach((cell, index) => {
        values.set(coordinateKey(...cell), result.assignments[index]);
    });
    const { qMin, qMax, rMin, rMax, zMin, zMax } = result.bounds;
    const lines = [];
    for (let z = zMin; z <= zMax; z++) {
        lines.push(`Layer ${z}`);
        for (let r = rMin; r <= rMax; r++) {
            const cells = [];
            for (let q = qMin; q <= qMax; q++) {
                cells.push(values.get(coordinateKey(q, r, z)) ?? "*");
            }
            lines.push(`${" ".repeat(r - rMin)}${cells.join(" ")}`);
        }
        if (z < zMax) {
            lines.push("");
        }
    }
    return lines.join("\n");
}

class PuzzleHex {
    constructor(input) {
        this.definition = parsePuzzleHex(input);
        this.result = null;
    }

    solve() {
        this.result = solvePuzzleHex(this.definition);
        return formatHexSolution(this.result);
    }
}

export {
    HEX_NEIGHBOR_OFFSETS,
    HexPuzzleInputError,
    PuzzleHex,
    buildHexOrientations,
    formatHexSolution,
    parsePuzzleHex,
    solvePuzzleHex
};
