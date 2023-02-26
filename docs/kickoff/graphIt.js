class GraphIt {

    get_x3d(solution, title) {

        /*

         1  *  U  U  P  N  N  N  *
         2  L  L  U  P  P  T  N  N
         3  L  U  U  P  P  T  T  T
         4  L  Z  Z  W  W  T  F  F
         5  L  Z  W  W  X  F  F  V
         6  Z  Z  W  X  X  X  F  V
         7  Y  Y  Y  Y  X  V  V  V
         8  *  Y  I  I  I  I  I  *

         */

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
            '1 1 1' // white
        ];
        let pieces = {};
        let checkersMode = false;
        let idx = 1;
        let lines = solution.split('\n');
        for (let row = 0; row < lines.length; ++row) {
            let line = lines[row];
            // ' 1  '*
            for (let col = 4; col < line.length; ++col) {
                if (line[col] !== '*') {
                    let z = line[col].toUpperCase();
                    if (z !== line[col]) {
                        checkersMode = true;
                    }
                    if (!pieces[z]) {
                        pieces[z] = idx;
                        if (idx-1 > colors.length) {
                            console.error('For now supports up to ' + colors.length + ' pieces');
                            alert('For now supports up to ' + colors.length + ' pieces');
                            throw new Error('For now supports up to ' + colors.length + ' pieces');
                        }
                        res += "<Shape DEF=\"PIECE_" + idx + "_r\"><Appearance><Material diffuseColor=\"" + colors[idx-1] + "\"/></Appearance><Box size=\"7 7 7\"/></Shape>\n";
                        if (checkersMode) {
                            res += "<Shape DEF=\"PIECE_" + idx + "_s\"><Appearance><Material diffuseColor=\"" + colors[idx-1] + "\"/></Appearance><Box size=\"5 5 5\"/></Shape>\n";
                        }
                        ++ idx;
                    }
                }
                col += 2;
            }
        }
        console.debug('found ' + (idx-1) + ' pieces');

        let transforms = "";
        let switchSizes = false; // 0 0 0 can't be a piece with s, from some reason
        let point = 0;
        for (let row = 0; row < lines.length; ++row) {
            let line = lines[lines.length - 1 - row];
            for (let col = 4; col < line.length; ++col) {
                if (line[col] !== '*') {
                    let z = line[col].toUpperCase();
                    idx = pieces[z];
                    let c = col;
                    let r = row;
                    let currLine =
                        "<Transform DEF=\"POINT" + point + "_id_" + (r) +"0" + ((c-1)/3) +
                        "\" translation=\"0 " + (r*10-10) + " " + ((line.length-c-3)/3*10) + "\">" +
                        "<Shape USE=\"PIECE_" + (z === line[c] ? (idx+"_r") : (idx+"_s")) + "\"/></Transform>\n";
                    ++ point;
                    transforms += currLine;
                    ++ idx;
                    if ((r*10-10) === 0 && ((line.length-c-3)/3*10) === 0 && z !== line[c]) {
                        switchSizes = true;
                    }
                }
                col += 2;
            }
        }

        if (switchSizes) {
            transforms = transforms.replaceAll("_r\"/></Transform>", "_t\"/></Transform>");
            transforms = transforms.replaceAll("_s\"/></Transform>", "_r\"/></Transform>");
            transforms = transforms.replaceAll("_t\"/></Transform>", "_s\"/></Transform>");
        }

        // postfix
        res += transforms +
            "</Transform>\n" +
            "</Scene></X3D>\n" +
            "</body>\n" +
            "</html>\n";

        return res;
    }

    graphIt(solution, title) {
        // console.log(solution)
        var newWin = open('','graphIt'+title,'');
        newWin.document.write(this.get_x3d(solution, title));
        newWin.document.close();
    }

}

export { GraphIt }
