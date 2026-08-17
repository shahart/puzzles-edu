import {
    SPHERE_NEIGHBOR_OFFSETS,
    PuzzleSphere,
    buildSphereOrientations,
    parsePuzzleSphere,
    solvePuzzleSphere
} from "../puzzleSphere.js";
import { GraphItSphere, latticePosition } from "../graphItSphere.js";
import { BALLS_PYRAMID_900, TINY_TETRAHEDRON } from "../presetsSphere.js";

const { assert } = chai;

function normalizedKey(cells) {
    const minimum = [0, 1, 2].map((axis) => Math.min(...cells.map((cell) => cell[axis])));
    return cells.map((cell) => cell.map((value, axis) => value - minimum[axis]))
        .sort((left, right) => left[2] - right[2] || left[1] - right[1] || left[0] - right[0])
        .map((cell) => cell.join(",")).join(";");
}

function reflectCubicX([q, r, z]) {
    let [x, y, cubicZ] = [q + r, q + z, r + z];
    x = -x;
    return [
        (x + y - cubicZ) / 2,
        (x + cubicZ - y) / 2,
        (y + cubicZ - x) / 2
    ];
}

describe("Close-packed sphere solver", function () {
    it("defines six same-layer, three upper, and three lower neighbors", function () {
        assert.lengthOf(SPHERE_NEIGHBOR_OFFSETS, 12);
        assert.equal(SPHERE_NEIGHBOR_OFFSETS.filter((offset) => offset[2] === 0).length, 6);
        assert.equal(SPHERE_NEIGHBOR_OFFSETS.filter((offset) => offset[2] === 1).length, 3);
        assert.equal(SPHERE_NEIGHBOR_OFFSETS.filter((offset) => offset[2] === -1).length, 3);
    });

    it("generates proper FCC rotations and excludes a mirror-only copy", function () {
        assert.lengthOf(buildSphereOrientations([[0, 0, 0]]), 1);
        assert.lengthOf(buildSphereOrientations([[0, 0, 0], [1, 0, 0]]), 6);
        const chiral = [[0, 0, 0], [1, 0, 0], [0, 0, 1], [0, 1, 1], [2, 0, 0]];
        const orientations = buildSphereOrientations(chiral);
        assert.lengthOf(orientations, 24);
        const orientationKeys = new Set(orientations.map(normalizedKey));
        assert.isFalse(orientationKeys.has(normalizedKey(chiral.map(reflectCubicX))));
    });

    it("builds tetrahedral pyramids with the expected cell count", function () {
        for (let size = 1; size <= 5; size++) {
            const count = size * (size + 1) * (size + 2) / 6;
            const definition = parsePuzzleSphere(
                `#pyramid ${size}\n#end of grid\n#PieceA x${count}\n0,0,0\n#piece-End`
            );
            assert.lengthOf(definition.usableCells, count);
        }
    });

    it("parses equivalent ASCII and coordinate definitions", function () {
        const ascii = "#1,2,1\nxx\n#end of grid\n#PieceA\nxx\n#piece-End";
        const coordinates =
            "#coordinates\n0,0,0\n1,0,0\n#end of grid\n" +
            "#PieceA\n0,0,0\n1,0,0\n#piece-End";
        const asciiDefinition = parsePuzzleSphere(ascii);
        const coordinateDefinition = parsePuzzleSphere(coordinates);
        assert.deepEqual(asciiDefinition.usableCells, coordinateDefinition.usableCells);
        assert.deepEqual(asciiDefinition.pieces[0].cells, coordinateDefinition.pieces[0].cells);
    });

    it("reports line-aware malformed and duplicate coordinates", function () {
        assert.throws(
            () => parsePuzzleSphere(
                "#coordinates\n0,0,0\n0,0,0\n#end of grid\n#PieceA\n0,0,0\n#piece-End"
            ),
            /Line 3: duplicate grid coordinate/
        );
        assert.throws(
            () => parsePuzzleSphere(
                "#pyramid 2\nx\n#end of grid\n#PieceA x4\n0,0,0\n#piece-End"
            ),
            /Line 2: #pyramid creates the grid/
        );
    });

    it("solves the tutorial and Balls Pyramid 900 presets", function () {
        for (const input of [TINY_TETRAHEDRON, BALLS_PYRAMID_900]) {
            const puzzle = new PuzzleSphere(input);
            const output = puzzle.solve();
            assert.isTrue(puzzle.result.solved);
            assert.notInclude(puzzle.result.assignments, null);
            assert.include(output, "Layer 0");
        }
    });

    it("rejects a triangular piece on a straight three-cell grid", function () {
        const input =
            "#coordinates\n0,0,0\n1,0,0\n2,0,0\n#end of grid\n" +
            "#PieceA\n0,0,0\n1,0,0\n0,1,0\n#piece-End";
        assert.isFalse(solvePuzzleSphere(parsePuzzleSphere(input)).solved);
    });

    it("renders exact close-packed sphere positions", function () {
        assert.closeTo(latticePosition([0, 0, 1])[0], 5, 0.0001);
        assert.closeTo(latticePosition([0, 0, 1])[1], 2.886751, 0.0001);
        assert.closeTo(latticePosition([0, 0, 1])[2], 8.164966, 0.0001);
        const puzzle = new PuzzleSphere(TINY_TETRAHEDRON);
        puzzle.solve();
        const markup = new GraphItSphere().getSphereX3d(puzzle.result, "unit test");
        assert.include(markup, '<Sphere radius="5"/>');
        assert.include(markup, 'translation="5.000 2.887 8.165"');
        assert.include(markup, "height: calc(100vh - 60px)");
        assert.notInclude(markup, "x3d { width: 100%; height: 100%; }");
    });
});

