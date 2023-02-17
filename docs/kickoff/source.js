import { Puzzle2d } from "./puzzle2d.js";

let solveButton = document.getElementById('solveButton');

let restoreButton1 = document.getElementById('restoreButton1');
let restoreButton2 = document.getElementById('restoreButton2');
let restoreButton3 = document.getElementById('restoreButton3');

let saveButton1 = document.getElementById('saveButton1');
let saveButton2 = document.getElementById('saveButton2');
let saveButton3 = document.getElementById('saveButton3');

function restoreButton(id) {
    let input = loadPuzzle("preset" + id);
    console.log('cls');
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

let dropdownButton = document.getElementById('PuzzleSelect');
dropdownButton.addEventListener('change', () => {

    if (dropdownButton.value === '6x6, 6 pieces') {
        console.log('cls');
        document.getElementById('input').value =
            "#6,6\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "xxxxxx\n" +
            "\n" +
            "#end of grid. Pieces10\n" +
            "#PieceA5\n" +
            "x x\n" +
            "xxx\n" +
            "\n" +
            "#pieceB4\n" +
            " x\n" +
            "xxx\n" +
            "\n" +
            "#pieceC4\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceD4\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceE3\n" +
            " x\n" +
            "xx\n" +
            "\n" +
            "#pieceF2 R2\n" +
            "xx\n" +
            "\n" +
            "#pieceG2 R2\n" +
            "xx\n" +
            "\n" +
            "#unique=H\n" +
            "#pieceH4\n" +
            "  x\n" +
            "xxx\n" +
            "\n" +
            "#pieceI4 R1\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceJ4 R2\n" +
            "xxxx\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'Poly 12x5') {
        console.log('cls');
        document.getElementById('input').value =
            "#12,5\n" +
            "#end of grid. Pieces:Poly";
    }

    if (dropdownButton.value === 'Poly 10x6') {
        console.log('cls');
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

    if (dropdownButton.value === 'Poly 8x8') {
        console.log('cls');
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
            "#end of grid. Pieces12 # You can write: Pieces:Poly\n" +
            "#PieceL5\n" +
            "x\n" +
            "x\n" +
            "x\n" +
            "xx\n" +
            "\n" +
            "#pieceU5 S1\n" +
            "xx\n" +
            "x\n" +
            "xx\n" +
            "\n" +
            "#unique=F\n" +
            "#pieceF5 R1 S1\n" +
            " xx\n" +
            "xx\n" +
            " x\n" +
            "\n" +
            "#pieceX5 R1 S1\n" +
            " x\n" +
            "xxx\n" +
            " x\n" +
            "\n" +
            "#pieceY5\n" +
            "xxxx\n" +
            "  x\n" +
            "\n" +
            "#pieceN5\n" +
            " x\n" +
            "xx\n" +
            "x\n" +
            "x\n" +
            "\n" +
            "#pieceW5 S1\n" +
            "  x\n" +
            " xx\n" +
            "xx\n" +
            "\n" +
            "#pieceP5\n" +
            "x\n" +
            "xx\n" +
            "xx\n" +
            "\n" +
            "#pieceZ5 R2\n" +
            "  x\n" +
            "xxx\n" +
            "x\n" +
            "\n" +
            "#pieceV5 S1\n" +
            "  x\n" +
            "  x\n" +
            "xxx\n" +
            "\n" +
            "#pieceT5 S1\n" +
            "  x\n" +
            "xxx\n" +
            "  x\n" +
            "\n" +
            "#pieceI5 R2 S1\n" +
            "xxxxx\n" +
            "\n" +
            "#piece-End";
    }

    if (dropdownButton.value === 'Checkers') {
        console.log('cls');
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
            "#end of grid. Pieces12 # checkers\n" +
            "#PieceA4\n" +
            "ox\n" +
            "x\n" +
            "o\n" +
            "\n" +
            "#pieceU5\n" +
            "xo\n" +
            " x\n" +
            " o\n" +
            " x\n" +
            "\n" +
            "#pieceF5\n" +
            " o\n" +
            "ox\n" +
            "xo\n" +
            " x\n" +
            "\n" +
            "#pieceX5\n" +
            " x\n" +
            "xo\n" +
            "o\n" +
            "x\n" +
            "\n" +
            "#pieceY5\n" +
            "oxox\n" +
            "  x\n" +
            "\n" +
            "#pieceW5\n" +
            "o\n" +
            "xo\n" +
            " x\n" +
            " o\n" +
            "\n" +
            "#pieceP6\n" +
            "o\n" +
            "xo\n" +
            "oxo\n" +
            "\n" +
            "#pieceZ5\n" +
            "  x\n" +
            "oxo\n" +
            "x\n" +
            "\n" +
            "#pieceB5\n" +
            "  x\n" +
            "oxo\n" +
            "x\n" +
            "\n" +
            "#pieceV5\n" +
            "o\n" +
            "xo\n" +
            " x\n" +
            " o\n" +
            "\n" +
            "#pieceT5\n" +
            "ox\n" +
            " o\n" +
            " x\n" +
            " o\n" +
            "\n" +
            "#pieceI5\n" +
            " x\n" +
            "xox\n" +
            "oxo\n" +
            " o\n" +
            "\n" +
            "#piece-End";
    }
});

solveButton.addEventListener('click', () => {

    // todo async, otherwise we got the output only in the end,
    // this allows timeout > 1.5 sec as user sees progress.

    let output = document.getElementById('output');
    console.log('Started'); //  at ' + new Date().toISOString());

    let input = document.getElementById('input').value;

    let header = input.split('\n')[0];
    let puzzle;

    // As I fixed the Poly bug, still have a way to measure the cpu speed.
    // see "benchmarks" at puzzle2d.js
    // Chrome: most users don't use other browser
    //      Core i7, 8th Gen, 8665U (Q2 2019) - 3.2 sec - 53 sec with DevTools
    //      Mediatek MT6769T Helio G80 (Q1 2020) - 2.3
    //      Qualcomm SnapDragon 808 (Q2 2014) - 4.2
    // in fact, there's a way, with input = poly,3,20 (vs poly,20,3)
    if (header.toLowerCase().trim() === 'speed') {
        let dt = new Date();
        var amount = 1500000000;
        console.log('For-loop till ' + amount);
        let start = dt.getTime();
        for (var i = amount; i > 0; i--) {
            // do nothing
        }
        dt = new Date();
        console.log('Ended at ' + dt.toISOString());
        output.innerHTML = 'For-loop till ' + amount +
            '\nTime taken [sec] ' + (dt.getTime() - start) / 1000;
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
            // todo GraphIt.buildXml, see ShowSol
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
