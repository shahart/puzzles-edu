
class Piece3d {

    row = -1;
    column = -1;
    floor = -1;

    constructor(index, layout, name) {
        let msg = name;
        let atoms = 0;
        for (let i = 0; i < layout.length; ++i) {
            for (let j = 0; j < layout[0].length; ++j) {
                for (let k = 0; k < layout[0][0].length; ++k) {
                    if (layout[i][j][k] === 1) {
                        ++atoms;
                    }
                }
            }
        }
        msg += ", Atoms=" + atoms;
        console.debug(msg);
        let availRotations = 64; // todo if needed because of timeouts, maybe drop dups will help
        this.index = index;
        this.totalThisFill = 0;
        this.name = name;
        this.currRotation = 0;
        this.layouts = new Array(64);
        for (let i = 0; i < this.layouts.length; i++) {
            this.layouts[i] = [];
        }
        this.layouts[0] = layout;
        this.rowsSet = new Array(64).fill(0);
        this.columnsSet = new Array(64).fill(0); //[];
        this.floorsSet = new Array(64).fill(0);
        this.maxColumns = -1;

        for (let i = 0; i < layout.length; i++) {
            for (let j = 0; j < layout[i].length; j++) {
                for (let k = 0; k < layout[i][j].length; k++) {
                    if (layout[i][j][k] !== 0) {
                        window.globalTotalFill++;
                        this.totalThisFill++;
                    }
                }
            }
            if (layout[i].length > this.maxColumns) {
                this.maxColumns = layout[i].length;
            }
        }

        if (this.totalThisFill === 0) {
            console.error("empty piece");
            alert("empty piece");
            throw new Error('empty piece');
        }

        // 2d, 0->1,2,3
        for (let a = 1; a < 4; ++a) {
            this.layouts[a] = this.realRotate2d(this.layouts[a - 1]);
        }

        // 3d Y, 0,1,2,3 -> 4,5,6,7  8,9,10,11  12,13,14,15
        for (let a = 0; a < 4; ++a) {
            for (let b = 1; b < 4; ++b) {
                this.layouts[a+b*4] = this.realRotate2dY(this.layouts[a+(b-1)*4]);
            }
        }

        // 3d Z, 0,1,2,3...15 -> 16,..31  32..47  48..63
        for (let a = 0; a < 16; ++a) {
            for (let b = 1; b < 4; ++b) {
                this.layouts[a+b*16] = this.realRotate2dZ(this.layouts[a+(b-1)*16]);
            }
        }

        for (let rot = 0; rot < 64; rot++) {
            this.rowsSet[rot] = [this.totalThisFill];
            this.columnsSet[rot] = [this.totalThisFill];
            this.floorsSet[rot] = [this.totalThisFill];
            let setSoFar = 0;
            for (let k = 0; k < 1; k++) {
                for (let i = 0; i < this.layouts[rot].length; i++) {
                    for (let j = 0; j < this.layouts[rot][i].length; j++) {
                        if (this.layouts[rot][i][j][k] >= 1) {
                            this.rowsSet[rot][setSoFar] = i;
                            this.columnsSet[rot][setSoFar] = j;
                            this.floorsSet[rot][setSoFar] = k;
                            setSoFar++;
                        }
                    }
                }
            }
        }
    }

    shuffle() {
        let indices = [this.layouts.length];
        for (let i = 0; i < this.layouts.length; ++i) {
            indices[i] = i;
        }
        indices = indices.sort(function () { return Math.random() - 0.5; });
        // just for debugging
        let indices2 = [this.layouts.length];
        for (let i = 0; i < this.layouts.length; ++i) {
            indices2[i] = i;
        }
        for (let i = 0; i < this.layouts.length-1; ++i) {
            this.swap(indices2, i, indices[i]);
            this.swap(this.layouts, i, indices[i]);
            this.swap(this.rowsSet, i, indices[i]);
            this.swap(this.columnsSet, i, indices[i]);
            this.swap(this.floorsSet, i, indices[i]);
        }
    }

    swap(arr, first, second) {
        let h = arr[first];
        arr[first] = arr[second];
        arr[second] = h;
    }

    getRowSet(i) {
        return this.rowsSet[this.currRotation][i];
    }

    getColumnSet(i) {
        return this.columnsSet[this.currRotation][i];
    }

    getFloorSet(i) {
        return this.floorsSet[this.currRotation][i];
    }

    getAvailRotations() {
        return 64;
    }

    getLayout() {
        return this.layouts[this.currRotation];
    }

    realRotate2d(original) {
        let L = original.length;
        let result = new Array(L).fill(0).map(_ => new Array(L).fill(0).map(_ => new Array(L).fill(0)));
        for (let k = 0; k < L; k++) {
            for (let i = 0; i < L; i++) {
                for (let j = 0; j < L; j++) {
                    result[k][L - j - 1][i] = original[k][i][j];
                }
            }
        }
        this.printPart(result);
        return result;
    }

    realRotate2dY(original) {
        let L = original.length;
        let result = new Array(L).fill(0).map(_ => new Array(L).fill(0).map(_ => new Array(L).fill(0)));
        for (let k = 0; k < L; k++) {
            for (let i = 0; i < L; i++) {
                for (let j = 0; j < L; j++) {
                    result[L - k - 1][j][i] = original[k][i][j];
                }
            }
        }
        this.printPart(result);
        return result;
    }

    realRotate2dZ(original) {
        let L = original.length;
        let result = new Array(L).fill(0).map(_ => new Array(L).fill(0).map(_ => new Array(L).fill(0)));
        for (let k = 0; k < L; k++) {
            for (let i = 0; i < L; i++) {
                for (let j = 0; j < L; j++) {
                    result[k][j][L - i - 1] = original[k][i][j];
                }
            }
        }
        this.printPart(result);
        return result;
    }

    rotate() { // vs. clock-wise
        this.currRotation++;
        if (this.currRotation === 64) {
            this.currRotation = 0;
        }
    }

    setPosition(row, column, floor) {
        this.row = row;
        this.column = column;
        this.floor = floor;
    }

    getRow() {
        return this.row;
    }

    getColumn() {
        return this.column;
    }

    getFloor() {
        return this.floor;
    }

    printPart(layout) {
        // console.debug("    layout="); // + l);
        for (let k=0; k<layout.length; ++k) {
            for (let i = 0; i < layout.length; ++i) {
                let line = "    ";
                for (let j = 0; j < layout[i].length; ++j) {
                    line += layout[k][i][j];
                }
                // console.debug(line);
            }
            // console.debug("\n" + k + "\n");
        }
    }

}

export { Piece3d };
