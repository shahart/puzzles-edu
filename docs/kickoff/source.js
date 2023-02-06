import { Puzzle2d } from "./puzzle2d.js";

let solveButton = document.getElementById('solveButton');

// todo save lib of puzzles and not just the last one, we got 4k for that
let restoreButton = document.getElementById('restoreButton');

let puzzle;

restoreButton.addEventListener('click', () => {
    let input = getCookie("input");
    if (input !== "") {
        document.getElementById('input').value = input;
    }
});

solveButton.addEventListener('click', () => {

    document.getElementById('solveButton').disabled = true;
    document.getElementById('restoreButton').disabled = true;

    let output = document.getElementById('output');

    let input = document.getElementById('input').value;

    if (input !== "") {
        setCookie("input", input, 3);
    }

    let header = input.split('\n')[0];

    if (header.indexOf('#6,6') >= 0) {
        // todo move into buildFromFile
        puzzle = new Puzzle2d(10, 6, 6);
        output.innerHTML = '10';
    }
    else if (header.indexOf('12,') >= 0) {
        puzzle = new Puzzle2d(12, parseInt(header.split(',')[1]), parseInt(header.split(',')[2]));
        output.innerHTML = 'Poly';
    }
    // As I fixed the Poly bug, still have a way to measure the cpu speed.
    // see "benchmarks" at puzzle2d.js
    // Chrome: most users don't use other browser
    //      Core i7, 8th Gen, 8665U (Q2 2019) - 3.2 sec - 53 sec with DevTools
    //      Mediatek MT6769T Helio G80 (Q1 2020) - 2.3
    //      Qualcomm SnapDragon 808 (Q2 2014) - 4.2
    //      Mediatek MT6572M (2013) -
    else if (header.toLowerCase().trim() === 'speed') {
        let dt = new Date();
        var amount = 1500000000;
        console.log('for-loop till ' + amount + ' started at ' + dt + "." + dt.getMilliseconds()/1000);
        for (var i = amount; i > 0; i--) {}
        dt = new Date();
        console.log('ended at ' + dt.getHours() + ":" + dt + "." + dt.getMilliseconds()/1000);
        output.innerHTML = header;
    }
    else {
        puzzle = new Puzzle2d(0,0,0, input);
        output.innerHTML = 'Custom';
    }

    if (puzzle) {
        output.innerHTML = puzzle.solve(); // todo async
    }
});

function setCookie(cname, cvalue, exdays) {
    const d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    let expires = "expires="+ d.toUTCString();
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
