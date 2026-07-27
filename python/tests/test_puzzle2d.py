import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

from src.core.puzzle2d import Puzzle2D


def test_has_solution_for_12x5():
    puzzle2d = Puzzle2D()
    puzzle2d.set(12, 5)
    assert puzzle2d.solve() == 1


def test_has_solution_for_10x6():
    puzzle2d = Puzzle2D()
    puzzle2d.set(10, 6)
    assert puzzle2d.solve() == 1


def test_no_solution_for_5x13():
    puzzle2d = Puzzle2D()
    puzzle2d.set(5, 13)
    assert puzzle2d.solve() == 0


def test_rotates_wide_board_to_the_faster_equivalent_orientation():
    puzzle2d = Puzzle2D()
    puzzle2d.set(3, 20)

    assert (puzzle2d.ROWS, puzzle2d.COLUMNS) == (20, 3)
    assert puzzle2d.solve() == 1
    assert puzzle2d.get_solution_count() == 4