describe("Sphere Solver page controls", function () {
    const workers = [];

    before(async function () {
        localStorage.removeItem("lastRunSphere");
        sessionStorage.removeItem("puzzleSphereSolvedState");
        const fixture = document.createElement("div");
        fixture.hidden = true;
        fixture.innerHTML = `
            <button id="solveButton"></button><button id="graphItButton"></button>
            <select id="PuzzleSelect">
                <option value="BallsPyramid900">BallsPyramid900</option>
                <option value="TinyTetrahedron">TinyTetrahedron</option>
                <option value="Custom">Custom</option>
            </select>
            <button id="restoreButton1"></button><button id="restoreButton2"></button><button id="restoreButton3"></button>
            <button id="saveButton1"></button><button id="saveButton2"></button><button id="saveButton3"></button>
            <textarea id="input"></textarea><textarea id="output"></textarea>
        `;
        document.body.append(fixture);
        window.Worker = class {
            constructor() { this.listeners = {}; workers.push(this); }
            addEventListener(type, listener) { this.listeners[type] = listener; }
            postMessage(message) { this.message = message; }
            terminate() { this.terminated = true; }
        };
        await import("../sourceSphere.js");
    });

    it("loads both sphere presets", function () {
        const select = document.getElementById("PuzzleSelect");
        select.value = "BallsPyramid900";
        select.dispatchEvent(new Event("change"));
        assert.equal(document.getElementById("input").value, BALLS_PYRAMID_900);
        select.value = "TinyTetrahedron";
        select.dispatchEvent(new Event("change"));
        assert.equal(document.getElementById("input").value, TINY_TETRAHEDRON);
    });

    it("submits and cancels a worker solve", function () {
        document.getElementById("solveButton").click();
        assert.deepEqual(workers.at(-1).message, { text: TINY_TETRAHEDRON });
        assert.equal(document.getElementById("output").value, "Solving…");
        document.getElementById("solveButton").click();
        assert.isTrue(workers.at(-1).terminated);
        assert.equal(document.getElementById("output").value, "Solve cancelled");
    });
});
