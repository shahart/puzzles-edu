import { Puzzle2d } from "./puzzle2d.js";

let solveButton = document.getElementById('solveButton');

// a "library" of puzzles, we got 4k for that
let restoreButtonLatest = document.getElementById('restoreButtonLatest');
let restoreButton2 = document.getElementById('restoreButton2');
let restoreButton3 = document.getElementById('restoreButton3');
let restoreButton4 = document.getElementById('restoreButton4');

let puzzle;

restoreButtonLatest.addEventListener('click', () => {
    let input = getCookie("presetLatest");
    if (input !== "") {
        document.getElementById('input').value = input;
    }
});

restoreButton2.addEventListener('click', () => {
    let input = getCookie("preset2");
    if (input !== "") {
        document.getElementById('input').value = input;
    }
});

restoreButton3.addEventListener('click', () => {
    let input = getCookie("preset3");
    if (input !== "") {
        document.getElementById('input').value = input;
    }
});

restoreButton4.addEventListener('click', () => {
    let input = getCookie("preset4");
    if (input !== "") {
        document.getElementById('input').value = input;
    }
});

solveButton.addEventListener('click', () => {

    let output = document.getElementById('output');

    let input = document.getElementById('input').value;

    let header = input.split('\n')[0];

    // As I fixed the Poly bug, still have a way to measure the cpu speed.
    // see "benchmarks" at puzzle2d.js
    // Chrome: most users don't use other browser
    //      Core i7, 8th Gen, 8665U (Q2 2019) - 3.2 sec - 53 sec with DevTools
    //      Mediatek MT6769T Helio G80 (Q1 2020) - 2.3
    //      Qualcomm SnapDragon 808 (Q2 2014) - 4.2
    if (header.toLowerCase().trim() === 'speed') {
        let dt = new Date();
        var amount = 1500000000;
        console.log('For-loop till ' + amount);
        console.log('Started at ' + dt.toISOString());
        for (var i = amount; i > 0; i--) {}
        dt = new Date();
        console.log('Ended at ' + dt.toISOString());
        output.innerHTML = header;
    }
    else {
        if (input !== "") {
            setCookie("preset4", getCookie("preset3"), 365);
            setCookie("preset3", getCookie("preset2"), 365);
            setCookie("preset2", getCookie("presetLatest"), 365);
            setCookie("presetLatest", input, 365);
        }

        if (header.indexOf('#6,6') >= 0) {
            // todo move into buildFromFile
            puzzle = new Puzzle2d(10, 6, 6);
            output.innerHTML = '10';
        } else if (header.indexOf('12,') >= 0) {
            puzzle = new Puzzle2d(12, parseInt(header.split(',')[1]), parseInt(header.split(',')[2]));
            output.innerHTML = 'Poly';
        }
        else {
            puzzle = new Puzzle2d(0, 0, 0, input);
            output.innerHTML = 'Custom';
        }
        output.innerHTML = puzzle.solve(); // todo async
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
