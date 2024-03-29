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
document.getElementById('input').value = lastRun !== '' ? lastRun : "#3,4,5\n#end of grid. Pieces:Poly";

dropdownButton.addEventListener('change', () => {

    if (dropdownButton.value === 'Custom') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#3,4,5\n" +
            "#end of grid. Pieces:Poly";
    }

    if (dropdownButton.value === 'Bedlam') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#3,3,3 # Bedlam\n" +
            "#end of grid\n" +
            "#PieceL\n" +
            "xx\n" +
            "x\n" +
            "\n" +
            "#pieceT\n" +
            "xxx\n" +
            " x\n" +
            "\n" +
            "#pieceM\n" +
            "xxx\n" +
            "x\n" +
            "\n" +
            "#pieceS\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceE\n" +
            "xx\n" +
            "x\n" +
            "2\n" + // 2nd floor
            " x\n" +
            "\n" +
            "#pieceF\n" +
            "xx\n" +
            "x\n" +
            "2\n" +
            "x\n" +
            "\n" +
            "#pieceG\n" +
            "xx\n" +
            "x\n" +
            "2\n" +
            "x\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'Conway') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#5,5,5 # Conway\n" +
            "#end of grid\n" +
            "#PieceA x13\n" +
            "xxxx\n" +
            "xxxx\n" +
            "\n" +
            "#pieceN\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceO x3\n" +
            "xxx\n" +
            "\n" +
            "#pieceR\n" +
            "xx\n" +
            "xx\n" +
            "2\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'Soma') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#3,3,3 # Soma\n" +
            "#end of grid\n" +
            "#PieceL\n" +
            "xxx\n" +
            "x\n" +
            "\n" +
            "#pieceP\n" +
            "xx\n" +
            "x\n" +
            "2\n" +
            "x\n" +
            "\n" +
            "#pieceV\n" +
            "xx\n" +
            "x\n" +
            "\n" +
            "#pieceZ\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceT\n" +
            "xxx\n" +
            " x\n" +
            "\n" +
            "#pieceA\n" +
            "xx\n" +
            " x\n" +
            "2\n" +
            "x\n" +
            "\n" +
            "#pieceB\n" +
            "xx\n" +
            " x\n" +
            "2\n" +
            " x\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'HappyCubeG') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#5,5,5 # Green (Easy)\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "x---x\n" +
            "x---x\n" +
            "x---x\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "x---x\n" +
            "x---x\n" +
            "x---x\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "x---x\n" +
            "x---x\n" +
            "x---x\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "\n" +
            "#end of grid # https://happycube.com\n" +
            "#pieceA\n" +
            "xx x\n" +
            " xxxx\n" +
            "xxxx\n" +
            " xxxx\n" +
            "xx xx\n" +
            "\n" +
            "#pieceB\n" +
            "  x\n" +
            " xxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            " x xx\n" +
            "\n" +
            "#pieceC\n" +
            "  x\n" +
            " xxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            "xx xx\n" +
            "\n" +
            "#pieceD\n" +
            "  x\n" +
            "xxxxx\n" +
            " xxx\n" +
            "xxxxx\n" +
            " x x\n" +
            "\n" +
            "#pieceE\n" +
            " x x\n" +
            " xxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            " x x\n" +
            "\n" +
            "#pieceF\n" +
            "xx xx\n" +
            "xxxx\n" +
            " xxxx\n" +
            "xxxx\n" +
            "  x\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'HappyCubeR') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#5,5,5 # Red (Medium)\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "x---x\n" +
            "x---x\n" +
            "x---x\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "x---x\n" +
            "x---x\n" +
            "x---x\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "x---x\n" +
            "x---x\n" +
            "x---x\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "\n" +
            "#end of grid\n" +
            "#pieceA\n" +
            "  x\n" +
            " xxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            " x x\n" +
            "\n" +
            "#pieceB\n" +
            "  x\n" +
            "xxxx\n" +
            " xxx\n" +
            "xxxxx\n" +
            " x x\n" +
            "\n" +
            "#pieceC\n" +
            " x xx\n" +
            "xxxx\n" +
            "xxxx\n" +
            " xxxx\n" +
            "xx xx\n" +
            "\n" +
            "#pieceD\n" +
            "  x\n" +
            "xxxx\n" +
            " xxx\n" +
            "xxxxx\n" +
            "x x x\n" +
            "\n" +
            "#pieceE\n" +
            "  xx\n" +
            " xxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            "xx xx\n" +
            "\n" +
            "#pieceF\n" +
            " x x\n" +
            "xxxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            " x xx\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'HappyCubeO') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#5,5,5 # Orange (Hard)\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx" +
            "\n" +
            "x---x\n" +
            "x---x\n" +
            "x---x\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "x---x\n" +
            "x---x\n" +
            "x---x\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "x---x\n" +
            "x---x\n" +
            "x---x\n" +
            "xxxxx\n" +
            "\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "xxxxx\n" +
            "\n" +
            "#end of grid\n" +
            "#pieceA\n" +
            "  x\n" +
            " xxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            "xx x\n" +
            "\n" +
            "#pieceB\n" +
            " x x\n" +
            " xxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            " x x\n" +
            "\n" +
            "#pieceC\n" +
            "xx x\n" +
            " xxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            " x xx\n" +
            "\n" +
            "#pieceD\n" +
            "xx x\n" +
            "xxxx\n" +
            " xxxx\n" +
            "xxxx\n" +
            "x x\n" +
            "\n" +
            "#pieceE\n" +
            "x x\n" +
            "xxxxx\n" +
            " xxx\n" +
            "xxxxx\n" +
            " x x\n" +
            "\n" +
            "#pieceF\n" +
            " x x\n" +
            " xxx\n" +
            "xxxxx\n" +
            " xxx\n" +
            "xx xx\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'Graatsma') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#3,3,3 # Slothouber–Graatsma\n" +
            "#end of grid\n" +
            "#PieceA x6\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceG x3\n" +
            "x\n" +
            "\n" +
            "#piece-End";
    }

});

solveButton.addEventListener('click', () => {

    let output = document.getElementById('output');
    console.log('Started');

    let input = document.getElementById('input').value;

    let puzzle3d;

    if (input !== "") {
        savePuzzle("lastRun3d", input);
        puzzle3d = new Puzzle3d(0, 0, 0, 0, input);
        output.innerHTML = puzzle3d.solve();
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
