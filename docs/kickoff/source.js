import { Puzzle2d } from "./puzzle2d.js";

let solveButton = document.getElementById('solveButton');

let puzzle;

solveButton.addEventListener('click', () => {

    document.getElementById('solveButton').disabled = true;

    let output = document.getElementById('output');

    let input = document.getElementById('input').value;
    let header = input.split('\n');

    if (header[0].indexOf('#6,6') >= 0) {
        // todo move into buildFromFile
        puzzle = new Puzzle2d(10,6,6);
        output.innerHTML = '10';
    } // 3,3,3; 12,12,5; 12,10,6; 10,6,6
    else {
        puzzle = new Puzzle2d(12,6,10);
        output.innerHTML = 'Poly';
    }

    output.innerHTML = puzzle.solve(); // todo async
});
