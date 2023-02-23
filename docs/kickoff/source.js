import { Puzzle2d } from "./puzzle2d.js";
import { Speed } from "./speed.js";
import { GraphIt } from "./graphIt.js";

let solveButton = document.getElementById('solveButton');
let graphItButton = document.getElementById('graphItButton');
let dropdownButton = document.getElementById('PuzzleSelect');

function handleOrientation(event) {
    const a = event.alpha > 180 ? event.alpha - 360 : event.alpha;
    const b = event.beta - 90;
    const g = event.gamma > 180 ? 360 - event.gamma : -event.gamma;
    let rot = "rotateX(" + b + "deg) rotateY(" + g + "deg) rotateZ(" + a + "deg)";
    document.getElementById("cube").style.transform = rot;
}

graphItButton.addEventListener('click', () => {
    let solution = document.getElementById('output').innerHTML;
    if (solution !== '' && solution.startsWith(" 1  ")) {
        let title = dropdownButton.value; // todo identify a change in text if not custom, and switch option/title to custom
        new GraphIt().graphIt(solution, title);
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

if (!(typeof (StorageEvent) !== undefined)) {
    console.warn('No storageApi, using cookies');
}

let lastRun = loadPuzzle("lastRun");
document.getElementById('input').value = lastRun !== '' ? lastRun : 'Poly,15,4';

dropdownButton.addEventListener('change', () => {

    if (dropdownButton.value === '6x6, 6 pieces') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#6,6\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "\n" +
            "#end of grid\n" +
            "#PieceA\n" +
            "x x\n" +
            "xxx\n" +
            "\n" +
            "#pieceB\n" +
            " x\n" +
            "xxx\n" +
            "\n" +
            "#pieceC\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceD\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceE\n" +
            " x\n" +
            "xx\n" +
            "\n" +
            "#pieceF\n" +
            "xx\n" +
            "\n" +
            "#pieceG\n" +
            "xx\n" +
            "\n" +
            "#unique=H\n" +
            "#pieceH\n" +
            "  x\n" +
            "xxx\n" +
            "\n" +
            "#pieceI\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceJ\n" +
            "xxxx\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'Poly 12x5') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#12,5\n" +
            "#end of grid. Pieces:Poly";
    }

    if (dropdownButton.value === 'Poly 10x6') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#10,6\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "\n" +
            "#end of grid. Pieces:Poly";
    }

    if (dropdownButton.value === 'Eureka') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#8,8\n" +
            "#end of grid\n" +
            "#PieceA\n" +
            "xxx\n" +
            "xxx\n" +
            " xx\n" +
            "\n" +
            "#pieceB\n" +
            "xxx\n" +
            "x\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceC\n" +
            "  xx\n" +
            "xxxx\n" +
            "  xx\n" +
            "\n" +
            "#pieceD\n" +
            "x\n" +
            "xxx\n" +
            "xxxx\n" +
            "\n" +
            "#pieceE\n" +
            "x\n" +
            "xxx\n" +
            "xxx\n" +
            " x\n" +
            "\n" +
            "#pieceF\n" +
            "xx\n" +
            "xx\n" +
            "xxxx\n" +
            "\n" +
            "#pieceG\n" +
            "  x\n" +
            "xxx\n" +
            "xxx\n" +
            "  x\n" +
            "\n" +
            "#pieceH\n" +
            "x x\n" +
            "xxx\n" +
            "xxx\n" +
            "\n" +
            "#piece-End\n";
    }

    if (dropdownButton.value === 'Custom') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#rows,columns\n" +
            "xxxx\n" +
            "...\n" +
            "#end of grid # comment\n" +
            "#PieceA\n" +
            "xxx\n" +
            "xxx\n" +
            " xx\n" +
            "...\n" +
            "#piece-End\n";
    }


    if (dropdownButton.value === 'Tetris 10x4') {
        console.log('cls');
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#4,10\n" +
            "#end of grid # Tetris\n" +
            "#PieceA\n" +
            "xxxx\n" +
            "\n" +
            "#pieceB\n" +
            "xxxx\n" +
            "\n" +
            "#pieceC\n" +
            " x\n" +
            "xxx\n" +
            "\n" +
            "#pieceD\n" +
            " x\n" +
            "xxx\n" +
            "\n" +
            "#pieceE\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceF\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceG\n" +
            "xxx\n" +
            "x\n" +
            "\n" +
            "#pieceH\n" +
            "xxx\n" +
            "x\n" +
            "\n" +
            "#pieceI\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceJ\n" +
            "xx\n" +
            " xx\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'Tetris 5x8') {
        console.log('cls');
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#5,8\n" +
            "#end of grid # Tetris\n" +
            "#PieceA\n" +
            "xxxx\n" +
            "\n" +
            "#pieceB\n" +
            "xxxx\n" +
            "\n" +
            "#pieceC\n" +
            " x\n" +
            "xxx\n" +
            "\n" +
            "#pieceD\n" +
            " x\n" +
            "xxx\n" +
            "\n" +
            "#pieceE\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceF\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceG\n" +
            "xxx\n" +
            "x\n" +
            "\n" +
            "#pieceH\n" +
            "xxx\n" +
            "x\n" +
            "\n" +
            "#pieceI\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceJ\n" +
            "xx\n" +
            " xx\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'Checkers 6x6') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value = ""; // todo
    }

    if (dropdownButton.value === 'Poly 8x8') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#8,8\n" +
            "_xxxxxx_\n" +
            "xxxxxxxx\n" +
            "xxxxxxxx\n" +
            "xxxxxxxx\n" +
            "xxxxxxxx\n" +
            "xxxxxxxx\n" +
            "xxxxxxxx\n" +
            "_xxxxxx_\n" +
            "\n" +
            "#end of grid # You can write: Pieces:Poly\n" +
            "#PieceL\n" +
            "x\n" +
            "x\n" +
            "x\n" +
            "xx\n" +
            "\n" +
            "#pieceU\n" +
            "xx\n" +
            "x\n" +
            "xx\n" +
            "\n" +
            "#unique=F\n" +
            "#pieceF\n" +
            " xx\n" +
            "xx\n" +
            " x\n" +
            "\n" +
            "#pieceX\n" +
            " x\n" +
            "xxx\n" +
            " x\n" +
            "\n" +
            "#pieceY\n" +
            "xxxx\n" +
            "  x\n" +
            "\n" +
            "#pieceN\n" +
            " x\n" +
            "xx\n" +
            "x\n" +
            "x\n" +
            "\n" +
            "#pieceW\n" +
            "  x\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceP\n" +
            "x\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceZ\n" +
            "  x\n" +
            "xxx\n" +
            "x\n" +
            "\n" +
            "#pieceV\n" +
            "  x\n" +
            "  x\n" +
            "xxx\n" +
            "\n" +
            "#pieceT\n" +
            "  x\n" +
            "xxx\n" +
            "  x\n" +
            "\n" +
            "#pieceI\n" +
            "xxxxx\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'Checkers') { // Broken Chess board, by Dudeney
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').innerHTML = '';
        document.getElementById('input').value =
            "#8,8\n" +
            "oxoxoxox\n" +
            "xoxoxoxo\n" +
            "oxoxoxox\n" +
            "xoxoxoxo\n" +
            "oxoxoxox\n" +
            "xoxoxoxo\n" +
            "oxoxoxox\n" +
            "xoxoxoxo\n" +
            "\n" +
            "#end of grid # checkers\n" +
            "#PieceA\n" +
            "ox\n" +
            "x\n" +
            "o\n" +
            "\n" +
            "#pieceU\n" +
            "xo\n" +
            " x\n" +
            " o\n" +
            " x\n" +
            "\n" +
            "#pieceF\n" +
            " o\n" +
            "ox\n" +
            "xo\n" +
            " x\n" +
            "\n" +
            "#pieceX\n" +
            " x\n" +
            "xo\n" +
            "o\n" +
            "x\n" +
            "\n" +
            "#pieceY\n" +
            "oxox\n" +
            "  x\n" +
            "\n" +
            "#pieceW\n" +
            "o\n" +
            "xo\n" +
            " x\n" +
            " o\n" +
            "\n" +
            "#pieceP\n" +
            "o\n" +
            "xo\n" +
            "oxo\n" +
            "\n" +
            "#pieceZ\n" +
            "  x\n" +
            "oxo\n" +
            "x\n" +
            "\n" +
            "#pieceB\n" +
            "  x\n" +
            "oxo\n" +
            "x\n" +
            "\n" +
            "#pieceV\n" +
            "o\n" +
            "xo\n" +
            " x\n" +
            " o\n" +
            "\n" +
            "#pieceT\n" +
            "ox\n" +
            " o\n" +
            " x\n" +
            " o\n" +
            "\n" +
            "#pieceI\n" +
            " x\n" +
            "xox\n" +
            "oxo\n" +
            " o\n" +
            "\n" +
            "#piece-End";
    }
});

