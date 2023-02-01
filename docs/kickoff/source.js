import { Puzzle2d } from "./puzzle2d.js";

let solveButton = document.getElementById('solveButton');

let puzzle = new Puzzle2d(10,6,6); // 3,3,3; 12,12,5; 12,10,6; 10,6,6

solveButton.addEventListener('click', () => {

    document.getElementById('solveButton').disabled = true;
    // let input = document.getElementById('input').value;
    let output = document.getElementById('output');

    output.innerHTML = puzzle.solve();
});
