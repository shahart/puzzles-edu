import {
    formatSphereSolution,
    parsePuzzleSphere,
    solvePuzzleSphere
} from "./puzzleSphere.js";

self.addEventListener("message", (event) => {
    try {
        const definition = parsePuzzleSphere(event.data.text);
        const result = solvePuzzleSphere(definition);
        self.postMessage({
            type: "result",
            result,
            output: formatSphereSolution(result)
        });
    } catch (error) {
        self.postMessage({
            type: "error",
            error: error instanceof Error ? error.message : String(error)
        });
    }
});