window.addEventListener('deviceorientation', handleOrientation);

solveButton.addEventListener('click', () => {

    // todo async, otherwise we got the output only in the end,
    // this allows timeout > 1.5 sec as user sees progress.

    let output = document.getElementById('output');
    console.log('Started'); //  at ' + new Date().toISOString());

    let input = document.getElementById('input').value;

    let header = input.split('\n')[0];
    let puzzle;

    if (header.toLowerCase().trim() === 'speed') {
        new Speed().measure();
    }
    else if (input !== "" && input.indexOf(',') !== -1) {
        savePuzzle("lastRun", input);
        if (header.toLowerCase().indexOf('poly,') >= 0) {
            puzzle = new Puzzle2d(12, parseInt(header.split(',')[1]), parseInt(header.split(',')[2]));
        }
        else if (header.trim().startsWith('#')) {
            puzzle = new Puzzle2d(0, 0, 0, input);
        }
        if (puzzle != null) {
            output.innerHTML = puzzle.solve();
            graphItButton.disabled =
                output.innerHTML.indexOf("no solution") !== -1 ||
                output.innerHTML.indexOf("Invalid input") !== -1 ||
               !output.innerHTML.startsWith(" 1  ");
        }
        else {
            console.warn('Invalid input');
            output.innerHTML = new Date().toISOString() + ' Invalid input';
        }
    }
    else {
        console.warn('Invalid input');
        output.innerHTML = new Date().toISOString() + ' Invalid input';
    }
});

function savePuzzle(cname, cvalue) {
    if (typeof (Storage) !== "undefined") {
        // ~5M max
        localStorage.setItem(cname, cvalue);
    } else {
        // 4K
        const d = new Date();
        let expireInDays = 365;
        d.setTime(d.getTime() + (expireInDays * 24 * 60 * 60 * 1000));
        let expires = "expires=" + d.toUTCString();
        var myCookieValue = cvalue.split('\n').join('\\'); // todo en/decodeURIComponent
        document.cookie = cname + "=" + myCookieValue + ";" + expires + ";path=/";
    }
}

function loadPuzzle(cname) {
    if (typeof (Storage) !== "undefined") {
        let res = localStorage.getItem(cname);
        return res ? res : "";
    } else {
        let name = cname + "=";
        let decodedCookie = document.cookie;
        let ca = decodedCookie.split(';');
        for (let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) === ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) === 0) {
                return c.substring(name.length, c.length).split('\\').join('\n');
            }
        }
        return "";
    }
}
