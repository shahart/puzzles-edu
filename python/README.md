# Python implementation

The Python puzzle solver can run either behind Flask or directly in a browser
through PyScript.

## Run in the browser

From the repository root:

```sh
python3 -m http.server 8000 --directory python
```

Then open <http://localhost:8000>. The page downloads the pinned PyScript
runtime once, loads the existing modules under `src/core/`, and performs the
solve locally in the browser.

Serving the folder over HTTP is required. Opening `index.html` as a `file://`
URL prevents PyScript from fetching the Python modules.

## Run the Flask API

From the `python` directory:

```sh
python3 -m pip install -r requirements.txt
python3 src/app.py
```

Then request a puzzle such as <http://localhost:8080/solve/5_12>.
