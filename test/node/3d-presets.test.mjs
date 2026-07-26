import { assert } from 'chai';

import {
  parsePuzzle3d,
  solvePuzzle3d
} from '../../docs/3d/puzzle3d.js';

describe('Puzzle3D browser presets', function () {
  const elements = new Map();
  const workers = [];
  const presetNames = [
    'Soma',
    'Graatsma',
    'Conway',
    'HappyCubeG',
    'HappyCubeR',
    'HappyCubeO'
  ];

  before(async function () {
    for (const id of [
      'solveButton',
      'graphItButton',
      'PuzzleSelect',
      'restoreButton1',
      'restoreButton2',
      'restoreButton3',
      'saveButton1',
      'saveButton2',
      'saveButton3',
      'input',
      'output'
    ]) {
      elements.set(id, {
        id,
        value: '',
        disabled: false,
        textContent: '',
        listeners: {},
        addEventListener(type, listener) {
          this.listeners[type] = listener;
        }
      });
    }
    globalThis.document = {
      getElementById(id) {
        return elements.get(id);
      }
    };
    globalThis.localStorage = {
      getItem() {
        return null;
      },
      setItem() {}
    };
    globalThis.Worker = class {
      constructor() {
        this.listeners = {};
        this.terminated = false;
        workers.push(this);
      }

      addEventListener(type, listener) {
        this.listeners[type] = listener;
      }

      postMessage(message) {
        this.message = message;
      }

      terminate() {
        this.terminated = true;
      }
    };
    await import('../../docs/3d/source3d.js');
  });

  it('sends textarea input to the solver worker', function () {
    const input = '#1,1,1\nx\n\n#end of grid\n#PieceA\nx\n\n#piece-End';
    elements.get('input').value = input;
    elements.get('solveButton').listeners.click();
    assert.equal(workers.length, 1);
    assert.deepEqual(workers[0].message, { text: input });
    assert.equal(elements.get('output').value, 'Solving…');
    elements.get('solveButton').listeners.click();
    assert.isTrue(workers[0].terminated);
  });

  for (const presetName of presetNames) {
    it(`solves ${presetName}`, function () {
      const select = elements.get('PuzzleSelect');
      select.value = presetName;
      select.listeners.change();
      const definition = parsePuzzle3d(elements.get('input').value);
      const result = solvePuzzle3d(definition);
      assert.isTrue(result.solved);
      assert.isAbove(result.assignments.length, 0);
      assert.notInclude(result.assignments, null);
    });
  }
});
