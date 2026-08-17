import {
    HEX_NEIGHBOR_OFFSETS,
    PuzzleHex,
    buildHexOrientations,
    parsePuzzleHex,
    solvePuzzleHex
} from "../puzzleHex.js";
import { GraphItHex } from "../graphItHex.js";
import { HEX_FLOWER, STACKED_TRIANGLE } from "../presetsHex.js";

const { assert } = chai;

describe("Hexagonal-prism solver", function () {
    it("defines six axial and two depth neighbors", function () {
        assert.deepEqual(HEX_NEIGHBOR_OFFSETS, [
            [1, 0, 0], [1, -1, 0], [0, -1, 0],
            [-1, 0, 0], [-1, 1, 0], [0, 1, 0],
            [0, 0, 1], [0, 0, -1]
        ]);
    });

    it("deduplicates physical prism orientations", function () {
        assert.lengthOf(buildHexOrientations([[0, 0, 0]]), 1);
        assert.lengthOf(buildHexOrientations([[0, 0, 0], [1, 0, 0]]), 3);
        assert.lengthOf(buildHexOrientations([[0, 0, 0], [0, 0, 1]]), 1);
        assert.lengthOf(
            buildHexOrientations([[0, 0, 0], [1, 0, 0], [0, 1, 0], [0, 0, 1]]),
            12
        );
    });

    it("parses equivalent ASCII and coordinate definitions", function () {
        const ascii = "#1,2,1\nxx\n#end of grid\n#PieceA\nxx\n#piece-End";
        const coordinates =
            "#coordinates\n0,0,0\n1,0,0\n#end of grid\n" +
            "#PieceA\n0,0,0\n1,0,0\n#piece-End";
        const asciiDefinition = parsePuzzleHex(ascii);
        const coordinateDefinition = parsePuzzleHex(coordinates);
        assert.deepEqual(asciiDefinition.usableCells, coordinateDefinition.usableCells);
        assert.deepEqual(asciiDefinition.pieces[0].cells, coordinateDefinition.pieces[0].cells);
    });

    it("reports line-aware coordinate errors", function () {
        assert.throws(
            () => parsePuzzleHex(
                "#coordinates\n0,0,0\n0,0,0\n#end of grid\n#PieceA\n0,0,0\n#piece-End"
            ),
            /Line 3: duplicate grid coordinate/
        );
        assert.throws(
            () => parsePuzzleHex(
                "#coordinates\nnot-a-cell\n#end of grid\n#PieceA\n0,0,0\n#piece-End"
            ),
            /Line 2: expected q,r,z/
        );
    });

    it("solves both editable presets", function () {
        for (const input of [STACKED_TRIANGLE, HEX_FLOWER]) {
            const puzzle = new PuzzleHex(input);
            const output = puzzle.solve();
            assert.isTrue(puzzle.result.solved);
            assert.notInclude(puzzle.result.assignments, null);
            assert.include(output, "Layer 0");
        }
    });

    it("reports an impossible depth placement", function () {
        const input =
            "#coordinates\n0,0,0\n1,0,0\n2,0,0\n#end of grid\n" +
            "#PieceA\n0,0,0\n0,0,1\n#PieceB\n0,0,0\n#piece-End";
        assert.isFalse(solvePuzzleHex(parsePuzzleHex(input)).solved);
    });

    it("renders axial positions as hexagonal prisms", function () {
        const puzzle = new PuzzleHex(HEX_FLOWER);
        puzzle.solve();
        const markup = new GraphItHex().getHexX3d(puzzle.result, "unit test");
        assert.include(markup, "IndexedFaceSet");
        assert.include(markup, 'DEF="HEX_PIECE_0"');
        assert.include(markup, 'translation="10.000 0.000 0.000"');
        assert.include(markup, "height: calc(100vh - 60px)");
        assert.notInclude(markup, "x3d { width: 100%; height: 100%; }");
    });
});

describe("Hex Solver page controls", function () {
    const workers = [];

    before(async function () {
        localStorage.removeItem("lastRunHex");
        sessionStorage.removeItem("puzzleHexSolvedState");
        const fixture = document.createElement("div");
        fixture.hidden = true;
        fixture.innerHTML = `
            <button id="solveButton"></button>
            <button id="graphItButton"></button>
            <select id="PuzzleSelect">
                <option value="StackedTriangle">StackedTriangle</option>
                <option value="HexFlower">HexFlower</option>
                <option value="Custom">Custom</option>
            </select>
            <button id="restoreButton1"></button><button id="restoreButton2"></button><button id="restoreButton3"></button>
            <button id="saveButton1"></button><button id="saveButton2"></button><button id="saveButton3"></button>
            <textarea id="input"></textarea><textarea id="output"></textarea>
        `;
        document.body.append(fixture);

        window.Worker = class {
            constructor() {
                this.listeners = {};
                this.terminated = false;
                workers.push(this);
            }
            addEventListener(type, listener) { this.listeners[type] = listener; }
            postMessage(message) { this.message = message; }
            terminate() { this.terminated = true; }
        };
        await import("../sourceHex.js");
    });

    it("loads both preset formats", function () {
        const select = document.getElementById("PuzzleSelect");
        select.value = "HexFlower";
        select.dispatchEvent(new Event("change"));
        assert.equal(document.getElementById("input").value, HEX_FLOWER);
        select.value = "StackedTriangle";
        select.dispatchEvent(new Event("change"));
        assert.equal(document.getElementById("input").value, STACKED_TRIANGLE);
    });

    it("submits and cancels a worker solve", function () {
        document.getElementById("solveButton").click();
        assert.deepEqual(workers.at(-1).message, { text: STACKED_TRIANGLE });
        assert.equal(document.getElementById("output").value, "Solving…");
        document.getElementById("solveButton").click();
        assert.isTrue(workers.at(-1).terminated);
        assert.equal(document.getElementById("output").value, "Solve cancelled");
    });
});
