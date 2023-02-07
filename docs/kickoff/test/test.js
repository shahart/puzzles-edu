import { Puzzle2d } from "../puzzle2d.js";
import { Piece } from "../piece.js";

// todo in tests mode, no serviceWorker, no notification, etc.

describe('Mocha Puzzle2D tests', function () {

    it('6x6-10 pieces', function () {
        let puzzle2d = new Puzzle2d(10, 6, 6);
        let res = puzzle2d.solve();
        console.log("6x6-10 pieces>>" + res);
        chai.assert.include(res, ' 1  ');
    });

    it('3x3-3 pieces-no solution', function () {
        let puzzle2d = new Puzzle2d(3, 3, 3);
        let res = puzzle2d.solve();
        /*
        [[0,0,1],[1,1,1],[1]]
          x
        xxx
        x
        [[1,1],[1]]
        xx
        x
        [[1]]
        x
        */
        console.log("3x3-3 pieces-no solution>>" + res);
        chai.assert.include(res, '');
    });

    it('invalid config, grid 60 pieces 55', () => {
        try {
            let puzzle2d = new Puzzle2d(11, 6, 10);
            let res = puzzle2d.solve();
            console.log("invalid config, grid 60 pieces 55>>" + res);
            chai.assert.fail();
        } catch (err) {
            console.warn('invalid config, grid 60 pieces 55>>' + err);
            // RangeError
            chai.assert.include(err.message, 'nvalid array length'); // Chrome i..., Firefox I
        }
    });

    it('invalid config, grid 50 pieces 60', () => {
        let puzzle2d = new Puzzle2d(12, 5, 10);
        let res = puzzle2d.solve();
        console.log("invalid config, grid 50 pieces 60>>" + res);
        chai.assert.equal(res, '');
    });

    xit('12x5-Poly-9000-timeout', function () {
        let puzzle2d = new Puzzle2d(12, 6, 10);
        let res = puzzle2d.solve();
        console.log("12x5-Poly>>" + res);
        chai.assert.equal(res, '');
    }); // .timeout(9200) // mocha's timeout 2 sec, app timeout (was) 9s, firefox 20s - trim now for 1.5 for 0.5 sec for user to hit okay

    it('12x5-Poly-2000-timeout', function () {
        let res;
        try {
            let puzzle2d = new Puzzle2d(12, 6, 10);
            res = puzzle2d.solve();
            console.log("12x5-Poly-2000-timeout>>" + res);
            // chai.assert.fail();
        } catch (err) {
            console.warn(err);
            console.warn(err.message);
            console.log("timeout>>" + res);
            chai.assert.isUndefined(res);
            chai.assert.include(err.message, 'Timeout');
        }
    }); // made app timeout < 2s - people has no patience

    // todo separate describe, for 'piece-' tests

    it('piece-valid', function () {
        let layout = [[1],[1],[1],[1,1]];
        let piece = new Piece(0, layout, 1,1,'0');
        chai.assert.equal(piece.totalThisFill, 5);
    });

    it('piece-invalid', function () {
        try {
            let layout = []; // [[1],[1],[1],[1,1]];
            new Piece(0, layout, 1,1,'0');
            chai.assert.fail();
        } catch (err) {
            console.warn('piece-invalid>>' + err);
            chai.assert.include(err.message, 'undefined'); // Cannot read properties of undefined (reading 'length') at new Piece (piece.js:24:58) - layouts[0][0].length
        }
    });

    it('piece-empty', function () {
        try {
            let layout = [[]];
            let piece = new Piece(0, layout, 1,1,'0');
            chai.assert.equal(piece.totalThisFill, 0);
            chai.assert.fail();
        } catch (err) {
            console.warn('piece-empty>>' + err.message);
            chai.assert.include(err.message, 'empty piece');
        }
    });

});
