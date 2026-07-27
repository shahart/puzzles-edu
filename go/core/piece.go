package core

import "fmt"

type Piece struct {
	Index             int
	Name              string
	CurrRotation      int
	Row               int
	Column            int
	TotalThisFill     int
	FirstSquarePos    []int
	RowsSet           [][]int
	ColumnsSet        [][]int
	availRotations    int
	totalOrientations int
}

func NewPiece(index int, layout [][]int, availRotations int, symmetric int, name string) *Piece {
	p := &Piece{
		Index:          index,
		Name:           name,
		CurrRotation:   0,
		Row:            -1,
		Column:         -1,
		TotalThisFill:  0,
		availRotations: availRotations,
	}

	totalOrientations := availRotations * symmetric
	p.totalOrientations = totalOrientations
	layouts := make([][][]int, totalOrientations)
	p.FirstSquarePos = make([]int, totalOrientations)
	p.RowsSet = make([][]int, totalOrientations)
	p.ColumnsSet = make([][]int, totalOrientations)

	layouts[0] = layout

	p.FirstSquarePos[0] = 0
	for p.FirstSquarePos[0] < len(layout[0]) && layout[0][p.FirstSquarePos[0]] == 0 {
		p.FirstSquarePos[0]++
	}

	maxColumns := -1
	for _, row := range layout {
		for _, value := range row {
			if value == 1 {
				p.TotalThisFill++
			}
		}
		if len(row) > maxColumns {
			maxColumns = len(row)
		}
	}

	p.printPart(0, layout)
	if availRotations > 1 {
		layouts[1] = p.realRotate(layouts[0], maxColumns, len(layout), 1)
		p.printPart(1, layouts[1])
		if availRotations > 2 {
			layouts[2] = p.realRotate(layouts[1], len(layout), maxColumns, 2)
			p.printPart(2, layouts[2])
			if availRotations > 3 {
				layouts[3] = p.realRotate(layouts[2], maxColumns, len(layout), 3)
				p.printPart(3, layouts[3])
			}
		}
	}

	if symmetric == 2 {
		for i := 0; i < availRotations; i++ {
			layouts[i+availRotations] = p.copySymmetric(layouts[i])
			p.printPart(i+availRotations, layouts[i+availRotations])
			p.FirstSquarePos[i+availRotations] = 0
			for p.FirstSquarePos[i+availRotations] < len(layouts[i+availRotations][0]) && layouts[i+availRotations][0][p.FirstSquarePos[i+availRotations]] == 0 {
				p.FirstSquarePos[i+availRotations]++
			}
		}
	}

	for rot := 0; rot < totalOrientations; rot++ {
		p.RowsSet[rot] = make([]int, p.TotalThisFill)
		p.ColumnsSet[rot] = make([]int, p.TotalThisFill)
		setSoFar := 0
		for i := 0; i < len(layouts[rot]); i++ {
			for j := 0; j < len(layouts[rot][i]); j++ {
				if layouts[rot][i][j] == 1 {
					p.RowsSet[rot][setSoFar] = i
					p.ColumnsSet[rot][setSoFar] = j
					setSoFar++
				}
			}
		}
	}

	return p
}

func (p *Piece) GetAvailRotations() int {
	return len(p.FirstSquarePos)
}

func (p *Piece) GetFirstSquarePos() int {
	return p.FirstSquarePos[p.CurrRotation]
}

func (p *Piece) GetRowSet(i int) int {
	return p.RowsSet[p.CurrRotation][i]
}

func (p *Piece) GetColumnSet(i int) int {
	return p.ColumnsSet[p.CurrRotation][i]
}

func (p *Piece) GetRow() int {
	return p.Row
}

func (p *Piece) GetColumn() int {
	return p.Column
}

func (p *Piece) Rotate() {
	p.CurrRotation++
	if p.CurrRotation == len(p.FirstSquarePos) {
		p.CurrRotation = 0
	}
}

func (p *Piece) SetPosition(row, column int) {
	p.Row = row
	p.Column = column
}

func (p *Piece) copySymmetric(original [][]int) [][]int {
	rows := len(original)
	result := make([][]int, rows)
	for i := 0; i < rows; i++ {
		result[i] = make([]int, len(original[rows-i-1]))
		copy(result[i], original[rows-i-1])
	}
	return result
}

func (p *Piece) realRotate(original [][]int, rows, columns, index int) [][]int {
	result := make([][]int, rows)
	for i := 0; i < rows; i++ {
		result[i] = make([]int, columns)
	}

	for i := 0; i < columns; i++ {
		for j := 0; j < rows; j++ {
			if i < len(original) && j < len(original[i]) {
				result[rows-j-1][i] = original[i][j]
			}
		}
	}

	p.FirstSquarePos[index] = 0
	for p.FirstSquarePos[index] < len(result[0]) && result[0][p.FirstSquarePos[index]] == 0 {
		p.FirstSquarePos[index]++
	}

	return result
}

func (p *Piece) printPart(l int, layout [][]int) {
	fmt.Printf("    layout=%d\n", l)
	for _, row := range layout {
		line := ""
		for _, val := range row {
			line += fmt.Sprintf("%d", val)
		}
		fmt.Println(line)
	}
}

func (p *Piece) String() string {
	return fmt.Sprintf("id %s rotation %d used %v", p.Name, 90*p.CurrRotation, p.Row != -1)
}
