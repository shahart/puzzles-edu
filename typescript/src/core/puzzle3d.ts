import { ExactCoverCounter } from './exact-cover-counter';

type Cube = [number, number, number];

interface Placement {
  piece: number;
  cells: number[];
}

export class Puzzle3D {
  private static readonly PIECES = 12;
  private static readonly PENTOMINOES: number[][][] = [
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
    [[1, 1, 1, 1, 1]],
  ];

  readonly grid: number[];
  totalSolutions = 0;
  triedPieces = 0;

  private readonly cellCount: number;
  private readonly placements: Placement[] = [];
  private readonly orientations: Cube[][][];

  constructor(
    private readonly rows: number,
    private readonly columns: number,
    private readonly depth: number,
  ) {
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

    this.cellCount = rows * columns * depth;
    this.grid = new Array(this.cellCount).fill(0);
    this.orientations = Puzzle3D.PENTOMINOES.map((layout) =>
      Puzzle3D.buildOrientations(layout),
    );
    this.buildPlacements();
  }

  solve(): number {
    this.totalSolutions = 0;
    this.triedPieces = 0;
    this.grid.fill(0);

    if (this.cellCount !== Puzzle3D.PIECES * 5) {
      return 0;
    }

    const exactCoverRows = this.placements.map((placement) => [
      ...placement.cells,
      this.cellCount + placement.piece,
    ]);
    const result = ExactCoverCounter.findOne(
      this.cellCount + Puzzle3D.PIECES,
      exactCoverRows,
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

  private buildPlacements(): void {
    if (this.cellCount !== Puzzle3D.PIECES * 5) {
      return;
    }

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
                  (placedRow * this.columns + placedColumn) * this.depth +
                  placedDepth
                );
              });
              this.placements.push({ piece, cells });
            }
          }
        }
      }
    }
  }

  private static buildOrientations(layout: number[][]): Cube[][] {
    const cubes: Cube[] = [];
    for (let row = 0; row < layout.length; row++) {
      for (let column = 0; column < layout[row].length; column++) {
        if (layout[row][column] === 1) {
          cubes.push([row, column, 0]);
        }
      }
    }

    const permutations: Cube[] = [
      [0, 1, 2],
      [0, 2, 1],
      [1, 0, 2],
      [1, 2, 0],
      [2, 0, 1],
      [2, 1, 0],
    ];
    const orientations: Cube[][] = [];
    const seen = new Set<string>();

    for (const permutation of permutations) {
      const permutationSign = Puzzle3D.permutationSign(permutation);
      for (const firstSign of [-1, 1]) {
        for (const secondSign of [-1, 1]) {
          for (const thirdSign of [-1, 1]) {
            if (
              permutationSign * firstSign * secondSign * thirdSign !==
              1
            ) {
              continue;
            }

            const signs = [firstSign, secondSign, thirdSign];
            const transformed = cubes.map(
              (cube): Cube => [
                signs[0] * cube[permutation[0]],
                signs[1] * cube[permutation[1]],
                signs[2] * cube[permutation[2]],
              ],
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

  private static permutationSign(permutation: Cube): number {
    let inversions = 0;
    for (let first = 0; first < permutation.length; first++) {
      for (let second = first + 1; second < permutation.length; second++) {
        if (permutation[first] > permutation[second]) {
          inversions++;
        }
      }
    }
    return inversions % 2 === 0 ? 1 : -1;
  }

  private static normalizeAndSort(cubes: Cube[]): void {
    const minimum: Cube = [Infinity, Infinity, Infinity];
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
        if (left[axis] !== right[axis]) {
          return left[axis] - right[axis];
        }
      }
      return 0;
    });
  }
}
