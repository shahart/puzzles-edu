from __future__ import annotations

import asyncio

from pyscript import document, when

from src.core.puzzle2d import Puzzle2D


rows_input = document.querySelector("#rows")
columns_input = document.querySelector("#columns")
solve_button = document.querySelector("#solve-button")
status = document.querySelector("#status")
result = document.querySelector("#result")
details = document.querySelector("#details")


def show_message(
    status_text: str,
    result_text: str = "",
    details_text: str = "",
    *,
    is_error: bool = False,
) -> None:
    status.textContent = status_text
    result.textContent = result_text
    details.textContent = details_text
    status.classList.toggle("error", is_error)


@when("click", "#solve-button")
async def solve_in_browser(event) -> None:
    try:
        rows = int(rows_input.value)
        columns = int(columns_input.value)
        if rows < 1 or columns < 1:
            raise ValueError("Rows and columns must both be positive.")
    except ValueError as error:
        show_message("Check the puzzle dimensions.", str(error), is_error=True)
        return

    solve_button.disabled = True
    show_message(f"Solving {rows} × {columns}…")
    print(f"Starting browser solve for {rows} × {columns}")

    # Yield once so the loading state is painted before the CPU-bound solver runs.
    await asyncio.sleep(0)

    try:
        puzzle = Puzzle2D()
        puzzle.set(rows, columns)
        fundamental_solutions = puzzle.solve()
        solutions = puzzle.get_solution_count()
        noun = "solution" if solutions == 1 else "solutions"
        tiling_noun = "tiling" if fundamental_solutions == 1 else "tilings"
        show_message(
            "Solved in this browser.",
            f"{solutions} {noun} found",
            (
                f"{fundamental_solutions} fundamental {tiling_noun}; "
                f"tried {puzzle.tried_pieces:,} piece placements."
            ),
        )
    except Exception as error:
        show_message("The solver could not run.", str(error), is_error=True)
    finally:
        solve_button.disabled = False


show_message("Python is ready.")
