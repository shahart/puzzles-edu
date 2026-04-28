import { assert } from 'chai';

import { Puzzle3d } from '../../docs/3d/puzzle3d.js';
import { Piece3d } from '../../docs/3d/piece3d.js';

describe('Puzzle3D (Node)', function () {
  it.skip('3x4x5-12 pieces', function () {
    const puzzle3d = new Puzzle3d(12, 5, 4, 3);
    const res = puzzle3d.solve();
    assert.include(res, ' 1  ');
  });

  it('1x1x1-1 piece', function () {
    const input =
      '#1,1,1\n' +
      'x\n' +
      '\n' +
      '#end of grid\n' +
      '#PieceA\n' +
      'x\n' +
      '\n' +
      '#piece-End\n';
    const puzzle3d = new Puzzle3d(1, 1, 1, 1, input);
    const res = puzzle3d.solve();
    assert.include(res, ' 1  ');
  });

  it('3x3x1-3 pieces-no solution', function () {
    const input =
      '#3,3,1\n' +
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
    const puzzle3d = new Puzzle3d(3, 3, 3, 1, input);
    const res = puzzle3d.solve();
    assert.equal(res, 'Found no solution');
  });

  it('3x3x1-3 pieces', function () {
    const input =
      '#3,3,1\n' +
      'xxx\n' +
      'xxx\n' +
      'xxx\n' +
      '\n' +
      '#end of grid\n' +
      '#pieceC\n' +
      'x\n' +
      '\n' +
      '#PieceB\n' +
      '  x\n' +
      'xxx\n' +
      'xx\n' +
      '\n' +
      '#pieceA\n' +
      'xx\n' +
      '\n' +
      '#piece-End\n';
    const puzzle3d = new Puzzle3d(3, 3, 3, 1, input);
    const res = puzzle3d.solve();
    assert.include(res, ' 1  ');
  });

  it('invalid config, grid 60 pieces 55', function () {
    const puzzle3d = new Puzzle3d(11, 3, 4, 5);
    const res = puzzle3d.solve();
    assert.equal(res, 'Invalid input');
  });

  it('invalid config, grid 3x4x4=48 pieces 60', function () {
    const puzzle3d = new Puzzle3d(12, 3, 4, 4);
    const res = puzzle3d.solve();
    assert.equal(res, 'Invalid input');
  });

  it('piece-valid', function () {
    const layout = [[[1], [1], [1], [1, 1]]];
    const piece = new Piece3d(0, layout, '0');
    assert.equal(piece.totalThisFill, 5);
  });

  it('piece-invalid', function () {
    try {
      const layout = undefined;
      // eslint-disable-next-line no-new
      new Piece3d(0, layout, '0');
      assert.fail('expected constructor to throw');
    } catch (err) {
      assert.include(String(err?.message ?? err), 'undefined');
    }
  });

  it('piece-empty', function () {
    try {
      const layout = [[[]]];
      const piece = new Piece3d(0, layout, '0');
      assert.equal(piece.totalThisFill, 0);
      assert.fail('expected constructor to throw');
    } catch (err) {
      assert.include(String(err?.message ?? err), 'empty piece');
    }
  });
});

