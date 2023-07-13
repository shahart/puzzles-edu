class Speed {

    // As I fixed the Poly bug, still have a way to measure the cpu speed.
    // see "benchmarks" at puzzle2d.js
    // Chrome: most users don't use other browser
    //      Core i7, 8th Gen, 8665U (Q2 2019) - 3.2 sec - 53 sec with DevTools
    //      Core i7, 12th Gen, 1265U (Q1 2022) - 0.48 sec - 1.0 sec with DevTools
    //      Mediatek MT6769T Helio G80 (Q1 2020) - 2.3
    //      Qualcomm SnapDragon 808 (Q2 2014) - 4.2
    // in fact, there's a way, with this input (vs 20,3..)
    // #3,20
    // #end of grid. Pieces:Poly
    measure() {
        let dt = new Date();
        var amount = 1500000000 / 1;
        console.log('For-loop till ' + amount);
        let start = dt.getTime();
        for (var i = amount; i > 0; i--) {
            // do nothing
        }
        dt = new Date();
        console.log('Ended at ' + dt.toISOString());
        let res = 'For-loop till ' + amount +
            '\nTime taken [sec] ' + (dt.getTime() - start) / 1000;
        output.innerHTML = res;
        return res;
    }

}

export { Speed }
