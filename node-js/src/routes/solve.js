const express = require('express');
const path = require('path');
const { Worker } = require('worker_threads');

const router = express.Router();
const SOLVE_TIMEOUT_MS = 5000;
const workerPath = path.join(__dirname, '../workers/solve.js');

function solveInWorker(problemId, countSolutions) {
  return new Promise((resolve, reject) => {
    const worker = new Worker(workerPath, { workerData: { problemId, countSolutions } });
    let settled = false;

    const finish = (callback, value) => {
      if (settled) return;
      settled = true;
      clearTimeout(timeout);
      callback(value);
    };

    const timeout = setTimeout(() => {
      finish(reject, new Error(`Solve timed out after ${SOLVE_TIMEOUT_MS}ms`));
      void worker.terminate();
    }, SOLVE_TIMEOUT_MS);

    worker.once('message', ({ result, error }) => {
      if (error) {
        finish(reject, new Error(error));
      } else {
        finish(resolve, result);
      }
    });
    worker.once('error', (error) => finish(reject, error));
    worker.once('exit', (code) => {
      if (code !== 0) {
        finish(reject, new Error(`Solve worker stopped with exit code ${code}`));
      }
    });
  });
}

router.get('/solve/:problemId', async (req, res) => {
  const { problemId } = req.params;
  const countSolutions = req.query.count === 'true';
  console.log(`Starting id ${problemId}`);

  try {
    const result = await solveInWorker(problemId, countSolutions);
    console.log(`Done id ${problemId} with result ${result}`);
    res.json(result);
  } catch (err) {
    console.error('Solve error:', err);
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
