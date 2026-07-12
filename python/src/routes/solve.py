import threading

from flask import Blueprint, jsonify

from src.core.piece import Piece
from src.core.puzzle2d import Puzzle2D

solve_bp = Blueprint("solve", __name__)


@solve_bp.route("/solve/<problem_id>")
def solve(problem_id: str):
    print(f"Starting id {problem_id}")

    rows_cols = problem_id.split("_")
    puzzle2d = Puzzle2D()
    Piece.total_fill = 0
    puzzle2d.set(int(rows_cols[0]), int(rows_cols[1]))

    abort_event = threading.Event()

    def timeout_handler() -> None:
        abort_event.set()

    timer = threading.Timer(5.0, timeout_handler)
    timer.start()

    try:
        result = puzzle2d.solve(abort_event)
        print(f"Done id {problem_id} with result {result}")
        return jsonify(result)
    except Exception as err:
        print(f"Solve error: {err}")
        return jsonify({"error": str(err)}), 500
    finally:
        timer.cancel()
