import { GraphItSphere } from "./graphItSphere.js";
import { BALLS_PYRAMID_900, TINY_TETRAHEDRON } from "./presetsSphere.js";

const solveButton = document.getElementById("solveButton");
const graphItButton = document.getElementById("graphItButton");
const dropdownButton = document.getElementById("PuzzleSelect");
const inputElement = document.getElementById("input");
const outputElement = document.getElementById("output");
const solvedStateKey = "puzzleSphereSolvedState";
let activeWorker = null;
let currentResult = null;

graphItButton.disabled = true;

function savePuzzle(key, value) {
    localStorage.setItem(key, value);
}

function loadPuzzle(key) {
    return localStorage.getItem(key) ?? "";
}

function saveSolvedState(input, output, result) {
    try {
        sessionStorage.setItem(solvedStateKey, JSON.stringify({
            input,
            output,
            result,
            title: dropdownButton.value
        }));
    } catch (error) {
        console.warn(`Could not preserve the solved sphere puzzle: ${error.message}`);
    }
}

function restoreSolvedState() {
    try {
        const savedState = JSON.parse(sessionStorage.getItem(solvedStateKey));
        if (!savedState || savedState.input !== inputElement.value || !savedState.result?.solved) {
            return;
        }
        currentResult = savedState.result;
        outputElement.value = savedState.output;
        graphItButton.disabled = false;
        if ([...dropdownButton.options].some((option) => option.value === savedState.title)) {
            dropdownButton.value = savedState.title;
        }
    } catch (error) {
        sessionStorage.removeItem(solvedStateKey);
        console.warn(`Could not restore the solved sphere puzzle: ${error.message}`);
    }
}

function cancelActiveSolve() {
    if (activeWorker !== null) {
        activeWorker.terminate();
        activeWorker = null;
        solveButton.textContent = "Solve";
    }
}

function loadDefinition(text) {
    cancelActiveSolve();
    currentResult = null;
    graphItButton.disabled = true;
    outputElement.value = "";
    inputElement.value = text;
}

dropdownButton.addEventListener("change", () => {
    if (dropdownButton.value === "BallsPyramid900") {
        loadDefinition(BALLS_PYRAMID_900);
    } else if (dropdownButton.value === "TinyTetrahedron" || dropdownButton.value === "Custom") {
        loadDefinition(TINY_TETRAHEDRON);
    }
});

for (let slot = 1; slot <= 3; slot++) {
    document.getElementById(`restoreButton${slot}`).addEventListener("click", () => {
        const saved = loadPuzzle(`spherePreset${slot}`);
        if (saved !== "") {
            loadDefinition(saved);
        }
    });
    document.getElementById(`saveButton${slot}`).addEventListener("click", () => {
        if (inputElement.value !== "") {
            savePuzzle(`spherePreset${slot}`, inputElement.value);
        }
    });
}

graphItButton.addEventListener("click", () => {
    if (currentResult?.solved) {
        try {
            new GraphItSphere().graphItSphere(currentResult, dropdownButton.value);
        } catch (error) {
            outputElement.value = `Render error: ${error.message}`;
        }
    }
});

solveButton.addEventListener("click", () => {
    if (activeWorker !== null) {
        cancelActiveSolve();
        outputElement.value = "Solve cancelled";
        return;
    }
    const input = inputElement.value;
    if (input.trim() === "") {
        outputElement.value = "Invalid input. Follow an example";
        return;
    }

    savePuzzle("lastRunSphere", input);
    sessionStorage.removeItem(solvedStateKey);
    currentResult = null;
    graphItButton.disabled = true;
    outputElement.value = "Solving…";
    solveButton.textContent = "Cancel";
    const worker = new Worker(new URL("./workerSphere.js", import.meta.url), { type: "module" });
    activeWorker = worker;

    const finish = () => {
        worker.terminate();
        if (activeWorker === worker) {
            activeWorker = null;
            solveButton.textContent = "Solve";
        }
    };

    worker.addEventListener("message", (event) => {
        if (activeWorker !== worker) {
            return;
        }
        if (event.data.type === "error") {
            outputElement.value = `Invalid input: ${event.data.error}`;
            finish();
            return;
        }
        currentResult = event.data.result;
        outputElement.value = event.data.output;
        graphItButton.disabled = !currentResult.solved;
        if (currentResult.solved) {
            saveSolvedState(input, outputElement.value, currentResult);
        }
        console.log(
            `Ended in ${currentResult.elapsedMs} msec. Tried placements ${currentResult.triedRows}`
        );
        finish();
    }, { once: true });

    worker.addEventListener("error", (event) => {
        if (activeWorker !== worker) {
            return;
        }
        outputElement.value = `Solver error: ${event.message}`;
        finish();
    }, { once: true });

    worker.postMessage({ text: input });
});

inputElement.value = loadPuzzle("lastRunSphere") || TINY_TETRAHEDRON;
restoreSolvedState();
