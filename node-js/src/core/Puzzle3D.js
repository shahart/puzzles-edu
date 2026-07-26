const ExactCoverCounter = require('./ExactCoverCounter');

class Puzzle3D {
  static PIECES = 12;

  static PENTOMINOES = [
    [[1], [1], [1], [1, 1]],
    [[1, 1], [1], [1, 1]],
    [[0, 1, 1], [1, 1], [0, 1]],
    [[0, 1], [1, 1, 1], [0, 1]],
    [[1, 1, 1, 1], [0, 0, 1]],
    [[0, 1], [1, 1], [1], [1]],
    [[0, 0, 1], [0, 1, 1], [1, 1]],
    [[1], [1, 1], [1, 1]],
    [[0, 0, 1], [1, 1, 1], [1]],
    [[0, 0, 1], [0, 0, 1], [1, 1, 1]],
    [[0, 0, 1], [1, 1, 1], [0, 0, 1]],
    [[1, 1, 1, 1, 1]]
  ];

  constructor(rows, columns, depth) {
    if (
      !Number.isInteger(rows) ||
      !Number.isInteger(columns) ||
      !Number.isInteger(depth) ||
      rows < 1 ||
      columns < 1 ||
      depth < 1
    ) {
      throw new Error('All dimensions must be positive integers');
    }

    this.rows = rows;
    this.columns = columns;
    this.depth = depth;
    this.cellCount = rows * columns * depth;
    this.totalSolutions = 0;
    this.triedPieces = 0;
    this.grid = new Array(this.cellCount).fill(0);
    this.placements = [];

    this.orientations = Puzzle3D.PENTOMINOES.map((layout) =>
      Puzzle3D.buildOrientations(layout)
    );
    this.buildPlacements();
  }

  solve() {
    this.totalSolutions = 0;
    this.triedPieces = 0;
    this.grid.fill(0);

    if (this.cellCount !== Puzzle3D.PIECES * 5) return 0;

    const exactCoverRows = this.placements.map((placement) => [
      ...placement.cells,
      this.cellCount + placement.piece
    ]);
    const result = ExactCoverCounter.findOne(
      this.cellCount + Puzzle3D.PIECES,
      exactCoverRows
    );
    this.triedPieces = result.triedRows;
    this.totalSolutions = result.solutions;
    for (const rowIndex of result.selectedRows) {
      const placement = this.placements[rowIndex];
      for (const cell of placement.cells) {
        this.grid[cell] = placement.piece + 1;
      }
    }
    return this.totalSolutions > 0 ? 1 : 0;
  }

  buildPlacements() {
    if (this.cellCount !== Puzzle3D.PIECES * 5) return;

    for (let piece = 0; piece < Puzzle3D.PIECES; piece++) {
      for (const orientation of this.orientations[piece]) {
        const maxRow = Math.max(...orientation.map((cube) => cube[0]));
        const maxColumn = Math.max(...orientation.map((cube) => cube[1]));
        const maxDepth = Math.max(...orientation.map((cube) => cube[2]));

        for (let rowOffset = 0; rowOffset + maxRow < this.rows; rowOffset++) {
          for (
            let columnOffset = 0;
            columnOffset + maxColumn < this.columns;
            columnOffset++
          ) {
            for (
              let depthOffset = 0;
              depthOffset + maxDepth < this.depth;
              depthOffset++
            ) {
              const cells = orientation.map(([row, column, depth]) => {
                const placedRow = rowOffset + row;
                const placedColumn = columnOffset + column;
                const placedDepth = depthOffset + depth;
                return (
                  (placedRow * this.columns + placedColumn) * this.depth + placedDepth
                );
              });
              const placement = { piece, cells };
              this.placements.push(placement);
            }
          }
        }
      }
    }
  }

  static buildOrientations(layout) {
    const cubes = [];
    for (let row = 0; row < layout.length; row++) {
      for (let column = 0; column < layout[row].length; column++) {
        if (layout[row][column] === 1) cubes.push([row, column, 0]);
      }
    }

    const permutations = [
      [0, 1, 2],
      [0, 2, 1],
      [1, 0, 2],
      [1, 2, 0],
      [2, 0, 1],
      [2, 1, 0]
    ];
    const orientations = [];
    const seen = new Set();

    for (const permutation of permutations) {
      const permutationSign = Puzzle3D.permutationSign(permutation);
      for (const firstSign of [-1, 1]) {
        for (const secondSign of [-1, 1]) {
          for (const thirdSign of [-1, 1]) {
            if (permutationSign * firstSign * secondSign * thirdSign !== 1) continue;

            const signs = [firstSign, secondSign, thirdSign];
            const transformed = cubes.map((cube) =>
              signs.map((sign, axis) => sign * cube[permutation[axis]])
            );
            Puzzle3D.normalizeAndSort(transformed);
            const key = transformed.map((cube) => cube.join(',')).join(';');
            if (!seen.has(key)) {
              seen.add(key);
              orientations.push(transformed);
            }
          }
        }
      }
    }
    return orientations;
  }

  static permutationSign(permutation) {
    let inversions = 0;
    for (let first = 0; first < permutation.length; first++) {
      for (let second = first + 1; second < permutation.length; second++) {
        if (permutation[first] > permutation[second]) inversions++;
      }
    }
    return inversions % 2 === 0 ? 1 : -1;
  }

  static normalizeAndSort(cubes) {
    const minimum = [Infinity, Infinity, Infinity];
    for (const cube of cubes) {
      for (let axis = 0; axis < 3; axis++) {
        minimum[axis] = Math.min(minimum[axis], cube[axis]);
      }
    }
    for (const cube of cubes) {
      for (let axis = 0; axis < 3; axis++) {
        cube[axis] -= minimum[axis];
      }
    }
    cubes.sort((left, right) => {
      for (let axis = 0; axis < 3; axis++) {
        if (left[axis] !== right[axis]) return left[axis] - right[axis];
      }
      return 0;
    });
  }
}

module.exports = Puzzle3D;
