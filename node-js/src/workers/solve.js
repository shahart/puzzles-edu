const { parentPort, workerData } = require('worker_threads');
const Puzzle2D = require('../core/Puzzle2D');

function solve(problemId, countSolutions) {
  const [rows, columns] = problemId.split('_').map(Number);
  const puzzle2D = new Puzzle2D();
  puzzle2D.set(rows, columns);
  return puzzle2D.solve(undefined, countSolutions);
}

try {
  parentPort.postMessage({
    result: solve(workerData.problemId, workerData.countSolutions)
  });
} catch (error) {
  parentPort.postMessage({ error: error.message });
}
