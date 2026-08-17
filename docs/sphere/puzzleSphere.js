import { ExactCover3d } from "../3d/exactCover3d.js";

const SPHERE_NEIGHBOR_OFFSETS = Object.freeze([
    [1, 0, 0], [1, -1, 0], [0, -1, 0],
    [-1, 0, 0], [-1, 1, 0], [0, 1, 0],
    [0, 1, -1], [0, 0, -1], [1, 0, -1],
    [0, -1, 1], [0, 0, 1], [-1, 0, 1]
].map((offset) => Object.freeze(offset)));

class SpherePuzzleInputError extends Error {
    constructor(message, lineNumber) {
        super(lineNumber ? `Line ${lineNumber}: ${message}` : message);
        this.name = "SpherePuzzleInputError";
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
    cells.forEach((cell) => {
        for (let axis = 0; axis < 3; axis++) {
            minimum[axis] = Math.min(minimum[axis], cell[axis]);
        }
    });
    return cells.map((cell) => cell.map((value, axis) => value - minimum[axis]))
        .sort(compareCoordinates);
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

function toCubic([q, r, z]) {
    return [q + r, q + z, r + z];
}

function fromCubic([x, y, z]) {
    return [(x + y - z) / 2, (x + z - y) / 2, (y + z - x) / 2];
}

function buildSphereOrientations(cells) {
    const permutations = [
        [0, 1, 2], [0, 2, 1], [1, 0, 2],
        [1, 2, 0], [2, 0, 1], [2, 1, 0]
    ];
    const cubicCells = cells.map(toCubic);
    const orientations = [];
    const seen = new Set();

    for (const permutation of permutations) {
        const parity = permutationSign(permutation);
        for (const firstSign of [-1, 1]) {
            for (const secondSign of [-1, 1]) {
                for (const thirdSign of [-1, 1]) {
                    const signs = [firstSign, secondSign, thirdSign];
                    if (parity * firstSign * secondSign * thirdSign !== 1) {
                        continue;
                    }
                    const transformed = cubicCells.map((cell) =>
                        signs.map((sign, axis) => sign * cell[permutation[axis]])
                    ).map(fromCubic);
                    if (transformed.some((cell) => cell.some((value) => !Number.isInteger(value)))) {
                        throw new Error("sphere rotation left the FCC lattice");
                    }
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
    const pyramid = line.match(/^#\s*pyramid\s+(\d+)(?:\s|$)/i);
    if (pyramid) {
        const size = Number(pyramid[1]);
        if (!Number.isInteger(size) || size < 1) {
            throw new SpherePuzzleInputError("pyramid size must be a positive integer", 1);
        }
        return { mode: "pyramid", size };
    }
    if (/^#\s*coordinates(?:\s|$)/i.test(line)) {
        return { mode: "coordinates" };
    }
    const match = line.match(/^#\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)(?:\s|$)/);
    if (!match) {
        throw new SpherePuzzleInputError(
            "expected #pyramid N, #rows,columns,layers, or #coordinates",
            1
        );
    }
    const [rows, columns, layers] = match.slice(1, 4).map(Number);
    if ([rows, columns, layers].some((value) => !Number.isInteger(value) || value < 1)) {
        throw new SpherePuzzleInputError("dimensions must be positive integers", 1);
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
    const layers = splitAsciiLayers(lines, startLine);
    if (layers.length !== header.layers) {
        throw new SpherePuzzleInputError(
            `grid has ${layers.length} layers; expected ${header.layers}`,
            startLine
        );
    }
    const cells = [];
    layers.forEach((layer, z) => {
        if (layer.rows.length !== header.rows) {
            throw new SpherePuzzleInputError(
                `grid layer has ${layer.rows.length} rows; expected ${header.rows}`,
                layer.startLine
            );
        }
        layer.rows.forEach((line, r) => {
            if (line.length > header.columns) {
                throw new SpherePuzzleInputError(
                    `grid row has ${line.length} columns; expected at most ${header.columns}`,
                    layer.startLine + r
                );
            }
            for (let q = 0; q < header.columns; q++) {
                const character = line[q] ?? " ";
                if (character === "x" || character === "X") {
                    cells.push([q, r, z]);
                } else if (character !== "-" && character !== "_" && character !== " ") {
                    throw new SpherePuzzleInputError(
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
        throw new SpherePuzzleInputError("expected q,r,z integer coordinates", lineNumber);
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
            throw new SpherePuzzleInputError(
                `duplicate ${label} coordinate ${key}`,
                startLine + index
            );
        }
        seen.add(key);
        cells.push(cell);
    });
    return cells;
}

function buildPyramid(size) {
    const cells = [];
    for (let z = 0; z < size; z++) {
        for (let r = 0; r < size - z; r++) {
            for (let q = 0; q < size - z - r; q++) {
                cells.push([q, r, z]);
            }
        }
    }
    return cells;
}

function parsePieceDirective(line, lineNumber) {
    const match = line.match(/^#piece(\S)(?:\s+x(\d+))?(?:\s|$)/i);
    if (!match) {
        throw new SpherePuzzleInputError(
            "piece directive requires a one-character name",
            lineNumber
        );
    }
    const multiplier = match[2] === undefined ? 1 : Number(match[2]);
    if (!Number.isInteger(multiplier) || multiplier < 1) {
        throw new SpherePuzzleInputError("piece multiplier must be a positive integer", lineNumber);
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
        if (mode === "ascii") {
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
                        throw new SpherePuzzleInputError(
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
                        throw new SpherePuzzleInputError(
                            `invalid piece character '${character}'`,
                            actualLine
                        );
                    }
                }
                r++;
            });
        } else {
            cells = parseCoordinateCells(current.lines, current.contentStart, "piece");
        }

        if (cells.length === 0) {
            throw new SpherePuzzleInputError(`piece ${current.name} is empty`, lineNumber);
        }
        const seenCells = new Set();
        cells.forEach((cell) => {
            const key = coordinateKey(...cell);
            if (seenCells.has(key)) {
                throw new SpherePuzzleInputError(
                    `duplicate piece coordinate ${key}`,
                    current.lineNumber
                );
            }
            seenCells.add(key);
        });
        const normalized = normalizeCoordinates(cells);
        for (let copy = 0; copy < current.multiplier; copy++) {
            const name = String.fromCharCode(current.name.charCodeAt(0) + copy);
            if (usedNames.has(name)) {
                throw new SpherePuzzleInputError(
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
            current = {
                ...parsePieceDirective(line, lineNumber),
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
            throw new SpherePuzzleInputError("expected a #Piece definition", lineNumber);
        }
        current.lines.push(line);
    }
    if (!foundEnd) {
        throw new SpherePuzzleInputError("missing #piece-End", startLine + lines.length);
    }
    if (pieces.length === 0) {
        throw new SpherePuzzleInputError("no pieces were defined", startLine);
    }
    return pieces;
}

function boundsFor(cells, header) {
    if (header.mode === "ascii") {
        return {
            qMin: 0, qMax: header.columns - 1,
            rMin: 0, rMax: header.rows - 1,
            zMin: 0, zMax: header.layers - 1
        };
    }
    if (header.mode === "pyramid") {
        return {
            qMin: 0, qMax: header.size - 1,
            rMin: 0, rMax: header.size - 1,
            zMin: 0, zMax: header.size - 1
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

function parsePuzzleSphere(text) {
    if (typeof text !== "string" || text.trim() === "") {
        throw new SpherePuzzleInputError("puzzle input is empty");
    }
    const lines = text.replace(/\r\n?/g, "\n").split("\n");
    const header = parseHeader(lines[0]);
    const endIndex = lines.findIndex((line, index) =>
        index > 0 && /^#end of grid(?:\s|\.|$)/i.test(line)
    );
    if (endIndex === -1) {
        throw new SpherePuzzleInputError("missing #end of grid");
    }
    const gridLines = lines.slice(1, endIndex);
    let usableCells;
    if (header.mode === "pyramid") {
        const firstExplicitLine = gridLines.findIndex((line) => line !== "");
        if (firstExplicitLine !== -1) {
            throw new SpherePuzzleInputError(
                "#pyramid creates the grid; expected #end of grid",
                firstExplicitLine + 2
            );
        }
        usableCells = buildPyramid(header.size);
    } else if (header.mode === "ascii") {
        usableCells = parseAsciiGrid(gridLines, header, 2);
    } else {
        usableCells = parseCoordinateCells(gridLines, 2, "grid");
    }
    if (usableCells.length === 0) {
        throw new SpherePuzzleInputError("grid has no usable cells", 2);
    }

    const pieceMode = header.mode === "ascii" ? "ascii" : "coordinates";
    const pieces = parsePieces(lines.slice(endIndex + 1), endIndex + 2, pieceMode);
    const pieceCells = pieces.reduce((total, piece) => total + piece.cells.length, 0);
    if (pieceCells !== usableCells.length) {
        throw new SpherePuzzleInputError(
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
            .map((cell) => cell.join(",")).join(";");
        const groupKey = `${piece.typeName}\u0000${shapeKey}`;
        let groupIndex = groupIndexByKey.get(groupKey);
        if (groupIndex === undefined) {
            groupIndex = groups.length;
            groupIndexByKey.set(groupKey, groupIndex);
            groups.push({ cells: piece.cells, names: [] });
        }
        groups[groupIndex].names.push(piece.name);
    });

    const placements = [];
    groups.forEach((group, groupIndex) => {
        const seenPlacements = new Set();
        for (const orientation of buildSphereOrientations(group.cells)) {
            const anchor = orientation[0];
            for (const target of definition.usableCells) {
                const offset = target.map((value, axis) => value - anchor[axis]);
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
                const key = cells.join(",");
                if (!seenPlacements.has(key)) {
                    seenPlacements.add(key);
                    placements.push({ groupIndex, cells });
                }
            }
        }
    });
    return { groups, placements };
}

function findCountedExactCover(cellCount, groups, placements) {
    const placementsByCell = Array.from({ length: cellCount }, () => []);
    placements.forEach((placement, index) => {
        placement.cells.forEach((cell) => placementsByCell[cell].push(index));
    });
    const covered = new Uint8Array(cellCount);
    const remaining = groups.map((group) => group.names.length);
    const selectedRows = [];
    let triedRows = 0;

    function available(placement) {
        return remaining[placement.groupIndex] > 0 &&
            placement.cells.every((cell) => covered[cell] === 0);
    }
    function search(coveredCount) {
        if (coveredCount === cellCount) {
            return remaining.every((count) => count === 0);
        }
        let candidates = null;
        for (let cell = 0; cell < cellCount; cell++) {
            if (covered[cell]) {
                continue;
            }
            const options = placementsByCell[cell].filter((index) => available(placements[index]));
            if (options.length === 0) {
                return false;
            }
            if (candidates === null || options.length < candidates.length) {
                candidates = options;
                if (options.length === 1) {
                    break;
                }
            }
        }
        for (const index of candidates) {
            const placement = placements[index];
            triedRows++;
            remaining[placement.groupIndex]--;
            placement.cells.forEach((cell) => { covered[cell] = 1; });
            selectedRows.push(index);
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

function solvePuzzleSphere(definition) {
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
        exactCover.selectedRows.forEach((rowIndex) => {
            const placement = placements[rowIndex];
            const group = groups[placement.groupIndex];
            const name = group.names[usedNames[placement.groupIndex]++];
            placement.cells.forEach((cell) => { assignments[cell] = name; });
        });
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

function formatSphereSolution(result) {
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
            lines.push(`${" ".repeat((z - zMin) * 2 + r - rMin)}${cells.join(" ")}`);
        }
        if (z < zMax) {
            lines.push("");
        }
    }
    return lines.join("\n");
}

class PuzzleSphere {
    constructor(input) {
        this.definition = parsePuzzleSphere(input);
        this.result = null;
    }

    solve() {
        this.result = solvePuzzleSphere(this.definition);
        return formatSphereSolution(this.result);
    }
}

export {
    SPHERE_NEIGHBOR_OFFSETS,
    SpherePuzzleInputError,
    PuzzleSphere,
    buildSphereOrientations,
    formatSphereSolution,
    parsePuzzleSphere,
    solvePuzzleSphere
};
