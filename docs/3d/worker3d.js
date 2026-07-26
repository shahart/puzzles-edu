import {
    formatSolution,
    parsePuzzle3d,
    solvePuzzle3d
} from "./puzzle3d.js";

self.addEventListener("message", (event) => {
    try {
        const definition = parsePuzzle3d(event.data.text);
        const result = solvePuzzle3d(definition);
        self.postMessage({
            type: "result",
            result,
            output: formatSolution(result)
        });
    } catch (error) {
        self.postMessage({
            type: "error",
            error: error instanceof Error ? error.message : String(error)
        });
    }
});
