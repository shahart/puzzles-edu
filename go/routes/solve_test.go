package routes

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"sync"
	"testing"
)

func TestGetSolve5_12Returns200(t *testing.T) {
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

func TestSolveEquivalentRectangleOrientations(t *testing.T) {
	for _, path := range []string{"/solve/20_3", "/solve/3_20"} {
		t.Run(path, func(t *testing.T) {
			req := httptest.NewRequest(http.MethodGet, path, nil)
			w := httptest.NewRecorder()

			SolveHandler(w, req)

			if w.Code != http.StatusOK {
				t.Fatalf("expected status 200, got %d", w.Code)
			}
			var result int
			if err := json.NewDecoder(w.Body).Decode(&result); err != nil {
				t.Fatalf("failed to decode response: %v", err)
			}
			if result != 1 {
				t.Errorf("expected solution count 1, got %d", result)
			}
		})
	}
}

func TestSequentialValidInvalidValid(t *testing.T) {
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

func TestGetSolve3DReturns200(t *testing.T) {
	req := httptest.NewRequest(http.MethodGet, "/solve3d/3_4_5", nil)
	w := httptest.NewRecorder()

	Solve3DHandler(w, req)

	if w.Code != http.StatusOK {
		t.Fatalf("expected status 200, got %d", w.Code)
	}
	var result int
	if err := json.NewDecoder(w.Body).Decode(&result); err != nil {
		t.Fatalf("failed to decode response: %v", err)
	}
	if result != 1 {
		t.Errorf("expected solution count 1, got %d", result)
	}
}

func TestSolveHandlersCanRunConcurrently(t *testing.T) {
	testCases := []struct {
		path     string
		handler  http.HandlerFunc
		expected int
	}{
		{path: "/solve/5_12", handler: SolveHandler, expected: 1},
		{path: "/solve/5_13", handler: SolveHandler, expected: 0},
		{path: "/solve3d/3_4_5", handler: Solve3DHandler, expected: 1},
		{path: "/solve3d/3_2_5", handler: Solve3DHandler, expected: 0},
	}

	start := make(chan struct{})
	var waitGroup sync.WaitGroup
	for _, testCase := range testCases {
		testCase := testCase
		waitGroup.Add(1)
		go func() {
			defer waitGroup.Done()
			<-start

			req := httptest.NewRequest(http.MethodGet, testCase.path, nil)
			w := httptest.NewRecorder()
			testCase.handler(w, req)
			if w.Code != http.StatusOK {
				t.Errorf("%s: expected status 200, got %d", testCase.path, w.Code)
				return
			}

			var result int
			if err := json.NewDecoder(w.Body).Decode(&result); err != nil {
				t.Errorf("%s: failed to decode response: %v", testCase.path, err)
				return
			}
			if result != testCase.expected {
				t.Errorf("%s: expected %d, got %d", testCase.path, testCase.expected, result)
			}
		}()
	}
	close(start)
	waitGroup.Wait()
}
