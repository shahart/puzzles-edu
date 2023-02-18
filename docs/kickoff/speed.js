class Speed {

    // As I fixed the Poly bug, still have a way to measure the cpu speed.
    // see "benchmarks" at puzzle2d.js
    // Chrome: most users don't use other browser
    //      Core i7, 8th Gen, 8665U (Q2 2019) - 3.2 sec - 53 sec with DevTools
    //      Mediatek MT6769T Helio G80 (Q1 2020) - 2.3
    //      Qualcomm SnapDragon 808 (Q2 2014) - 4.2
    // in fact, there's a way, with input = poly,3,20 (vs poly,20,3)

    get_2d_ascii_6_6_MC_SZ_ST_167550() {
        // 2d_ascii_6_6_MC_SZ_ST_167550.html +  scale="0.05 0.05 0.05"
        return "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <script type='text/javascript' src='https://x3dom.org/release/x3dom-full.js'></script>\n" +
            "    <link rel='stylesheet' type='text/css' href='https://x3dom.org/release/x3dom.css' />\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <title>2d_ascii_6_6_MC_SZ_ST_167550</title>\n" +
            "    <meta name='viewport' content='width=device-width, height=device-height, initial-scale=1.0'>\n" +
            "</head>\n" +
            "<body>\n" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE X3D SYSTEM \"x3dViewer/Xj3D/DTD/x3d-3.0.dtd\">\n" +
            "<X3D profile=\"Interchange\">\n" +
            "<!--solution #167550 Parts order A2 H5 I0 D3 B1 J1 G0 E2 C0 F0 \n" +
            "\n" +
            " A A A H I I A D A H I I D D H H B J D G G B B J E E C C B J E C C F F J\n" +
            "\n" +
            " A A A H I I\n" +
            " A D A H I I\n" +
            " D D H H B J\n" +
            " D G G B B J\n" +
            " E E C C B J\n" +
            " E C C F F J\n" +
            "\n" +
            "-->\n" +
            "<Scene>\n" +
            "<Transform rotation=\"0 1 0 -1\"  scale=\"0.05 0.05 0.05\">\n" +
            "<Shape DEF=\"PIECE_A\"><Appearance><Material diffuseColor=\"1 0 0\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--red-->\n" +
            "<Shape DEF=\"PIECE_B\"><Appearance><Material diffuseColor=\"1 0.686 0.686\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--pink-->\n" +
            "<Shape DEF=\"PIECE_C\"><Appearance><Material diffuseColor=\"1 0.784 0\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--orange-->\n" +
            "<Shape DEF=\"PIECE_D\"><Appearance><Material diffuseColor=\"1 1 0\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--yellow-->\n" +
            "<Shape DEF=\"PIECE_E\"><Appearance><Material diffuseColor=\"0 1 0\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--green-->\n" +
            "<Shape DEF=\"PIECE_F\"><Appearance><Material diffuseColor=\"1 0 1\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--magenta-->\n" +
            "<Shape DEF=\"PIECE_G\"><Appearance><Material diffuseColor=\"0 1 1\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--cyan-->\n" +
            "<Shape DEF=\"PIECE_H\"><Appearance><Material diffuseColor=\"0 0 1\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--blue-->\n" +
            "<Shape DEF=\"PIECE_I\"><Appearance><Material diffuseColor=\"0.753 0.753 0.753\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--light_gray-->\n" +
            "<Shape DEF=\"PIECE_J\"><Appearance><Material diffuseColor=\"0.502 0.502 0.502\"/></Appearance><Box size=\"7 7 7\"/></Shape><!--gray-->\n" +
            "<Transform DEF=\"POINT0_id_101\" translation=\"0 0 0\"><Shape USE=\"PIECE_A\"/></Transform>\n" +
            "<Transform DEF=\"POINT1_id_102\" translation=\"0 10 0\"><Shape USE=\"PIECE_A\"/></Transform>\n" +
            "<Transform DEF=\"POINT2_id_103\" translation=\"0 20 0\"><Shape USE=\"PIECE_A\"/></Transform>\n" +
            "<Transform DEF=\"POINT3_id_104\" translation=\"0 30 0\"><Shape USE=\"PIECE_H\"/></Transform>\n" +
            "<Transform DEF=\"POINT4_id_105\" translation=\"0 40 0\"><Shape USE=\"PIECE_I\"/></Transform>\n" +
            "<Transform DEF=\"POINT5_id_106\" translation=\"0 50 0\"><Shape USE=\"PIECE_I\"/></Transform>\n" +
            "<Transform DEF=\"POINT6_id_206\" translation=\"0 50 10\"><Shape USE=\"PIECE_I\"/></Transform>\n" +
            "<Transform DEF=\"POINT7_id_205\" translation=\"0 40 10\"><Shape USE=\"PIECE_I\"/></Transform>\n" +
            "<Transform DEF=\"POINT8_id_204\" translation=\"0 30 10\"><Shape USE=\"PIECE_H\"/></Transform>\n" +
            "<Transform DEF=\"POINT9_id_203\" translation=\"0 20 10\"><Shape USE=\"PIECE_A\"/></Transform>\n" +
            "<Transform DEF=\"POINT10_id_202\" translation=\"0 10 10\"><Shape USE=\"PIECE_D\"/></Transform>\n" +
            "<Transform DEF=\"POINT11_id_201\" translation=\"0 0 10\"><Shape USE=\"PIECE_A\"/></Transform>\n" +
            "<Transform DEF=\"POINT12_id_301\" translation=\"0 0 20\"><Shape USE=\"PIECE_D\"/></Transform>\n" +
            "<Transform DEF=\"POINT13_id_302\" translation=\"0 10 20\"><Shape USE=\"PIECE_D\"/></Transform>\n" +
            "<Transform DEF=\"POINT14_id_303\" translation=\"0 20 20\"><Shape USE=\"PIECE_H\"/></Transform>\n" +
            "<Transform DEF=\"POINT15_id_304\" translation=\"0 30 20\"><Shape USE=\"PIECE_H\"/></Transform>\n" +
            "<Transform DEF=\"POINT16_id_305\" translation=\"0 40 20\"><Shape USE=\"PIECE_B\"/></Transform>\n" +
            "<Transform DEF=\"POINT17_id_306\" translation=\"0 50 20\"><Shape USE=\"PIECE_J\"/></Transform>\n" +
            "<Transform DEF=\"POINT18_id_406\" translation=\"0 50 30\"><Shape USE=\"PIECE_J\"/></Transform>\n" +
            "<Transform DEF=\"POINT19_id_405\" translation=\"0 40 30\"><Shape USE=\"PIECE_B\"/></Transform>\n" +
            "<Transform DEF=\"POINT20_id_404\" translation=\"0 30 30\"><Shape USE=\"PIECE_B\"/></Transform>\n" +
            "<Transform DEF=\"POINT21_id_403\" translation=\"0 20 30\"><Shape USE=\"PIECE_G\"/></Transform>\n" +
            "<Transform DEF=\"POINT22_id_402\" translation=\"0 10 30\"><Shape USE=\"PIECE_G\"/></Transform>\n" +
            "<Transform DEF=\"POINT23_id_401\" translation=\"0 0 30\"><Shape USE=\"PIECE_D\"/></Transform>\n" +
            "<Transform DEF=\"POINT24_id_501\" translation=\"0 0 40\"><Shape USE=\"PIECE_E\"/></Transform>\n" +
            "<Transform DEF=\"POINT25_id_502\" translation=\"0 10 40\"><Shape USE=\"PIECE_E\"/></Transform>\n" +
            "<Transform DEF=\"POINT26_id_503\" translation=\"0 20 40\"><Shape USE=\"PIECE_C\"/></Transform>\n" +
            "<Transform DEF=\"POINT27_id_504\" translation=\"0 30 40\"><Shape USE=\"PIECE_C\"/></Transform>\n" +
            "<Transform DEF=\"POINT28_id_505\" translation=\"0 40 40\"><Shape USE=\"PIECE_B\"/></Transform>\n" +
            "<Transform DEF=\"POINT29_id_506\" translation=\"0 50 40\"><Shape USE=\"PIECE_J\"/></Transform>\n" +
            "<Transform DEF=\"POINT30_id_606\" translation=\"0 50 50\"><Shape USE=\"PIECE_J\"/></Transform>\n" +
            "<Transform DEF=\"POINT31_id_605\" translation=\"0 40 50\"><Shape USE=\"PIECE_F\"/></Transform>\n" +
            "<Transform DEF=\"POINT32_id_604\" translation=\"0 30 50\"><Shape USE=\"PIECE_F\"/></Transform>\n" +
            "<Transform DEF=\"POINT33_id_603\" translation=\"0 20 50\"><Shape USE=\"PIECE_C\"/></Transform>\n" +
            "<Transform DEF=\"POINT34_id_602\" translation=\"0 10 50\"><Shape USE=\"PIECE_C\"/></Transform>\n" +
            "<Transform DEF=\"POINT35_id_601\" translation=\"0 0 50\"><Shape USE=\"PIECE_E\"/></Transform>\n" +
            "</Transform>\n" +
            "</Scene></X3D>\n" +
            "</body>\n" +
            "</html>\n";
    }

    measure() {
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
        // easter egg :)
        var newWin = open('','graphI','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=780,height=200,top="+(screen.height-400)+",left="+(screen.width-840)');
        newWin.document.write(this.get_2d_ascii_6_6_MC_SZ_ST_167550());
        newWin.document.close();
    }

}

export { Speed }
