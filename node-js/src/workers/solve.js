const { parentPort, workerData } = require('worker_threads');
const Puzzle2D = require('../core/Puzzle2D');

function solve(problemId) {
  const [rows, columns] = problemId.split('_').map(Number);
  const puzzle2D = new Puzzle2D();
  puzzle2D.set(rows, columns);
  return puzzle2D.solve();
}

try {
  parentPort.postMessage({ result: solve(workerData.problemId) });
} catch (error) {
  parentPort.postMessage({ error: error.message });
}
