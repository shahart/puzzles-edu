from __future__ import annotations

import time
from typing import Optional

from .piece import Piece


class Puzzle2D:
    def __init__(self) -> None:
        self.PIECES = 12
        self.ROWS = 0
        self.COLUMNS = 0
        self.total_solutions = 0
        self.tried_pieces = 0
        self.row = 0
        self.column = 0
        self.pieces_indices: list[int] = []
        self.solution: list[int] = [0] * self.PIECES
        self.pieces: list[Optional[Piece]] = [None] * self.PIECES
        self.names = ""
        self.grid: list[list[int]] = []
        self.curr_piece: Optional[Piece] = None
        self.total_fill_in_grid = 0
        self.total_fill_in_pieces = 0
        self.avail_in_grid = 0
        self._aborted = False

    def set(self, rows: int, columns: int) -> None:
        # A rectangular board and its 90-degree rotation have the same tilings.
        # Search the taller orientation because the row-major backtracking order
        # is dramatically faster for narrow boards such as 20x3.
        if columns > rows:
            rows, columns = columns, rows

        self.pieces_indices = []
        self.solution = [0] * self.PIECES
        self.total_solutions = 0
        self.tried_pieces = 0
        self.total_fill_in_pieces = 0
        self._aborted = False

        self.ROWS = rows
        self.COLUMNS = columns

        self.grid = [[0] * self.COLUMNS for _ in range(self.ROWS)]

        all_pieces = [
            [[1], [1], [1], [1, 1]],
            [[1, 1], [1], [1, 1]],
            [[0, 1, 1], [1, 1], [0, 1]],
            [[0, 1], [1, 1, 1], [0, 1]],
            [[1, 1, 1, 1], [0, 0, 1]],
            [[0, 1], [1, 1], [1], [1]],
            [[0, 0, 1], [0, 1, 1], [1, 1]],
            [[1], [1, 1], [1, 1]],
            [[0, 0, 1], [1, 1, 1], [1]],
            [[0, 0, 1], [0, 0, 1], [1, 1, 1]],
            [[0, 0, 1], [1, 1, 1], [0, 0, 1]],
            [[1, 1, 1, 1, 1]],
        ]

        rotations = [4, 4, 2, 1, 4, 4, 4, 4, 2, 4, 4, 2]
        symmetric = [2, 1, 1, 1, 2, 2, 1, 2, 2, 1, 1, 1]
        self.names = "LUFXYNWPZVTI"

        if self.ROWS == self.COLUMNS and self.ROWS == 8:
            rotations[2] = 1

        if self.ROWS == 0 and self.COLUMNS == 0:
            raise ValueError("not supported yet")
        else:
            for i in range(self.PIECES):
                self.pieces_indices.append(i)
                self.pieces[i] = Piece(
                    i, all_pieces[i], rotations[i], symmetric[i], self.names[i]
                )
                self.total_fill_in_pieces += self.pieces[i].total_this_fill
            if self.ROWS == self.COLUMNS and self.ROWS == 8:
                self.grid[0][0] = -1
                self.grid[7][0] = -1
                self.grid[0][7] = -1
                self.grid[7][7] = -1
            while self.grid[0][self.column] == -1:
                self.column += 1

        self.total_fill_in_grid = self.ROWS * self.COLUMNS
        for row in self.grid:
            for val in row:
                if val == -1:
                    self.total_fill_in_grid -= 1

        self.avail_in_grid = self.total_fill_in_grid
        print(
            f"Found {self.ROWS} rows, {self.COLUMNS} cols, "
            f"with total of cells {self.avail_in_grid}"
        )
        self.show_grid()

    def show_grid(self) -> None:
        print(
            f"[msec] showGrid. Tried Pieces {self.tried_pieces} "
            f"leftPieces {len(self.pieces_indices)}"
        )
        for i in range(self.ROWS):
            line = ""
            for j in range(self.COLUMNS):
                if self.grid[i][j] == -1:
                    line += "*  "
                elif self.grid[i][j] == 0:
                    line += "-  "
                else:
                    line += f"{self.names[self.grid[i][j] - 1]} "
            print(line)

    def show_pieces(self) -> None:
        line = ""
        for i in range(self.PIECES - len(self.pieces_indices)):
            line += f"{self.names[self.solution[i]]} "
        print(line)

    def put(self) -> None:
        left_pieces = len(self.pieces_indices)

        if left_pieces == 0:
            self.total_solutions += 1
            print(f"totalSolutions {self.total_solutions}")
            if self.total_solutions == 1:
                print("Found a solution")
                self.show_grid()
                self.show_pieces()

        if self.total_solutions >= 1:
            return

        rows_set = [0] * 5
        columns_set = [0] * 5

        for i in range(left_pieces):
            piece_idx = self.pieces_indices[i]
            self.curr_piece = self.pieces[piece_idx]
            for r in range(self.curr_piece.get_avail_rotations(), 0, -1):
                if self.can_put(rows_set, columns_set):
                    self.pieces_indices.pop(i)
                    self.solution[self.PIECES - left_pieces] = piece_idx
                    self.put_curr_piece(rows_set, columns_set)

                    if self.tried_pieces % 50000 == 0:
                        self.show_grid()
                        self.show_pieces()

                        if self._aborted:
                            print(
                                f"Id {self.ROWS}_{self.COLUMNS}. "
                                f"Signaled timed out! totalSolutions {self.total_solutions}"
                            )
                            return

                    self.put()
                    self.remove_last(piece_idx, rows_set, columns_set)
                    self.pieces_indices.insert(i, piece_idx)
                self.curr_piece.rotate()

    def put_curr_piece(self, rows_set: list[int], columns_set: list[int]) -> None:
        assert self.curr_piece is not None
        self.curr_piece.set_position(self.row, self.column)

        curr_index = self.curr_piece.index + 1
        for i in range(self.curr_piece.total_this_fill):
            self.grid[rows_set[i]][columns_set[i]] = curr_index

        self.go_forward()
        self.avail_in_grid -= self.curr_piece.total_this_fill

    def go_forward(self) -> None:
        while self.row < self.ROWS:
            while self.column < self.COLUMNS:
                if self.grid[self.row][self.column] == 0:
                    return
                self.column += 1
            self.column = 0
            self.row += 1

    def remove_last(
        self, piece: int, rows_set: list[int], columns_set: list[int]
    ) -> None:
        assert self.pieces[piece] is not None
        self.curr_piece = self.pieces[piece]
        assert self.curr_piece is not None

        self.row = self.curr_piece.get_row()
        self.column = self.curr_piece.get_column()

        for i in range(self.curr_piece.total_this_fill):
            self.grid[rows_set[i]][columns_set[i]] = 0

        self.avail_in_grid += self.curr_piece.total_this_fill

    def can_put(self, rows_set: list[int], columns_set: list[int]) -> bool:
        assert self.curr_piece is not None
        try:
            set_so_far = 0
            j = self.curr_piece.get_first_square_pos()
            column_jj = self.column - j
            for i in range(self.curr_piece.total_this_fill):
                row_i = self.row + self.curr_piece.get_row_set(set_so_far)
                column_j = column_jj + self.curr_piece.get_column_set(set_so_far)
                if (
                    row_i < 0
                    or row_i >= self.ROWS
                    or column_j < 0
                    or column_j >= self.COLUMNS
                ):
                    return False
                if self.grid[row_i][column_j] != 0:
                    return False
                else:
                    rows_set[set_so_far] = row_i
                    columns_set[set_so_far] = column_j
                    set_so_far += 1
        except (IndexError, TypeError):
            return False

        self.tried_pieces += 1
        return True

    def solve(self, abort_event: Optional[object] = None) -> int:
        start = time.time()

        if abort_event is not None and hasattr(abort_event, "is_set"):
            # Simulate AbortSignal: if event is set, treat as aborted
            if abort_event.is_set():  # type: ignore[union-attr]
                self._aborted = True

        if self.total_fill_in_grid != self.total_fill_in_pieces:
            print(
                f"Invalid config, grid {self.total_fill_in_grid} "
                f"pieces {self.total_fill_in_pieces}"
            )
        else:
            print(f"Starting rows {self.ROWS} cols {self.COLUMNS}")
            self.put()

        elapsed_time = int(time.time() - start)
        print(f"tried {self.tried_pieces:,} pieces")
        if elapsed_time > 0:
            print(f"at {self.tried_pieces // elapsed_time:,} pieces per sec")
        print(
            f"number of solutions {self.get_solution_count()}"
        )

        return self.total_solutions

    def get_solution_count(self) -> int:
        """Return solutions including equivalent board symmetries."""
        symmetries = 8 if self.ROWS == self.COLUMNS else 4
        return self.total_solutions * symmetries
