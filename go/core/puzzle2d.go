package core

import (
	"fmt"
	"sync/atomic"
	"time"
)

type Puzzle2D struct {
	PIECES          int
	ROWS            int
	COLUMNS         int
	TotalSolutions  int
	TriedPieces     int
	Row             int
	Column          int
	PiecesIndices   []int
	Solution        []int
	Pieces          []*Piece
	Names           string
	Grid            [][]int
	CurrPiece       *Piece
	TotalFillInGrid int
	AvailInGrid     int
	aborted         *atomic.Bool
}

func NewPuzzle2D() *Puzzle2D {
	return &Puzzle2D{
		PIECES:  12,
		aborted: &atomic.Bool{},
	}
}

func (p *Puzzle2D) Set(rows, columns int) {
	TotalFill = 0
	p.PiecesIndices = []int{}
	p.Solution = make([]int, p.PIECES)
	p.TotalSolutions = 0
	p.TriedPieces = 0
	p.aborted.Store(false)

	p.ROWS = rows
	p.COLUMNS = columns

	p.Grid = make([][]int, p.ROWS)
	for i := range p.Grid {
		p.Grid[i] = make([]int, p.COLUMNS)
	}

	allPieces := [][][]int{
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

	rotations := []int{4, 4, 2, 1, 4, 4, 4, 4, 2, 4, 4, 2}
	symmetric := []int{2, 1, 1, 1, 2, 2, 1, 2, 2, 1, 1, 1}
	p.Names = "LUFXYNWPZVTI"

	if p.ROWS == p.COLUMNS && p.ROWS == 8 {
		rotations[2] = 1
	}

	if p.ROWS == 0 && p.COLUMNS == 0 {
		panic("not supported yet")
	}

	p.Pieces = make([]*Piece, p.PIECES)
	for i := 0; i < p.PIECES; i++ {
		p.PiecesIndices = append(p.PiecesIndices, i)
		p.Pieces[i] = NewPiece(i, allPieces[i], rotations[i], symmetric[i], string(p.Names[i]))
	}

	if p.ROWS == p.COLUMNS && p.ROWS == 8 {
		p.Grid[0][0] = -1
		p.Grid[7][0] = -1
		p.Grid[0][7] = -1
		p.Grid[7][7] = -1
	}
	for p.Grid[0][p.Column] == -1 {
		p.Column++
	}

	p.TotalFillInGrid = p.ROWS * p.COLUMNS
	for _, row := range p.Grid {
		for _, val := range row {
			if val == -1 {
				p.TotalFillInGrid--
			}
		}
	}

	p.AvailInGrid = p.TotalFillInGrid
	fmt.Printf("Found %d rows, %d cols, with total of cells %d\n", p.ROWS, p.COLUMNS, p.AvailInGrid)
	p.ShowGrid()
}

func (p *Puzzle2D) ShowGrid() {
	fmt.Printf("[msec] showGrid. Tried Pieces %d leftPieces %d\n", p.TriedPieces, len(p.PiecesIndices))
	for i := 0; i < p.ROWS; i++ {
		line := ""
		for j := 0; j < p.COLUMNS; j++ {
			if p.Grid[i][j] == -1 {
				line += "*  "
			} else if p.Grid[i][j] == 0 {
				line += "-  "
			} else {
				line += fmt.Sprintf("%c ", p.Names[p.Grid[i][j]-1])
			}
		}
		fmt.Println(line)
	}
}

func (p *Puzzle2D) showPieces() {
	line := ""
	for i := 0; i < p.PIECES-len(p.PiecesIndices); i++ {
		line += fmt.Sprintf("%c ", p.Names[p.Solution[i]])
	}
	fmt.Println(line)
}

func (p *Puzzle2D) Put() {
	leftPieces := len(p.PiecesIndices)

	if leftPieces == 0 {
		p.TotalSolutions++
		fmt.Printf("totalSolutions %d\n", p.TotalSolutions)
		if p.TotalSolutions == 1 {
			fmt.Println("Found a solution")
			p.ShowGrid()
			p.showPieces()
		}
	}

	if p.TotalSolutions >= 1 {
		return
	}

	rowsSet := make([]int, 5)
	columnsSet := make([]int, 5)

	for i := 0; i < leftPieces; i++ {
		pieceIdx := p.PiecesIndices[i]
		p.CurrPiece = p.Pieces[pieceIdx]
		for r := p.CurrPiece.GetAvailRotations(); r > 0; r-- {
			if p.CanPut(rowsSet, columnsSet) {
				p.PiecesIndices = append(p.PiecesIndices[:i], p.PiecesIndices[i+1:]...)
				p.Solution[p.PIECES-leftPieces] = pieceIdx
				p.PutCurrPiece(rowsSet, columnsSet)

				if p.TriedPieces%50000 == 0 {
					p.ShowGrid()
					p.showPieces()

					if p.aborted.Load() {
						fmt.Printf("Id %d_%d. Signaled timed out! totalSolutions %d\n", p.ROWS, p.COLUMNS, p.TotalSolutions)
						return
					}
				}

				p.Put()

				p.RemoveLast(pieceIdx, rowsSet, columnsSet)
				// insert back at position i
				newIndices := make([]int, 0, len(p.PiecesIndices)+1)
				newIndices = append(newIndices, p.PiecesIndices[:i]...)
				newIndices = append(newIndices, pieceIdx)
				newIndices = append(newIndices, p.PiecesIndices[i:]...)
				p.PiecesIndices = newIndices
			}
			p.CurrPiece.Rotate()
		}
	}
}

func (p *Puzzle2D) PutCurrPiece(rowsSet, columnsSet []int) {
	p.CurrPiece.SetPosition(p.Row, p.Column)

	currIndex := p.CurrPiece.Index + 1
	for i := 0; i < p.CurrPiece.TotalThisFill; i++ {
		p.Grid[rowsSet[i]][columnsSet[i]] = currIndex
	}

	p.GoForward()
	p.AvailInGrid -= p.CurrPiece.TotalThisFill
}

func (p *Puzzle2D) GoForward() {
	for p.Row < p.ROWS {
		for p.Column < p.COLUMNS {
			if p.Grid[p.Row][p.Column] == 0 {
				return
			}
			p.Column++
		}
		p.Column = 0
		p.Row++
	}
}

func (p *Puzzle2D) RemoveLast(piece int, rowsSet, columnsSet []int) {
	p.CurrPiece = p.Pieces[piece]

	p.Row = p.CurrPiece.GetRow()
	p.Column = p.CurrPiece.GetColumn()

	for i := 0; i < p.CurrPiece.TotalThisFill; i++ {
		p.Grid[rowsSet[i]][columnsSet[i]] = 0
	}

	p.AvailInGrid += p.CurrPiece.TotalThisFill
}

func (p *Puzzle2D) CanPut(rowsSet, columnsSet []int) bool {
	if p.CurrPiece == nil {
		return false
	}

	setSoFar := 0
	j := p.CurrPiece.GetFirstSquarePos()
	columnJj := p.Column - j

	for i := 0; i < p.CurrPiece.TotalThisFill; i++ {
		rowI := p.Row + p.CurrPiece.GetRowSet(setSoFar)
		columnJ := columnJj + p.CurrPiece.GetColumnSet(setSoFar)

		if rowI < 0 || rowI >= p.ROWS || columnJ < 0 || columnJ >= p.COLUMNS {
			return false
		}
		if p.Grid[rowI][columnJ] != 0 {
			return false
		}
		rowsSet[setSoFar] = rowI
		columnsSet[setSoFar] = columnJ
		setSoFar++
	}

	p.TriedPieces++
	return true
}

func (p *Puzzle2D) Solve(abortChan <-chan struct{}) int {
	start := time.Now()

	if abortChan != nil {
		select {
		case <-abortChan:
			p.aborted.Store(true)
		default:
		}
	}

	if p.TotalFillInGrid != TotalFill {
		fmt.Printf("Invalid config, grid %d pieces %d\n", p.TotalFillInGrid, TotalFill)
	} else {
		TotalFill = 0
		fmt.Printf("Starting rows %d cols %d\n", p.ROWS, p.COLUMNS)
		p.Put()
	}

	elapsedTime := int(time.Since(start).Seconds())
	fmt.Printf("tried %d pieces\n", p.TriedPieces)
	if elapsedTime > 0 {
		fmt.Printf("at %d pieces per sec\n", p.TriedPieces/elapsedTime)
	}
	multiplier := 4
	if p.ROWS == p.COLUMNS {
		multiplier = 8
	}
	fmt.Printf("number of solutions %d\n", p.TotalSolutions*multiplier)

	return p.TotalSolutions
}
