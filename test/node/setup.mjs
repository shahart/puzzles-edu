// Node test shims for the browser-oriented solver code in `docs/`.
// - Many modules assume `window` exists (e.g. `window.globalTotalFill = 0`)
// - Some code calls `alert()` for UI; in tests we don't want modal behavior or ReferenceErrors.

globalThis.window = globalThis;
globalThis.__PUZZLES_EDU_TEST__ = true;

if (typeof globalThis.alert !== 'function') {
  globalThis.alert = () => {};
}

