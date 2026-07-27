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

	dimensions, err := parseDimensions(problemID, 2)
	if err != nil {
		writeJSONError(w, err.Error(), http.StatusBadRequest)
		return
	}

	puzzle2d := core.NewPuzzle2D()
	puzzle2d.Set(dimensions[0], dimensions[1])

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()

	result := puzzle2d.SolveContext(ctx)
	fmt.Printf("Done id %s with result %d\n", problemID, result)

	writeJSON(w, result)
}

func Solve3DHandler(w http.ResponseWriter, r *http.Request) {
	problemID := strings.TrimPrefix(r.URL.Path, "/solve3d/")
	fmt.Printf("Starting 3D id %s\n", problemID)

	dimensions, err := parseDimensions(problemID, 3)
	if err != nil {
		writeJSONError(w, err.Error(), http.StatusBadRequest)
		return
	}

	puzzle3d, err := core.NewPuzzle3D(dimensions[0], dimensions[1], dimensions[2])
	if err != nil {
		writeJSONError(w, err.Error(), http.StatusBadRequest)
		return
	}

	countSolutions := false
	if countValue := r.URL.Query().Get("count"); countValue != "" {
		countSolutions, err = strconv.ParseBool(countValue)
		if err != nil {
			writeJSONError(w, "invalid count parameter", http.StatusBadRequest)
			return
		}
	}

	ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
	defer cancel()
	result := puzzle3d.Solve(ctx, countSolutions)
	fmt.Printf("Done 3D id %s with result %d\n", problemID, result)

	writeJSON(w, result)
}

func parseDimensions(problemID string, expected int) ([]int, error) {
	parts := strings.Split(problemID, "_")
	if len(parts) != expected {
		return nil, fmt.Errorf("invalid problem id")
	}

	dimensions := make([]int, expected)
	for i, part := range parts {
		value, err := strconv.Atoi(part)
		if err != nil || value <= 0 {
			return nil, fmt.Errorf("dimensions must be positive integers")
		}
		dimensions[i] = value
	}
	return dimensions, nil
}

func writeJSON(w http.ResponseWriter, result int) {
	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(result); err != nil {
		fmt.Printf("Failed to encode response: %v\n", err)
	}
}

func writeJSONError(w http.ResponseWriter, message string, status int) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	if err := json.NewEncoder(w).Encode(map[string]string{"error": message}); err != nil {
		fmt.Printf("Failed to encode error response: %v\n", err)
	}
}
