const { parentPort, workerData } = require('worker_threads');
const Puzzle2D = require('../core/Puzzle2D');
const Puzzle3D = require('../core/Puzzle3D');

function solve(problemId, countSolutions, dimensions) {
  const sizes = problemId.split('_').map(Number);
  if (dimensions === 3) {
    const puzzle3D = new Puzzle3D(sizes[0], sizes[1], sizes[2]);
    return puzzle3D.solve();
  }

  const [rows, columns] = sizes;
  const puzzle2D = new Puzzle2D();
  puzzle2D.set(rows, columns);
  return puzzle2D.solve(undefined, countSolutions);
}

try {
  parentPort.postMessage({
    result: solve(
      workerData.problemId,
      workerData.countSolutions,
      workerData.dimensions
    )
  });
} catch (error) {
  parentPort.postMessage({ error: error.message });
}
