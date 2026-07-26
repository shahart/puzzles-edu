function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function colorFor(index) {
    const hue = (index * 137.508) % 360;
    const saturation = 0.72;
    const lightness = index % 2 === 0 ? 0.55 : 0.68;
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

class GraphIt3d {
    get3dX3d(result, title) {
        if (!result?.solved) {
            throw new Error("A solved puzzle is required for rendering");
        }
        const names = [...new Set(result.assignments)];
        const shapeIndex = new Map(names.map((name, index) => [name, index]));
        const cells = result.usableCells.map((cell, index) => ({
            cell,
            name: result.assignments[index]
        }));

        let shapes = "";
        names.forEach((name, index) => {
            shapes +=
                `<Shape DEF="PIECE_${index}">` +
                `<Appearance><Material diffuseColor="${colorFor(index)}"/></Appearance>` +
                '<Box size="7 7 7"/></Shape>\n';
        });

        let points = "";
        cells.forEach(({ cell: [row, column, floor], name }, index) => {
            const x = floor * 10;
            const y = row * 10;
            const z = (result.columns - column - 1) * 10;
            points +=
                `<Transform DEF="POINT_${index}" translation="${x} ${y} ${z}">` +
                `<Shape USE="PIECE_${shapeIndex.get(name)}"/></Transform>\n`;
        });

        return `<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>3D ${escapeHtml(title)}</title>
    <script src="https://x3dom.org/release/x3dom-full.js"></script>
    <link rel="stylesheet" href="https://x3dom.org/release/x3dom.css">
</head>
<body>
    <button type="button" onclick="window.close()">Go Back</button>
    <X3D profile="Interchange" style="width:100%;height:90vh">
        <Scene>
            <Transform rotation="0 1 0 -1" scale="0.05 0.05 0.05">
                ${shapes}
                ${points}
            </Transform>
        </Scene>
    </X3D>
</body>
</html>`;
    }

    graphIt3d(result, title) {
        const newWindow = open("", `graphIt3d${title}`, "");
        if (!newWindow) {
            throw new Error("The 3D view was blocked by the browser");
        }
        newWindow.document.write(this.get3dX3d(result, title));
        newWindow.document.close();
    }
}

export { GraphIt3d };
