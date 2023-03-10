class GraphIt3d {

    get3d_x3d(solution, title) {

        // prefix
        let res = "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <script type='text/javascript' src='https://x3dom.org/release/x3dom-full.js'></script>\n" +
            "    <link rel='stylesheet' type='text/css' href='https://x3dom.org/release/x3dom.css' />\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <title>x3d " + title + "</title>\n" +
            "    <meta name='viewport' content='width=device-width, height=device-height, initial-scale=1.0'>\n" +
            "</head>\n" +
            "<body>\n" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE X3D SYSTEM \"x3dViewer/Xj3D/DTD/x3d-3.0.dtd\">\n" +
            "<X3D profile=\"Interchange\">\n" +
            "<Scene>\n" +
            "<Transform rotation=\"0 1 0 -1\"  scale=\"0.05 0.05 0.05\">\n";

        let colors = [
            '0 1 1', // cyan
            '1 0 0', // red
            '1 0.686 0.686', // pink
            '1 0.784 0', // orange
            '1 1 0', // yello
            '0 1 0', // green
            '1 0 1', // magenta
            '0 0 1', // blue
            '0.753 0.753 0.753', // light_gray
            '0.502 0.502 0.502', // gray
            '0.251 0.251 0.251', // dark_gray
            '1 1 1', // white
            '0 0 0' // black
        ];
        let pieces = {};
        let idx = 1;
        let lines = solution.split('\n');
        for (let row = 0; row < lines.length; ++row) {
            let line = lines[row];
            // ' 1  '*
            for (let col = 4; col < line.length; ++col) {
                if (line[col] !== '*') {
                    let z = line[col].toUpperCase();
                    if (!pieces[z]) {
                        pieces[z] = idx;
                        if (idx-1 > colors.length) {
                            console.error('For now supports up to ' + colors.length + ' pieces');
                            alert('For now supports up to ' + colors.length + ' pieces');
                            throw new Error('For now supports up to ' + colors.length + ' pieces');
                        }
                        res += "<Shape DEF=\"PIECE_" + idx + "\"><Appearance><Material diffuseColor=\"" + colors[idx-1] + "\"/></Appearance><Box size=\"7 7 7\"/></Shape>\n";
                        ++ idx;
                    }
                }
                col += 2;
            }
        }
        console.debug('found ' + (idx-1) + ' pieces');

/*

 1  M  M  M
 2  M  E  G
 3  S  G  G

 4  E  E  F
 5  T  E  F
 6  S  S  G

 7  T  F  F
 8  T  L  L
 9  T  S  L   */

        let point = 0;
        let linesPerFloor = 0;
        let floor = 0;
        for (let row = 0; row < lines.length; ++row) {
            let line = lines[row];
            if (line === '') {
                ++ floor;
                if (linesPerFloor === 0) {
                    linesPerFloor = row;
                }
            }
            for (let col = 4; col < line.length; ++col) {
                if (line[col] !== '*') {
                    let z = line[col].toUpperCase();
                    idx = pieces[z];
                    let c = col;
                    let r = row - floor*(linesPerFloor+1);
                    let currLine =
                        "<Transform DEF=\"POINT" + point + "_id_" + floor + "0" + (r) +"0" + ((c-1)/3) +
                        "\" translation=\"" + (floor*10) + " " + (r*10-10) + " " + ((line.length-c-3)/3*10) + "\">" +
                        "<Shape USE=\"PIECE_" + idx + "\"/></Transform>\n";
                    ++ point;
                    res += currLine;
                    ++ idx;
                }
                col += 2;
            }
        }

        // postfix
        res +=
            "</Transform>\n" +
            "</Scene></X3D>\n" +
            "</body>\n" +
            "</html>\n";

        return res;
    }

    graphIt3d(solution, title) {
        // console.log(solution)
        var newWin = open('','graphIt3d'+title,'');
        newWin.document.write(this.get3d_x3d(solution, title));
        newWin.document.close();
    }

}

export { GraphIt3d }
