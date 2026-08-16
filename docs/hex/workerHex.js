import {
    formatHexSolution,
    parsePuzzleHex,
    solvePuzzleHex
} from "./puzzleHex.js";

self.addEventListener("message", (event) => {
    try {
        const definition = parsePuzzleHex(event.data.text);
        const result = solvePuzzleHex(definition);
        self.postMessage({
            type: "result",
            result,
            output: formatHexSolution(result)
        });
    } catch (error) {
        self.postMessage({
            type: "error",
            error: error instanceof Error ? error.message : String(error)
        });
    }
});
