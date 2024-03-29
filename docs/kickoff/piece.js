
class Piece {

    row = -1;
    column = -1;

    constructor(index, layout, availRotations, symmetric, name) {
        if (name !== '_') {
            let msg = name;
            if (availRotations < 4) {
                msg += " Rotations=" + availRotations;
            }
            // 2 means a flip(reflection) is a new shape
            if (symmetric === 1) {
                msg += ", Has symmetry";
            }
            console.debug(msg);
        }
        this.availRotations = availRotations;
        this.index = index;
        this.totalThisFill = 0;
        this.name = name;
        this.currRotation = 0;
        this.layouts = new Array(availRotations * symmetric); // .fill(0).map(_ => new Array().fill(0)); // availRotations*symmetric);
        for (let i = 0; i < this.layouts.length; i++) {
            this.layouts[i] = [];
        }
        this.firstSquarePos = new Array(availRotations * symmetric).fill(0);
        this.layouts[0] = layout;

        this.rowsSet = new Array(availRotations * symmetric).fill(0);
        this.columnsSet = new Array(availRotations * symmetric).fill(0); //[];

        this.firstSquarePos[0] = 0;
        while (this.firstSquarePos[0] < this.layouts[0][0].length && this.layouts[0][0][this.firstSquarePos[0]] === 0) {
            this.firstSquarePos[0]++;
        }

        this.maxColumns = -1;

        for (let i = 0; i < layout.length; i++) {
            for (let j = 0; j < layout[i].length; j++) {
                if (layout[i][j] !== 0) {
                    window.globalTotalFill++;
                    this.totalThisFill++;
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

        this.printPart(0, this.layouts[0]);
        if (availRotations === 3 || availRotations >= 5 || availRotations <= 0) {
            console.error("rotations must be 1/2/4");
            alert("rotations must be 1/2/4");
            throw new Error("rotations must be 1/2/4");
        }
        if (availRotations >= 2) {
            this.layouts[1] = this.realRotate(this.layouts[0], this.maxColumns, layout.length, 1);
            this.printPart(1, this.layouts[1]);
            if (availRotations >= 4) {
                this.layouts[2] = this.realRotate(this.layouts[1], layout.length, this.maxColumns, 2);
                this.printPart(2, this.layouts[2]);
                this.layouts[3] = this.realRotate(this.layouts[2], this.maxColumns, layout.length, 3);
                this.printPart(3, this.layouts[3]);
                this.layouts[0] = this.realRotate(this.layouts[3], layout.length, this.maxColumns, 0);
            }
            else {
                this.layouts[0] = this.realRotate(this.layouts[1], layout.length, this.maxColumns, 0);
            }
        }

        if (symmetric === 2) {
            for (let i = 0; i < availRotations; i++) {
                this.layouts[i + availRotations] = this.copySymmetric(this.layouts[i]);
                this.printPart(i + availRotations, this.layouts[i + availRotations]);

                this.firstSquarePos[i + availRotations] = 0;
                while (this.firstSquarePos[i + availRotations] < this.layouts[i + availRotations][0].length && this.layouts[i + availRotations][0][this.firstSquarePos[i + availRotations]] === 0) {
                    this.firstSquarePos[i + availRotations]++;
                }
            }
        }

        for (let rot = 0; rot < availRotations * symmetric; rot++) {
            this.rowsSet[rot] = [this.totalThisFill];
            this.columnsSet[rot] = [this.totalThisFill];
            let setSoFar = 0;
            for (let i = 0; i < this.layouts[rot].length; i++) {
                for (let j = 0; j < this.layouts[rot][i].length; j++) {
                    if (this.layouts[rot][i][j] >= 1) {
                        this.rowsSet[rot][setSoFar] = i;
                        this.columnsSet[rot][setSoFar] = j;
                        setSoFar++;
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
            this.swap(this.firstSquarePos, i, indices[i]);
            this.swap(this.rowsSet, i, indices[i]);
            this.swap(this.columnsSet, i, indices[i]);
        }
    }

    swap(arr, first, second) {
        let h = arr[first];
        arr[first] = arr[second];
        arr[second] = h;
    }

    getFirstSquarePos() {
        return this.firstSquarePos[this.currRotation];
    }

    getRowSet(i) {
        return this.rowsSet[this.currRotation][i];
    }

    getColumnSet(i) {
        return this.columnsSet[this.currRotation][i];
    }

    getAvailRotations() {
        return this.firstSquarePos.length;
    }

    getLayout() {
        return this.layouts[this.currRotation];
    }

    copySymmetric(original) {
        let rows = original.length;
        let result = new Array(rows).fill(0);

        for (let i = 0; i < rows; i++) {
            result[i] = new Array(original[rows-i-1].length).fill(0);
            for (let j = 0; j < result[i].length; j++) {
                result[i][j] = original[rows - i - 1][j];
            }
        }

        return result;
    }

    realRotate(original, rows, columns, index) {
        let result = new Array(rows).fill(0).map(_ => new Array(columns).fill(0)); //[maxColumns][originalLayout.length];

        for (let i = 0; i < columns; i++) {
            for (let j = 0; j < rows; j++) {
                result[rows - j - 1][i] = 0;
                if (original[i][j] === 1) {
                    result[rows - j - 1][i] = 1;
                }
                else if (original[i][j] === 2) {
                    result[rows - j - 1][i] = 2;
                }
            }
        }

        this.firstSquarePos[index] = 0;
        while (this.firstSquarePos[index]<result[0].length && result[0][this.firstSquarePos[index]] === 0) {
            this.firstSquarePos[index]++;
        }

        return result;
    }

    rotate() { // vs. clock-wise
        this.currRotation++;
        if (this.currRotation === this.firstSquarePos.length) {
            this.currRotation = 0;
        }
    }

    setPosition(row, column) {
        this.row = row;
        this.column = column;
    }

    getRow() {
        return this.row;
    }

    getColumn() {
        return this.column;
    }

    printPart(l, layout) {
        // console.debug("    layout=" + l);
        for (let i=0; i<layout.length; ++i) {
            let line = "    ";
            for (let j=0; j<layout[i].length; ++j) {
                line += layout[i][j];
            }
            // console.debug(line);
        }
    }

    eq(orig,dup) {
        if (dup > this.getAvailRotations()-1) {
            console.info("Irrelevant to call eq, as user set the avail rotations to " + this.getAvailRotations());
            return false;
        }
        if (this.layouts[orig].length !== this.layouts[dup].length) {
            return false;
        }
        for (let i = 0; i < this.layouts[orig].length; i++) {
            if (this.layouts[orig][i].length !== this.layouts[dup][i].length) {
                return false;
            }
            for (let j = 0; j < this.layouts[orig][i].length; j++) {
                if (this.layouts[orig][i][j] !== this.layouts[dup][i][j]) {
                    return false
                }
            }
        }
        return true;
    }

    calcRotations() {
        if (this.eq(0,1)) {
            return 1;
        }
        if (this.eq(0,2)) {
            return 2;
        }
        return 4;
    }

    calcSymmetric() {
        if (this.availRotations === 1) {
            return 1;
        }
        else if (this.availRotations === 2) {
            if ((this.eq(0, 2) || this.eq(0, 3)) &&
                (this.eq(1, 2) || this.eq(1, 3))) {
                return 1;
            }
        }
        else if (this.availRotations === 4) {
            if ((this.eq(0, 4) || this.eq(0, 5) || this.eq(0, 6) || this.eq(0, 7)) &&
                (this.eq(1, 4) || this.eq(1, 5) || this.eq(1, 6) || this.eq(1, 7)) &&
                (this.eq(2, 4) || this.eq(2, 5) || this.eq(2, 6) || this.eq(2, 7)) &&
                (this.eq(3, 4) || this.eq(3, 5) || this.eq(3, 6) || this.eq(3, 7))) {
                return 1;
            }
        }
        return 2;
    }

}

export { Piece };
