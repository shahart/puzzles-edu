from __future__ import annotations


class Piece:
    total_fill: int = 0

    def __init__(
        self,
        index: int,
        layout: list[list[int]],
        avail_rotations: int,
        symmetric: int,
        name: str,
    ) -> None:
        self.index = index
        self.name = name
        self.curr_rotation = 0
        self.row = -1
        self.column = -1
        self.total_this_fill = 0

        total_orientations = avail_rotations * symmetric
        layouts: list[list[list[int]]] = [None] * total_orientations  # type: ignore[list-item]
        self.first_square_pos: list[int] = [0] * total_orientations
        layouts[0] = layout

        self.rows_set: list[list[int]] = [None] * total_orientations  # type: ignore[list-item]
        self.columns_set: list[list[int]] = [None] * total_orientations  # type: ignore[list-item]

        self.first_square_pos[0] = 0
        while (
            self.first_square_pos[0] < len(layout[0])
            and layout[0][self.first_square_pos[0]] == 0
        ):
            self.first_square_pos[0] += 1

        max_columns = -1
        for row in layout:
            for value in row:
                if value == 1:
                    Piece.total_fill += 1
                    self.total_this_fill += 1
            if len(row) > max_columns:
                max_columns = len(row)

        self._print_part(0, layout)
        if avail_rotations > 1:
            layouts[1] = self._real_rotate(layouts[0], max_columns, len(layout), 1)
            self._print_part(1, layouts[1])
            if avail_rotations > 2:
                layouts[2] = self._real_rotate(layouts[1], len(layout), max_columns, 2)
                self._print_part(2, layouts[2])
                if avail_rotations > 3:
                    layouts[3] = self._real_rotate(layouts[2], max_columns, len(layout), 3)
                    self._print_part(3, layouts[3])

        if symmetric == 2:
            for i in range(avail_rotations):
                layouts[i + avail_rotations] = self._copy_symmetric(layouts[i])
                self._print_part(i + avail_rotations, layouts[i + avail_rotations])
                self.first_square_pos[i + avail_rotations] = 0
                while (
                    self.first_square_pos[i + avail_rotations]
                    < len(layouts[i + avail_rotations][0])
                    and layouts[i + avail_rotations][0][
                        self.first_square_pos[i + avail_rotations]
                    ]
                    == 0
                ):
                    self.first_square_pos[i + avail_rotations] += 1

        for rot in range(total_orientations):
            self.rows_set[rot] = [0] * self.total_this_fill
            self.columns_set[rot] = [0] * self.total_this_fill
            set_so_far = 0
            for i in range(len(layouts[rot])):
                for j in range(len(layouts[rot][i])):
                    if layouts[rot][i][j] == 1:
                        self.rows_set[rot][set_so_far] = i
                        self.columns_set[rot][set_so_far] = j
                        set_so_far += 1

    def get_avail_rotations(self) -> int:
        return len(self.first_square_pos)

    def get_first_square_pos(self) -> int:
        return self.first_square_pos[self.curr_rotation]

    def get_row_set(self, i: int) -> int:
        return self.rows_set[self.curr_rotation][i]

    def get_column_set(self, i: int) -> int:
        return self.columns_set[self.curr_rotation][i]

    def get_row(self) -> int:
        return self.row

    def get_column(self) -> int:
        return self.column

    def rotate(self) -> None:
        self.curr_rotation += 1
        if self.curr_rotation == len(self.first_square_pos):
            self.curr_rotation = 0

    def set_position(self, row: int, column: int) -> None:
        self.row = row
        self.column = column

    def _copy_symmetric(self, original: list[list[int]]) -> list[list[int]]:
        rows = len(original)
        result: list[list[int]] = [None] * rows  # type: ignore[list-item]
        for i in range(rows):
            result[i] = list(original[rows - i - 1])
        return result

    def _real_rotate(
        self,
        original: list[list[int]],
        rows: int,
        columns: int,
        index: int,
    ) -> list[list[int]]:
        result: list[list[int]] = [[0] * columns for _ in range(rows)]

        for i in range(columns):
            for j in range(rows):
                try:
                    result[rows - j - 1][i] = original[i][j]
                except IndexError:
                    pass

        self.first_square_pos[index] = 0
        while (
            self.first_square_pos[index] < len(result[0])
            and result[0][self.first_square_pos[index]] == 0
        ):
            self.first_square_pos[index] += 1

        return result

    def _print_part(self, l: int, layout: list[list[int]]) -> None:
        print(f"    layout={l}")
        for row in layout:
            print("".join(str(x) for x in row))

    def __str__(self) -> str:
        return f"id {self.name} rotation {90 * self.curr_rotation} used {self.row != -1}"
