package core

import (
	"context"
	"testing"
)

func TestPuzzle3DFindsSolution(t *testing.T) {
	puzzle, err := NewPuzzle3D(3, 4, 5)
	if err != nil {
		t.Fatalf("failed to create puzzle: %v", err)
	}

	if result := puzzle.Solve(context.Background(), false); result != 1 {
		t.Fatalf("expected 1 solution, got %d", result)
	}

	cubesPerPiece := make([]int, Puzzle3DPieces+1)
	for _, cell := range puzzle.Grid {
		if cell < 1 || cell > Puzzle3DPieces {
			t.Fatalf("grid contains invalid piece %d", cell)
		}
		cubesPerPiece[cell]++
	}
	for piece := 1; piece <= Puzzle3DPieces; piece++ {
		if cubesPerPiece[piece] != cellsPerPiece {
			t.Errorf("piece %d occupies %d cells, expected %d", piece, cubesPerPiece[piece], cellsPerPiece)
		}
	}
}

func TestPuzzle3DRejectsNonPositiveDimensions(t *testing.T) {
	if _, err := NewPuzzle3D(0, 4, 5); err == nil {
		t.Fatal("expected invalid dimensions to return an error")
	}
}
