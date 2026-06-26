const express = require('express');
const Puzzle2D = require('../core/Puzzle2D');
const Piece = require('../core/Piece');

const router = express.Router();

router.get('/solve/:problemId', (req, res) => {
  const { problemId } = req.params;
  console.log(`Starting id ${problemId}`);

  const rowsCols = problemId.split('_');
  const puzzle2D = new Puzzle2D();
  Piece.totalFill = 0;
  puzzle2D.set(parseInt(rowsCols[0], 10), parseInt(rowsCols[1], 10));

  const abortController = new AbortController();
  const timeout = setTimeout(() => abortController.abort(), 5000);

  try {
    const result = puzzle2D.solve(abortController.signal);
    console.log(`Done id ${problemId} with result ${result}`);
    res.json(result);
  } catch (err) {
    console.error('Solve error:', err);
    res.status(500).json({ error: err.message });
  } finally {
    clearTimeout(timeout);
  }
});

module.exports = router;
