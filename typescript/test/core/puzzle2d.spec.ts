import { Puzzle2D } from '../../src/core/puzzle2d';
import { Piece } from '../../src/core/piece';

beforeEach(() => {
  Piece.totalFill = 0;
});

test('has solution for 12x5', () => {
  const puzzle2D = new Puzzle2D();
  puzzle2D.set(12, 5);
  expect(puzzle2D.solve()).toBe(1);
}, 60000);

test('has solution for 10x6', () => {
  const puzzle2D = new Puzzle2D();
  puzzle2D.set(10, 6);
  expect(puzzle2D.solve()).toBe(1);
}, 60000);

test('no solution for 5x13', () => {
  const puzzle2D = new Puzzle2D();
  puzzle2D.set(5, 13);
  expect(puzzle2D.solve()).toBe(0);
}, 60000);
