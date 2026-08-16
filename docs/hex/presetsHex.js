const STACKED_TRIANGLE =
    "#3,3,2 # stacked triangular prism\n" +
    "xxx\n" +
    "xx-\n" +
    "x--\n" +
    "\n" +
    "xxx\n" +
    "xx-\n" +
    "x--\n" +
    "\n" +
    "#end of grid\n" +
    "#PieceA x6\n" +
    "x\n" +
    "2\n" +
    "x\n" +
    "\n" +
    "#piece-End";

const HEX_FLOWER =
    "#coordinates # seven-cell hex flower\n" +
    "0,0,0\n" +
    "1,0,0\n" +
    "1,-1,0\n" +
    "0,-1,0\n" +
    "-1,0,0\n" +
    "-1,1,0\n" +
    "0,1,0\n" +
    "#end of grid\n" +
    "#PieceA\n" +
    "0,0,0\n" +
    "1,0,0\n" +
    "1,-1,0\n" +
    "#PieceB x2\n" +
    "0,0,0\n" +
    "1,0,0\n" +
    "#piece-End";

export { HEX_FLOWER, STACKED_TRIANGLE };
