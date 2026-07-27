import json
from pathlib import Path


PYTHON_ROOT = Path(__file__).parent.parent


def test_pyscript_config_references_existing_source_files():
    config = json.loads((PYTHON_ROOT / "pyscript.json").read_text())

    assert config["files"]
    for source, destination in config["files"].items():
        assert (PYTHON_ROOT / source).is_file()
        assert destination.startswith("./src/")


def test_pyscript_entrypoint_compiles():
    source = (PYTHON_ROOT / "pyscript_app.py").read_text()

    compile(source, "pyscript_app.py", "exec")
    assert '@when("click", "#solve-button")' in source


def test_browser_form_cannot_navigate_away_from_results():
    page = (PYTHON_ROOT / "index.html").read_text()

    assert '<form id="solver-form" onsubmit="return false;">' in page
    assert '<button id="solve-button" type="button">' in page
