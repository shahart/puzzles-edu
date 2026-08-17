function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function colorFor(index) {
    const hue = (index * 137.508 + 18) % 360;
    const saturation = 0.72;
    const lightness = index % 2 === 0 ? 0.55 : 0.67;
    const chroma = (1 - Math.abs(2 * lightness - 1)) * saturation;
    const sector = hue / 60;
    const intermediate = chroma * (1 - Math.abs(sector % 2 - 1));
    const [red, green, blue] =
        sector < 1 ? [chroma, intermediate, 0] :
        sector < 2 ? [intermediate, chroma, 0] :
        sector < 3 ? [0, chroma, intermediate] :
        sector < 4 ? [0, intermediate, chroma] :
        sector < 5 ? [intermediate, 0, chroma] :
        [chroma, 0, intermediate];
    const match = lightness - chroma / 2;
    return [red + match, green + match, blue + match]
        .map((component) => component.toFixed(3))
        .join(" ");
}

function hexPrismGeometry() {
    return '<IndexedFaceSet coordIndex="6 7 8 9 10 11 -1 5 4 3 2 1 0 -1 ' +
        '1 7 6 0 -1 2 8 7 1 -1 3 9 8 2 -1 4 10 9 3 -1 5 11 10 4 -1 0 6 11 5 -1">' +
        '<Coordinate point="4.8 0 -3.8, 2.4 4.157 -3.8, -2.4 4.157 -3.8, ' +
        '-4.8 0 -3.8, -2.4 -4.157 -3.8, 2.4 -4.157 -3.8, ' +
        '4.8 0 3.8, 2.4 4.157 3.8, -2.4 4.157 3.8, ' +
        '-4.8 0 3.8, -2.4 -4.157 3.8, 2.4 -4.157 3.8"/>' +
        '</IndexedFaceSet>';
}

class GraphItHex {
    getHexX3d(result, title) {
        if (!result?.solved) {
            throw new Error("A solved puzzle is required for rendering");
        }
        const names = [...new Set(result.assignments)];
        const shapeIndex = new Map(names.map((name, index) => [name, index]));
        const positions = result.usableCells.map(([q, r, z]) => [
            10 * (q + r / 2),
            8.660254 * r,
            10 * z
        ]);
        const minimum = [0, 1, 2].map((axis) => Math.min(...positions.map((point) => point[axis])));
        const maximum = [0, 1, 2].map((axis) => Math.max(...positions.map((point) => point[axis])));
        const center = minimum.map((value, axis) => -((value + maximum[axis]) / 2));

        const shapes = names.map((name, index) =>
            `<Shape DEF="HEX_PIECE_${index}">` +
            `<Appearance><Material diffuseColor="${colorFor(index)}"/></Appearance>` +
            hexPrismGeometry() +
            '</Shape>'
        ).join("\n");
        const cells = positions.map((position, index) =>
            `<Transform DEF="HEX_CELL_${index}" translation="${position.map((value) => value.toFixed(3)).join(" ")}">` +
            `<Shape USE="HEX_PIECE_${shapeIndex.get(result.assignments[index])}"/></Transform>`
        ).join("\n");

        return `<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hex ${escapeHtml(title)}</title>
    <script src="https://x3dom.org/release/x3dom-full.js"></script>
    <link rel="stylesheet" href="https://x3dom.org/release/x3dom.css">
    <style>
        html, body { height: 100%; margin: 0; overflow: hidden; background: #0c111a; color: #e8edf7; font-family: sans-serif; }
        header { display: flex; align-items: center; gap: 14px; height: 60px; padding: 12px 16px; box-sizing: border-box; }
        button { border: 0; border-radius: 999px; padding: 9px 14px; font-weight: 700; cursor: pointer; }
        x3d { display: block; width: 100vw; height: calc(100vh - 60px); }
        @supports (height: 100dvh) { x3d { height: calc(100dvh - 60px); } }
    </style>
</head>
<body>
    <header><button type="button" onclick="window.close()">Go Back</button><span>${escapeHtml(title)}</span></header>
    <X3D profile="Interchange">
        <Scene>
            <NavigationInfo type='"EXAMINE"' transitionType='"TELEPORT"'></NavigationInfo>
            <Viewpoint position="0 0 120"></Viewpoint>
            <Transform rotation="1 0 0 -0.82" scale="0.8 0.8 0.8">
                <Transform translation="${center.map((value) => value.toFixed(3)).join(" ")}">
                    ${shapes}
                    ${cells}
                </Transform>
            </Transform>
        </Scene>
    </X3D>
</body>
</html>`;
    }

    graphItHex(result, title) {
        const newWindow = open("", `graphItHex${title}`, "");
        if (!newWindow) {
            throw new Error("The hex view was blocked by the browser");
        }
        newWindow.document.write(this.getHexX3d(result, title));
        newWindow.document.close();
    }
}

export { GraphItHex };
