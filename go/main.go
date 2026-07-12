package main

import (
	"fmt"
	"net/http"
	"os"

	"github.com/puzzles-edu/go/routes"
)

func main() {
	http.HandleFunc("/solve/", routes.SolveHandler)

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	fmt.Printf("Puzzle solver server running on http://localhost:%s\n", port)
	if err := http.ListenAndServe(":"+port, nil); err != nil {
		fmt.Fprintf(os.Stderr, "Server error: %v\n", err)
		os.Exit(1)
	}
}
