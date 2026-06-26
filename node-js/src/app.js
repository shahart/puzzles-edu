const express = require('express');
const solveRouter = require('./routes/solve');

const app = express();
const PORT = process.env.PORT || 8080;

app.use(express.json());
app.use('/', solveRouter);

if (require.main === module) {
  app.listen(PORT, () => {
    console.log(`Puzzle solver server running on http://localhost:${PORT}`);
  });
}

module.exports = app;
