import { GraphIt3d } from "./graphIt3d.js";

let solveButton = document.getElementById('solveButton');
let graphItButton = document.getElementById('graphItButton');
let dropdownButton = document.getElementById('PuzzleSelect');
let activeWorker = null;
let currentResult = null;

graphItButton.disabled = true;

function cancelActiveSolve() {
    if (activeWorker !== null) {
        activeWorker.terminate();
        activeWorker = null;
        solveButton.textContent = 'Solve';
    }
}

graphItButton.addEventListener('click', () => {
    if (currentResult?.solved) {
        let title = dropdownButton.value;
        try {
            new GraphIt3d().graphIt3d(currentResult, title);
        } catch (error) {
            document.getElementById('output').value = `Render error: ${error.message}`;
        }
    }
});

let restoreButton1 = document.getElementById('restoreButton1');
let restoreButton2 = document.getElementById('restoreButton2');
let restoreButton3 = document.getElementById('restoreButton3');

let saveButton1 = document.getElementById('saveButton1');
let saveButton2 = document.getElementById('saveButton2');
let saveButton3 = document.getElementById('saveButton3');

function restoreButton(id) {
    cancelActiveSolve();
    currentResult = null;
    let input = loadPuzzle("preset" + id);
    console.log('cls');
    graphItButton.disabled = true;
    document.getElementById('output').value = '';
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
    cancelActiveSolve();
    currentResult = null;

    if (dropdownButton.value === 'Custom') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').value = '';
        document.getElementById('input').value =
            "#3,4,5\n" +
            "#end of grid. Pieces:Poly";
    }

    if (dropdownButton.value === 'Bedlam') {
        console.log('cls');
        graphItButton.disabled = true;
        document.getElementById('output').value = '';
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
        document.getElementById('output').value = '';
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
        document.getElementById('output').value = '';
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
        document.getElementById('output').value = '';
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
        document.getElementById('output').value = '';
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
        document.getElementById('output').value = '';
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
        document.getElementById('output').value = '';
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
    if (activeWorker !== null) {
        cancelActiveSolve();
        output.value = 'Solve cancelled';
        return;
    }

    const input = document.getElementById('input').value;
    if (input.trim() === "") {
        output.value = 'Invalid input. Follow an example';
        return;
    }

    savePuzzle("lastRun3d", input);
    currentResult = null;
    graphItButton.disabled = true;
    output.value = 'Solving…';
    solveButton.textContent = 'Cancel';
    const worker = new Worker(new URL('./worker3d.js', import.meta.url), { type: 'module' });
    activeWorker = worker;

    const finish = () => {
        worker.terminate();
        if (activeWorker === worker) {
            activeWorker = null;
            solveButton.textContent = 'Solve';
        }
    };

    worker.addEventListener('message', (event) => {
        if (activeWorker !== worker) {
            return;
        }
        if (event.data.type === 'error') {
            output.value = `Invalid input: ${event.data.error}`;
            finish();
            return;
        }
        currentResult = event.data.result;
        output.value = event.data.output;
        graphItButton.disabled = !currentResult.solved;
        console.log(
            `Ended in ${currentResult.elapsedMs} msec. Tried placements ${currentResult.triedRows}`
        );
        finish();
    }, { once: true });

    worker.addEventListener('error', (event) => {
        if (activeWorker !== worker) {
            return;
        }
        output.value = `Solver error: ${event.message}`;
        finish();
    }, { once: true });

    worker.postMessage({ text: input });
});

function savePuzzle(cname, cvalue) {
    localStorage.setItem(cname, cvalue);
}

function loadPuzzle(cname) {
    let res = localStorage.getItem(cname);
    return res ? res : "";
}
