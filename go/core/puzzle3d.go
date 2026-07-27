package core

import (
	"context"
	"errors"
	"sort"
	"strconv"
	"strings"
)

const (
	Puzzle3DPieces = 12
	cellsPerPiece  = 5
)

type cube struct {
	row, column, depth int
}

type placement3D struct {
	piece int
	cells []int
}

type Puzzle3D struct {
	rows, columns, depth int
	cellCount            int
	placements           []placement3D

	Grid           []int
	TotalSolutions int
	TriedPieces    int
}

func NewPuzzle3D(rows, columns, depth int) (*Puzzle3D, error) {
	if rows <= 0 || columns <= 0 || depth <= 0 {
		return nil, errors.New("all dimensions must be positive integers")
	}

	puzzle := &Puzzle3D{
		rows:      rows,
		columns:   columns,
		depth:     depth,
		cellCount: rows * columns * depth,
	}
	puzzle.Grid = make([]int, puzzle.cellCount)
	puzzle.buildPlacements()
	return puzzle, nil
}

func (p *Puzzle3D) Solve(ctx context.Context, countSolutions bool) int {
	clear(p.Grid)
	p.TotalSolutions = 0
	p.TriedPieces = 0

	if p.cellCount != Puzzle3DPieces*cellsPerPiece {
		return 0
	}

	rows := make([][]int, len(p.placements))
	for i, placement := range p.placements {
		row := make([]int, 0, cellsPerPiece+1)
		row = append(row, placement.cells...)
		row = append(row, p.cellCount+placement.piece)
		rows[i] = row
	}

	limit := 1
	if countSolutions {
		limit = int(^uint(0) >> 1)
	}
	result := runExactCover(ctx, p.cellCount+Puzzle3DPieces, rows, limit)
	p.TotalSolutions = result.solutions
	p.TriedPieces = result.triedRows

	if !countSolutions {
		for _, rowIndex := range result.selectedRows {
			placement := p.placements[rowIndex]
			for _, cell := range placement.cells {
				p.Grid[cell] = placement.piece + 1
			}
		}
		if p.TotalSolutions > 0 {
			return 1
		}
	}
	return p.TotalSolutions
}

func (p *Puzzle3D) buildPlacements() {
	if p.cellCount != Puzzle3DPieces*cellsPerPiece {
		return
	}

	for piece, layout := range pentominoLayouts {
		for _, orientation := range build3DOrientations(layout) {
			maxRow, maxColumn, maxDepth := 0, 0, 0
			for _, cell := range orientation {
				maxRow = max(maxRow, cell.row)
				maxColumn = max(maxColumn, cell.column)
				maxDepth = max(maxDepth, cell.depth)
			}

			for rowOffset := 0; rowOffset+maxRow < p.rows; rowOffset++ {
				for columnOffset := 0; columnOffset+maxColumn < p.columns; columnOffset++ {
					for depthOffset := 0; depthOffset+maxDepth < p.depth; depthOffset++ {
						cells := make([]int, len(orientation))
						for i, cell := range orientation {
							row := rowOffset + cell.row
							column := columnOffset + cell.column
							depth := depthOffset + cell.depth
							cells[i] = (row*p.columns+column)*p.depth + depth
						}
						p.placements = append(p.placements, placement3D{piece: piece, cells: cells})
					}
				}
			}
		}
	}
}

func build3DOrientations(layout [][]int) [][]cube {
	var cells []cube
	for row, line := range layout {
		for column, value := range line {
			if value == 1 {
				cells = append(cells, cube{row: row, column: column})
			}
		}
	}

	permutations := [][3]int{
		{0, 1, 2}, {0, 2, 1}, {1, 0, 2},
		{1, 2, 0}, {2, 0, 1}, {2, 1, 0},
	}
	signValues := []int{-1, 1}
	seen := make(map[string]bool)
	var orientations [][]cube

	for _, permutation := range permutations {
		permutationSign := permutation3DSign(permutation)
		for _, firstSign := range signValues {
			for _, secondSign := range signValues {
				for _, thirdSign := range signValues {
					signs := [3]int{firstSign, secondSign, thirdSign}
					if permutationSign*firstSign*secondSign*thirdSign != 1 {
						continue
					}

					transformed := make([]cube, len(cells))
					for i, cell := range cells {
						values := [3]int{cell.row, cell.column, cell.depth}
						transformed[i] = cube{
							row:    signs[0] * values[permutation[0]],
							column: signs[1] * values[permutation[1]],
							depth:  signs[2] * values[permutation[2]],
						}
					}
					normalizeAndSort3D(transformed)
					key := cubesKey(transformed)
					if !seen[key] {
						seen[key] = true
						orientations = append(orientations, transformed)
					}
				}
			}
		}
	}
	return orientations
}

func permutation3DSign(permutation [3]int) int {
	inversions := 0
	for first := range permutation {
		for second := first + 1; second < len(permutation); second++ {
			if permutation[first] > permutation[second] {
				inversions++
			}
		}
	}
	if inversions%2 == 0 {
		return 1
	}
	return -1
}

func normalizeAndSort3D(cells []cube) {
	minRow, minColumn, minDepth := cells[0].row, cells[0].column, cells[0].depth
	for _, cell := range cells[1:] {
		minRow = min(minRow, cell.row)
		minColumn = min(minColumn, cell.column)
		minDepth = min(minDepth, cell.depth)
	}
	for i := range cells {
		cells[i].row -= minRow
		cells[i].column -= minColumn
		cells[i].depth -= minDepth
	}
	sort.Slice(cells, func(i, j int) bool {
		if cells[i].row != cells[j].row {
			return cells[i].row < cells[j].row
		}
		if cells[i].column != cells[j].column {
			return cells[i].column < cells[j].column
		}
		return cells[i].depth < cells[j].depth
	})
}

func cubesKey(cells []cube) string {
	var key strings.Builder
	for _, cell := range cells {
		key.WriteString(strconv.Itoa(cell.row))
		key.WriteByte(',')
		key.WriteString(strconv.Itoa(cell.column))
		key.WriteByte(',')
		key.WriteString(strconv.Itoa(cell.depth))
		key.WriteByte(';')
	}
	return key.String()
}

var pentominoLayouts = [][][]int{
	{{1}, {1}, {1}, {1, 1}},
	{{1, 1}, {1}, {1, 1}},
	{{0, 1, 1}, {1, 1}, {0, 1}},
	{{0, 1}, {1, 1, 1}, {0, 1}},
	{{1, 1, 1, 1}, {0, 0, 1}},
	{{0, 1}, {1, 1}, {1}, {1}},
	{{0, 0, 1}, {0, 1, 1}, {1, 1}},
	{{1}, {1, 1}, {1, 1}},
	{{0, 0, 1}, {1, 1, 1}, {1}},
	{{0, 0, 1}, {0, 0, 1}, {1, 1, 1}},
	{{0, 0, 1}, {1, 1, 1}, {0, 0, 1}},
	{{1, 1, 1, 1, 1}},
}
