import path from 'node:path';
import { pathToFileURL } from 'node:url';

// Programmatic Mocha runner to avoid Windows ESM `--import c:\...` issues.
const { default: Mocha } = await import('mocha');

const mocha = new Mocha({
  timeout: 120_000
});

// Load setup first (as ESM, via file:// URL)
await import(pathToFileURL(path.resolve('test/node/setup.mjs')).href);

// Add test files
mocha.addFile(path.resolve('test/node/kickoff.test.mjs'));
mocha.addFile(path.resolve('test/node/3d.test.mjs'));

mocha.run((failures) => {
  process.exitCode = failures ? 1 : 0;
});

