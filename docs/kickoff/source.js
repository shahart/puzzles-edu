import { Puzzle2d } from "./puzzle2d.js";

let solveButton = document.getElementById('solveButton');

// a "library" of puzzles, we got 4k for that
let restoreButton1 = document.getElementById('restoreButton1');
let restoreButton2 = document.getElementById('restoreButton2');
let restoreButton3 = document.getElementById('restoreButton3');
let restoreButton4 = document.getElementById('restoreButton4');

function restoreButton(id) {
    let input = getCookie("preset" + id);
    console.log('cls');
    document.getElementById('output').innerHTML = '';
    // if (input !== "") {
        document.getElementById('input').value = input;
    // }
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

restoreButton4.addEventListener('click', () => {
    restoreButton(4);
});

solveButton.addEventListener('click', () => {

    // todo async, otherwise we got the output only in the end,
    // this allows timeout > 1.5 sec as user will see the progress.

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
    // in fact, there's a way, with input = 12,3,20 (vs 12,20,3)
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
        if (input !== getCookie("preset1")) {
            setCookie("preset4", getCookie("preset3"), 365);
            setCookie("preset3", getCookie("preset2"), 365);
            setCookie("preset2", getCookie("preset1"), 365);
            setCookie("preset1", input, 365);
        }

        if (header.toLowerCase().indexOf('poly,') >= 0) {
            puzzle = new Puzzle2d(12, parseInt(header.split(',')[1]), parseInt(header.split(',')[2]));
        }
        else if (header.trim().startsWith('#')) {
            puzzle = new Puzzle2d(12, 0, 0, input);
        }
        if (puzzle != null) {
            output.innerHTML = puzzle.solve();
        }
        else {
            output.innerHTML = new Date().toISOString() + ' Invalid input';
        }
    }
    else {
        output.innerHTML = new Date().toISOString() + ' Invalid input';
    }
});

function setCookie(cname, cvalue, expireInDays) {
    const d = new Date();
    d.setTime(d.getTime() + (expireInDays * 24*60*60*1000));
    let expires = "expires=" + d.toUTCString();
    var myCookieValue = cvalue.split('\n').join('\\'); // todo en/decodeURIComponent
    document.cookie = cname + "=" + myCookieValue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = document.cookie;
    let ca = decodedCookie.split(';');
    for(let i = 0; i <ca.length; i++) {
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
