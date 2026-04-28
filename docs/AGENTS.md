# Repository Guidelines

## Project Structure & Module Organization
This `docs/` folder is a static frontend area for puzzle demos and generated solver pages. Top-level pages such as `index.html`, `more.html`, `2dsolver.html`, and `3dsolver.html` are the main entry points. Shared browser scripts live beside them in files like `index.js`, `site.js`, and `sw.js`. The `kickoff/` and `3d/` directories contain hand-edited solver code and browser test pages (`test/test.html`, `test/test.js`). The `x3d/` directory contains generated puzzle pages and should be treated as output unless a task explicitly targets regenerated artifacts.

## Build, Test, and Development Commands
There is no local package build in `docs/`; changes are usually edited directly and verified in a browser.

- `python -m http.server 8000` from `docs/`: serve the site locally so pages and the service worker behave correctly.
- Open `http://localhost:8000/`: verify the landing page and navigation.
- Open `http://localhost:8000/kickoff/test/test.html`: run the 2D browser test page.
- Open `http://localhost:8000/3d/test/test.html`: run the 3D browser test page.

If a change depends on backend-generated pages, coordinate with the repository root workflow instead of inventing a docs-only build step.

## Coding Style & Naming Conventions
Match the surrounding file style instead of reformatting aggressively. Existing HTML uses simple inline structure; JavaScript is plain ES modules with descriptive class names such as `Builder` and `Piece`. Preserve current indentation per file, keep identifiers in `camelCase`, classes in `UpperCamelCase`, and filenames lowercase where already established (`piece3d.js`, `graphIt.js`).

## Testing Guidelines
Prefer targeted browser verification over broad rewrites. For UI or solver-script changes, test the affected HTML page plus the matching `test/test.html` harness when available. Check the browser console for script and service worker errors. When editing generated `x3d/` pages, confirm links load and rendering still works.

## Commit & Pull Request Guidelines
Keep commit subjects short and imperative, consistent with nearby history such as `allow back on mobile` or `Update README.md. Fix 404`. Pull requests should summarize the visible change, list the pages tested, and include screenshots only when layout or rendering changed.

## Docs Folder Notes
Avoid mixing unrelated root-repo changes into a docs-only PR. Treat `favicon.ico`, `manifest.json`, and `sw.js` as deployment-facing assets: small edits there can affect the whole static site.
