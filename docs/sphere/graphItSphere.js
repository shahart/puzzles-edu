function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function colorFor(index) {
    const hue = (index * 137.508 + 8) % 360;
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
        .map((component) => component.toFixed(3)).join(" ");
}

function latticePosition([q, r, z]) {
    return [
        10 * (q + r / 2 + z / 2),
        10 * (Math.sqrt(3) * r / 2 + z / (2 * Math.sqrt(3))),
        10 * Math.sqrt(2 / 3) * z
    ];
}

class GraphItSphere {
    getSphereX3d(result, title) {
        if (!result?.solved) {
            throw new Error("A solved puzzle is required for rendering");
        }
        const names = [...new Set(result.assignments)];
        const shapeIndex = new Map(names.map((name, index) => [name, index]));
        const positions = result.usableCells.map(latticePosition);
        const minimum = [0, 1, 2].map((axis) => Math.min(...positions.map((point) => point[axis])));
        const maximum = [0, 1, 2].map((axis) => Math.max(...positions.map((point) => point[axis])));
        const center = minimum.map((value, axis) => -((value + maximum[axis]) / 2));
        const shapes = names.map((name, index) =>
            `<Shape DEF="SPHERE_PIECE_${index}">` +
            `<Appearance><Material diffuseColor="${colorFor(index)}"/></Appearance>` +
            '<Sphere radius="5"/></Shape>'
        ).join("\n");
        const cells = positions.map((position, index) =>
            `<Transform DEF="SPHERE_CELL_${index}" translation="${position.map((value) => value.toFixed(3)).join(" ")}">` +
            `<Shape USE="SPHERE_PIECE_${shapeIndex.get(result.assignments[index])}"/></Transform>`
        ).join("\n");

        return `<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sphere ${escapeHtml(title)}</title>
    <script src="https://x3dom.org/release/x3dom-full.js"></script>
    <link rel="stylesheet" href="https://x3dom.org/release/x3dom.css">
    <style>
        html, body { height: 100%; margin: 0; background: #0c111a; color: #e8edf7; font-family: sans-serif; }
        body { display: grid; grid-template-rows: auto 1fr; }
        header { display: flex; align-items: center; gap: 14px; padding: 12px 16px; }
        button { border: 0; border-radius: 999px; padding: 9px 14px; font-weight: 700; cursor: pointer; }
        x3d { width: 100%; height: 100%; }
    </style>
</head>
<body>
    <header><button type="button" onclick="window.close()">Go Back</button><span>${escapeHtml(title)}</span></header>
    <X3D profile="Interchange">
        <Scene>
            <Viewpoint position="0 0 120"></Viewpoint>
            <Transform rotation="1 0 0 -0.72" scale="0.82 0.82 0.82">
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

    graphItSphere(result, title) {
        const newWindow = open("", `graphItSphere${title}`, "");
        if (!newWindow) {
            throw new Error("The sphere view was blocked by the browser");
        }
        newWindow.document.write(this.getSphereX3d(result, title));
        newWindow.document.close();
    }
}

export { GraphItSphere, latticePosition };
