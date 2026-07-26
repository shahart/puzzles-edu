const Puzzle3D = require('../../src/core/Puzzle3D');

test('has a solution for 3x4x5', () => {
  const puzzle3D = new Puzzle3D(3, 4, 5);

  expect(puzzle3D.solve()).toBe(1);
  expect(puzzle3D.totalSolutions).toBe(1);
  expect(puzzle3D.grid.every((cell) => cell > 0)).toBe(true);
}, 60000);
