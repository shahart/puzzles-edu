package core

import "testing"

func resetTotalFill() {
	TotalFill = 0
}

func TestHasSolutionFor12x5(t *testing.T) {
	resetTotalFill()
	p := NewPuzzle2D()
	p.Set(12, 5)
	result := p.Solve(nil)
	if result != 1 {
		t.Errorf("expected 1 solution for 12x5, got %d", result)
	}
}

func TestHasSolutionFor10x6(t *testing.T) {
	resetTotalFill()
	p := NewPuzzle2D()
	p.Set(10, 6)
	result := p.Solve(nil)
	if result != 1 {
		t.Errorf("expected 1 solution for 10x6, got %d", result)
	}
}

func TestNoSolutionFor5x13(t *testing.T) {
	resetTotalFill()
	p := NewPuzzle2D()
	p.Set(5, 13)
	result := p.Solve(nil)
	if result != 0 {
		t.Errorf("expected 0 solutions for 5x13, got %d", result)
	}
}
