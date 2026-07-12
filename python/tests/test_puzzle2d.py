import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

from src.core.piece import Piece
from src.core.puzzle2d import Puzzle2D


def setup_function():
    Piece.total_fill = 0


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
