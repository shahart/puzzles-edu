package routes

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/puzzles-edu/go/core"
)

func resetTotalFill() {
	core.TotalFill = 0
}

func TestGetSolve5_12Returns200(t *testing.T) {
	resetTotalFill()
	req := httptest.NewRequest(http.MethodGet, "/solve/5_12", nil)
	w := httptest.NewRecorder()

	SolveHandler(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("expected status 200, got %d", w.Code)
	}

	var result int
	if err := json.NewDecoder(w.Body).Decode(&result); err != nil {
		t.Fatalf("failed to decode response: %v", err)
	}
	if result != 1 {
		t.Errorf("expected solution count 1, got %d", result)
	}
}

func TestSequentialValidInvalidValid(t *testing.T) {
	resetTotalFill()
	req := httptest.NewRequest(http.MethodGet, "/solve/5_12", nil)
	w := httptest.NewRecorder()
	SolveHandler(w, req)
	if w.Code != http.StatusOK {
		t.Errorf("expected status 200, got %d", w.Code)
	}
	var result int
	json.NewDecoder(w.Body).Decode(&result)
	if result != 1 {
		t.Errorf("expected 1, got %d", result)
	}

	resetTotalFill()
	req = httptest.NewRequest(http.MethodGet, "/solve/5_13", nil)
	w = httptest.NewRecorder()
	SolveHandler(w, req)
	if w.Code != http.StatusOK {
		t.Errorf("expected status 200, got %d", w.Code)
	}
	json.NewDecoder(w.Body).Decode(&result)
	if result != 0 {
		t.Errorf("expected 0, got %d", result)
	}

	resetTotalFill()
	req = httptest.NewRequest(http.MethodGet, "/solve/5_12", nil)
	w = httptest.NewRecorder()
	SolveHandler(w, req)
	if w.Code != http.StatusOK {
		t.Errorf("expected status 200, got %d", w.Code)
	}
	json.NewDecoder(w.Body).Decode(&result)
	if result != 1 {
		t.Errorf("expected 1, got %d", result)
	}
}
