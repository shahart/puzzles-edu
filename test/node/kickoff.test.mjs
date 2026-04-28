import { assert } from 'chai';

import { Puzzle2d } from '../../docs/kickoff/puzzle2d.js';
import { Piece } from '../../docs/kickoff/piece.js';

describe('Puzzle2D (Node)', function () {
  it('10x6-12 pieces', function () {
    const puzzle2d = new Puzzle2d(12, 10, 6);
    const res = puzzle2d.solve();
    assert.include(res, ' 1  ');
  });

  it('3x3-3 pieces-no solution', function () {
    const input =
      '#3,3\n' +
      'XXX\n' +
      'xxX\n' +
      'xxx\n' +
      '\n' +
      '#end of grid\n' +
      '#PieceA\n' +
      '  X\n' +
      'xxX\n' +
      'X\n' +
      '\n' +
      '#pieceB\n' +
      'xx\n' +
      'x\n' +
      '\n' +
      '#pieceC\n' +
      'x\n' +
      '\n' +
      '#piece-End\n';
    const puzzle2d = new Puzzle2d(3, 3, 3, input);
    const res = puzzle2d.solve();
    assert.equal(res, 'Found no solution');
  });

  it('invalid config, grid 60 pieces 55', function () {
    const puzzle2d = new Puzzle2d(11, 6, 10);
    const res = puzzle2d.solve();
    assert.equal(res, 'Invalid input');
  });

  it('invalid config, grid 50 pieces 60', function () {
    const puzzle2d = new Puzzle2d(12, 5, 10);
    const res = puzzle2d.solve();
    assert.equal(res, 'Invalid input');
  });

  it('piece-valid', function () {
    const layout = [[1], [1], [1], [1, 1]];
    const piece = new Piece(0, layout, 1, 1, '0');
    assert.equal(piece.totalThisFill, 5);
  });

  it('piece-invalid', function () {
    try {
      const layout = [];
      // eslint-disable-next-line no-new
      new Piece(0, layout, 1, 1, '0');
      assert.fail('expected constructor to throw');
    } catch (err) {
      assert.include(String(err?.message ?? err), 'undefined');
    }
  });

  it('piece-empty', function () {
    try {
      const layout = [[]];
      const piece = new Piece(0, layout, 1, 1, '0');
      assert.equal(piece.totalThisFill, 0);
      assert.fail('expected constructor to throw');
    } catch (err) {
      assert.include(String(err?.message ?? err), 'empty piece');
    }
  });
});

