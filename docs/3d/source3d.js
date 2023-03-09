import { Puzzle3d } from "./puzzle3d.js";
import { GraphIt3d } from "./graphIt3d.js";

let solveButton = document.getElementById('solveButton');
let graphItButton = document.getElementById('graphItButton');
let dropdownButton = document.getElementById('PuzzleSelect');

graphItButton.addEventListener('click', () => {
    let solution = document.getElementById('output').innerHTML;
    if (solution !== '' && solution.startsWith(" 1  ")) {
        let title = dropdownButton.value;
        new GraphIt3d().graphIt3d(solution, title);
    }
});

let restoreButton1 = document.getElementById('restoreButton1');
let restoreButton2 = document.getElementById('restoreButton2');
let restoreButton3 = document.getElementById('restoreButton3');

let saveButton1 = document.getElementById('saveButton1');
let saveButton2 = document.getElementById('saveButton2');
let saveButton3 = document.getElementById('saveButton3');

function restoreButton(id) {
    let input = loadPuzzle("preset" + id);
    console.log('cls');
    graphItButton.disabled = true;
    document.getElementById('output').innerHTML = '';
    if (input !== "") {
        document.getElementById('input').value = input;
    }
}

restoreButton1.addEventListener('click', () => {
    restoreButton(1);
});

restoreButton2.addEventListener('click', () => {
    restoreButton(2);
});

restoreButton3.addEventListener('click', () => {
    restoreButton(3);
});

saveButton1.addEventListener('click', () => {
    let input = document.getElementById('input').value;
    if (input !== "") {
        savePuzzle("preset" + 1, input);
    }
});

saveButton2.addEventListener('click', () => {
    let input = document.getElementById('input').value;
    if (input !== "") {
        savePuzzle("preset" + 2, input);
    }
});

saveButton3.addEventListener('click', () => {
    let input = document.getElementById('input').value;
    if (input !== "") {
        savePuzzle("preset" + 3, input);
    }
});

let lastRun = loadPuzzle("lastRun3d");
document.getElementById('input').value = lastRun !== '' ? lastRun : "Poly,3,4,5";

dropdownButton.addEventListener('change', () => {

    if (dropdownButton.value === 'Custom') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "Poly,3,4,5";
    }

});

solveButton.addEventListener('click', () => {

    let output = document.getElementById('output');
    console.log('Started');

    let input = document.getElementById('input').value;

    let header = input.split('\n')[0];
    let puzzle;

    if (input !== "") {
        savePuzzle("lastRun3d", input);
        puzzle = new Puzzle3d(12, 3, 4, 5);
        output.innerHTML = puzzle.solve();
        graphItButton.disabled =
            output.innerHTML.indexOf("no solution") !== -1 ||
            output.innerHTML.indexOf("Invalid input") !== -1 ||
            !output.innerHTML.startsWith(" 1  ");
    }
    else {
        console.warn('Invalid input. Follow an example');
        output.innerHTML = ' Invalid input. Follow an example';
    }
});

function savePuzzle(cname, cvalue) {
    localStorage.setItem(cname, cvalue);
}

function loadPuzzle(cname) {
    let res = localStorage.getItem(cname);
    return res ? res : "";
}
