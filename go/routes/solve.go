package routes

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"strings"
	"time"

	"github.com/puzzles-edu/go/core"
)

func SolveHandler(w http.ResponseWriter, r *http.Request) {
	problemID := strings.TrimPrefix(r.URL.Path, "/solve/")
	fmt.Printf("Starting id %s\n", problemID)

	rowsCols := strings.Split(problemID, "_")
	if len(rowsCols) != 2 {
		http.Error(w, `{"error":"invalid problem id"}`, http.StatusBadRequest)
		return
	}

	rows, err := strconv.Atoi(rowsCols[0])
	if err != nil {
		http.Error(w, `{"error":"invalid rows"}`, http.StatusBadRequest)
		return
	}
	cols, err := strconv.Atoi(rowsCols[1])
	if err != nil {
		http.Error(w, `{"error":"invalid columns"}`, http.StatusBadRequest)
		return
	}

	core.TotalFill = 0
	puzzle2d := core.NewPuzzle2D()
	puzzle2d.Set(rows, cols)

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()

	abortChan := make(chan struct{})
	go func() {
		<-ctx.Done()
		close(abortChan)
	}()

	result := puzzle2d.Solve(abortChan)
	fmt.Printf("Done id %s with result %d\n", problemID, result)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(result)
}
