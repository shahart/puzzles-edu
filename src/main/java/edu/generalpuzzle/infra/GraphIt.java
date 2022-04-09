package edu.generalpuzzle.infra;

import edu.generalpuzzle.examples.tangram.CellPartTang;
import edu.generalpuzzle.infra.engines.EngineStrategy;
//import edu.generalpuzzle.main.X3dViewer;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * Date: 18/07/2008
 */
public final class GraphIt {

    private static GraphIt graphItInstance = null;

    public static Process process;

    public static enum Shape {BOX, SPHERE, HEXA_PYRAMID, PIE, TANG, POLY   }

    private JFrame jf;

    private Shape shape = Shape.BOX;

    private static String args = "none";
    public static void setArgs(String argsParam) { args = argsParam; }
    private static String folderArgs = "";
    public static void setFolderArgs(String argsParam) { folderArgs = argsParam; }

    private  Double dims[]  = new Double[7]; // 3 points + partId + attr + gridId + attribute (only on tangram)
    private final List<ICellPart> breadCrumb;
    private final List<Double[]> points;

    private GraphIt()  {
        breadCrumb = new ArrayList<ICellPart>(60); // resizeable by ArrayList
        points = new ArrayList<Double[]>(60);
    }

    public static GraphIt getInstance() {
        if (graphItInstance == null)
            graphItInstance = new GraphIt();
        return graphItInstance;
    }

    private void pointsAdd(double x, double y, double z, double id, double attr, double gridId, double attribute) {
        points.add(new Double[] {x,y,z,id,attr,gridId, attribute});
    }

    /** DFS computation of the cells' positions in 3D space */
	public  void dfsPlot(ICellPart gridCell) {

            for (int edge=0; edge<gridCell.getCell().length; edge++)
                if (gridCell.getCell(edge) != null)
                    if (! breadCrumb.contains(gridCell.getCell(edge))) {
                        double prev[] = new double[3];
                        prev[IEdge.X] = dims[IEdge.X];
                        prev[IEdge.Y] = dims[IEdge.Y];
                        prev[IEdge.Z] = dims[IEdge.Z];

                        gridCell.getDummyEdge().edgeOffset(edge, dims);

                        breadCrumb.add(gridCell.getCell(edge));

                        if (! gridCell.getCell(edge).isPlaceHolder()) {
                            pointsAdd(dims[0], dims[1], dims[2], gridCell.getCell(edge).getPartId(), new Double(gridCell.getCell(edge).getSpecial()).doubleValue(), gridCell.getCell(edge).getId().getId(),
                                    gridCell instanceof CellPartTang ? ((CellPartTang)gridCell).getSpecial()+1 :
//                                            gridCell instanceof CellPartPoly ?  ((CellPartPoly)gridCell).direction+1 :
                                                    0);
                            checkKeys(gridCell.getCell(edge));
                        }

                        dfsPlot(gridCell.getCell(edge));

                        dims[IEdge.X] = prev[IEdge.X];
                        dims[IEdge.Y] = prev[IEdge.Y];
                        dims[IEdge.Z] = prev[IEdge.Z];
                    }
    }


    /** initiate the dfsPlot */
	public void graphIt(ICellPart cell1st)  {

        if (cell1st.getClass().getName().contains("poly"))
            shape = Shape.POLY;
        else if (cell1st.getClass().getName().contains("cube"))
            shape = Shape.BOX;
        else if (cell1st.getClass().getName().contains("sphere"))
            shape = Shape.SPHERE;
        else if (cell1st.getClass().getName().contains("hex"))
            shape = Shape.HEXA_PYRAMID; 
        else if (cell1st.getClass().getName().contains("pie"))
            shape = Shape.PIE;
        else if (cell1st.getClass().getName().contains("tang"))
            shape = Shape.TANG;

        breadCrumb.clear();
        points.clear();

        dims = new Double[5];
        dims[IEdge.X] = 0.0;
        dims[IEdge.Y] = 0.0;
        dims[IEdge.Z] = 0.0;
        dims[3] = (double)cell1st.getPartId();
        dims[4] = new Double(cell1st.getSpecial()).doubleValue();

        if (! cell1st.isPlaceHolder()) {
            pointsAdd(0, 0, 0, cell1st.getPartId(), dims[4], cell1st.getId().getId(),
                    cell1st instanceof CellPartTang ? ((CellPartTang)cell1st).getSpecial()+1 :
//                            cell1st instanceof CellPartPoly ? ((CellPartPoly)cell1st).direction+1 :
                                    0);

            checkKeys(cell1st);
        }

        breadCrumb.add(cell1st);

        dfsPlot(cell1st);

    }

    /** add symbols for keys and locks */
	private void checkKeys(ICellPart cell1st) {
        if (cell1st.keysExists) // as we are on the grid, keysExists is false
            for (int i=0; i<cell1st.keys.length; i++)
                if (cell1st.keys[i] != 0) { // && cell1st.getCell(i) != null) {

                    double prev[] = new double[3];
                    prev[IEdge.X] = dims[IEdge.X];
                    prev[IEdge.Y] = dims[IEdge.Y];
                    prev[IEdge.Z] = dims[IEdge.Z];

                    int origOffest = IEdge.OFFSET;
                    IEdge.OFFSET *= (cell1st.keys[i] > 0 ? 0.4 : 0.4);///= 3 or 0.1
                    cell1st.getDummyEdge().edgeOffset(i, dims);
                    IEdge.OFFSET = origOffest;

                    if (cell1st.keys[i] < 1)
                        dims[4] = 1.0 * cell1st.keys[i] - 1000;
                    else
                        dims[4] = -1.0 * cell1st.keys[i];

                    pointsAdd(dims[0], dims[1], dims[2], cell1st.partId, dims[4], cell1st.getId().getId(),
                            cell1st instanceof CellPartTang ? ((CellPartTang)cell1st).getSpecial()+1 :
//                                    cell1st instanceof CellPartPoly ? ((CellPartPoly)cell1st).direction+1 :
                                            0);



                    dims[IEdge.X] = prev[IEdge.X];
                    dims[IEdge.Y] = prev[IEdge.Y];
                    dims[IEdge.Z] = prev[IEdge.Z];
                }
    }



//    public void graphIt(List<IPart> parts)  {
//
//        if (parts.get(0).getClass().getName().contains("cube"))
//            shape = Shape.BOX;
//        else if (parts.get(0).getClass().getName().contains("ball"))
//            shape = Shape.SPHERE;
//        else if (parts.get(0).getClass().getName().contains("hexa"))
//            shape = Shape.HEXA_PYRAMID;
//
//        breadCrumb.clear();
//        points.clear();
//
//        double x = 0;
//        for (IPart p: parts) {
//
//            ICellPart cell1st = p.getCells()[0];
//
//            dims = new Double[5];
//            dims[IEdge.X] = x;
//            x += 100;
//            dims[IEdge.Y] = 0.0;
//            dims[IEdge.Z] = 0.0;
//            dims[3] = (double)cell1st.getPartId();
//            dims[4] = cell1st.getSpecial() ? 1.0 : 0;
//
//            if (! cell1st.isPlaceHolder())
//                pointsAdd(x, 0, 0, cell1st.getPartId(), cell1st.getSpecial() ? 1 : 0);
//
//            breadCrumb.add(cell1st);
//
//            dfsPlot(cell1st);
//        }
//
//    }

    /** build the x3d file, represents this sol solution (=order of parts on the lattice)  */
	public void buildXml(String sol) {

        System.out.println("\nGraphIt.x3d generated, from Engine0, for solution #" + EngineStrategy.getLastSolutionNumber());
        // new File("tmp").mkdir();
        String filename = "tmp/" + folderArgs + args + "_" + EngineStrategy.getLastSolutionNumber();

        boolean specialExists = false; 
        for (int j=0; j<points.size(); j++)
            if (points.get(j)[4] == 1.0)
                specialExists = true;

        String colorNames[] = new String[] { "red", "pink", "orange", "yellow","green",  "magenta", "cyan", "blue",
                "light_gray", "gray", "dark_gray", "white" };
        Color colors[] = new Color[] {
                Color.RED, Color.PINK, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.BLUE,
                Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.WHITE};
        Color color;
        int colorIdx = 0;

        List<String> rowsList = new ArrayList<String>();
        for (String row: sol.trim().split(" "))
            rowsList.add(row.substring(0,1));

        // sort, to keep the color of each partId
        Collections.sort(rowsList);

        try {

            // Find the implementation
            DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            // Create the document
//            String x3dNS = "http://www.web3d.org/specifications/x3d-3.0.dtd";
//            DocumentType x3d = impl.createDocumentType("X3D", "ISO//Web3D//DTD X3D 3.0//EN", x3dNS);
            DocumentType x3d = impl.createDocumentType("X3D", "", "x3dViewer/Xj3D/DTD/x3d-3.0.dtd");
            Document doc = impl.createDocument("", "X3D", x3d);

            // Fill the document

            Element root = doc.getDocumentElement();
            root.setAttribute("profile", "Interchange");

            Text space;

            space = doc.createTextNode("\n");
            root.appendChild(space);

            root.appendChild(doc.createComment("solution #" + EngineStrategy.getLastSolutionNumber() + " " + EngineStrategy.solutionPresentation));

            Element scene = doc.createElement("Scene");

            space = doc.createTextNode("\n");
            scene.appendChild(space);

            Element transform = doc.createElement("Transform");
//            if (shape == Shape.SPHERE)
//                transform.setAttribute("rotation", "0 0 0 0");
//            else
                transform.setAttribute("rotation", "0 1 0 -1"); // -0.345
            scene.appendChild(transform);

            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(3);

            // workaround, as in the Swirl x3d viewer, the 1st part in the group,
            // must be the same as in the trasforms

            int j=0;
            while ((char)points.get(j)[3].intValue() == '_')
                j++;

            int firstColor = -1;
            boolean firstPassed = false;
            for (String row: rowsList) {
                if (row.equals("_"))
                    continue;
                if (row.charAt(0) == (char)points.get(j)[3].intValue())
                    firstColor = colorIdx;
                colorIdx ++;
            }

            if (firstColor == -1)
                firstColor++;

            for (colorIdx = firstColor; colorIdx < rowsList.size(); colorIdx++) {

                String row = rowsList.get(colorIdx);

                if (row.equals("_"))
                    continue;

                if (colorIdx == firstColor && firstPassed)
                    continue;

//                Element group = doc.createElement("Group");
//                group.setAttribute("DEF","3CEG_"+row);
                space = doc.createTextNode("\n");
                transform.appendChild(space);


//                if (this.shape == Shape.TANG) { // TODO 2010

//                    for (int i=0; i<8; i++) {

                        Element shape;

//                        shape = doc.createElement("Shape");
//                        shape.setAttribute("DEF","PIECE_"+ row + i);

//                        shape.appendChild(breadCrumb.get(0).graphCell(doc));

//                    }

//                }
//                else {
//
                    shape = doc.createElement("Shape");
                    shape.setAttribute("DEF","PIECE_"+ row);

                    // shape.appendChild(breadCrumb.get(0).graphCell(doc));
//                }

                        // end TODO 2010

                Element appearance = doc.createElement("Appearance");
                shape.appendChild(appearance);

                if (this.shape == Shape.SPHERE) {
                    Element sphere = doc.createElement("Sphere");
                    sphere.setAttribute("radius","4.7");
                    shape.appendChild(sphere);
                }
                else if (this.shape == Shape.HEXA_PYRAMID) {
//                    Element cyl = doc.createElement("Cylinder");
//                    cyl.setAttribute("radius","4.5");
//                    cyl.setAttribute("height","2");
//                    shape.appendChild(cyl);
                    Element hexa = doc.createElement("IndexedFaceSet");
                    hexa.setAttribute("coordIndex",
                            "                6 7 8 9 10 11 -1\n" +
                            "                5 4 3 2 1 0 -1\n" +
                            "                1 7 6 0 -1\n" +
                            "                3 9 8 2 -1\n" +
                            "                1 2 8 7 -1\n" +
                            "                3 4 10 9 -1\n" +
                            "                4 5 11 10 -1\n" +
                            "                0 6 11 5");
                    Element coords = doc.createElement("Coordinate");
                    coords.setAttribute("point",
                            "              5 0 -2, 2.5 4.33 -2, -2.5 4.33 -2, -5 0 -2,  -2.5 -4.33  -2, 2.5 -4.33 -2, \n" +
                            "              5 0 2, 2.5 4.33 2, -2.5 4.33 2, -5 0 2,  -2.5 -4.33  2, 2.5 -4.33 2");
                    hexa.appendChild(coords);
                    shape.appendChild(hexa);
                }
                else if (this.shape == Shape.BOX) {
                    Element box = doc.createElement("Box");
                    box.setAttribute("size","7 7 7");
                    shape.appendChild(box);
                }
                else if (this.shape == Shape.POLY) {
                    Element box = doc.createElement("Box");
                    box.setAttribute("size","7 2 7");
//                    Element box = doc.createElement("Text");
//                    box.setAttribute("string","/"); //"\\");
                    shape.appendChild(box);
                }
                else if (this.shape == Shape.PIE) {
                    Element cyl = doc.createElement("Cylinder");
                    cyl.setAttribute("radius","4.5");
                    cyl.setAttribute("height","2");
                    shape.appendChild(cyl);
                }

                Element material = doc.createElement("Material");

                color = colors[colorIdx% colors.length];
                if (colorIdx >= colors.length)
                    color = color.darker().darker();
                material.setAttribute("diffuseColor",nf.format(color.getRed()/255.0)+" " + nf.format(color.getGreen()/255.0) + " " + nf.format(color.getBlue()/255.0));
                // material.setAttribute("transparency","0.1");

                appearance.appendChild(material);
//                group.appendChild(shape);
                transform.appendChild(shape); // group);

                Comment comment = doc.createComment(colorNames[colorIdx% colors.length]);
                transform.appendChild(comment);

                if (specialExists) { // && this.shape == Shape.HEXA_PYRAMID) {
//                    group = doc.createElement("Group");
//                    group.setAttribute("DEF","PIECEG_"+row + "2");
                    space = doc.createTextNode("\n");
                    transform.appendChild(space);

                    shape = doc.createElement("Shape");
                    shape.setAttribute("DEF","PIECE_"+ row + "2");

//                    Element sphere = doc.createElement("Sphere");
//                    sphere.setAttribute("radius","3");
                    Element sphere = doc.createElement("Box");
                    sphere.setAttribute("size","7 7 2");
                    shape.appendChild(sphere);

                    Element appearance2= doc.createElement("Appearance");
                    shape.appendChild(appearance2);

                    Element material2 = doc.createElement("Material");
                    material2.setAttribute("diffuseColor",nf.format(color.getRed()/255.0)+" " + nf.format(color.getGreen()/255.0) + " " + nf.format(color.getBlue()/255.0));
                    appearance2.appendChild(material2);
                    
//                    group.appendChild(shape);
                    transform.appendChild(shape); // group);
                }

//                    } // TODO 2010

                if (colorIdx == firstColor) {
                    colorIdx = -1;
                    firstPassed = true;
                }
            }

            for (int i=0; i<Parts.keysUsed.size(); i++) {
                space = doc.createTextNode("\n");
                transform.appendChild(space);

                Element shape3 = doc.createElement("Shape");
                shape3.setAttribute("DEF","PIECE_KEY" + Parts.keysUsed.get(i));

                if (Parts.keysUsed.get(i) == 2) {
                    Element sphere = doc.createElement("Sphere");
                    sphere.setAttribute("radius","2");
                    shape3.appendChild(sphere);
                }
                else {
//                    Element sphere = doc.createElement("Box");
//                    sphere.setAttribute("size","2 5 2");
//                    shape3.appendChild(sphere);
                    Element sphere = doc.createElement("Sphere");
                    sphere.setAttribute("radius","1");
                    shape3.appendChild(sphere);
                }

                Element appearance2= doc.createElement("Appearance");
                shape3.appendChild(appearance2);

                Element material2 = doc.createElement("Material");
                material2.setAttribute("diffuseColor",nf.format(colors[i].getRed()/255.0)+" " + nf.format(colors[i].getGreen()/255.0) + " " + nf.format(colors[i].getBlue()/255.0));
                appearance2.appendChild(material2);

                transform.appendChild(shape3); // group);

                Comment comment = doc.createComment(colorNames[i% colors.length]);
                transform.appendChild(comment);
            }

            for (int i=0; i<Parts.locksUsed.size(); i++) {
                space = doc.createTextNode("\n");
                transform.appendChild(space);

                Element shape4 = doc.createElement("Shape");
                shape4.setAttribute("DEF","PIECE_LOCK" + Parts.locksUsed.get(i));

                Element box = doc.createElement("Box");
                box.setAttribute("size","2 2 2");
                shape4.appendChild(box);

                Element appearance2= doc.createElement("Appearance");
                shape4.appendChild(appearance2);

                Element material2 = doc.createElement("Material");
                material2.setAttribute("diffuseColor",nf.format(colors[i].getRed()/255.0)+" " + nf.format(colors[i].getGreen()/255.0) + " " + nf.format(colors[i].getBlue()/255.0));
                appearance2.appendChild(material2);

                transform.appendChild(shape4); // group);

                Comment comment = doc.createComment(colorNames[i% colors.length]);
                transform.appendChild(comment);
            }

            NumberFormat nf2 = NumberFormat.getNumberInstance();

            for (j=0; j<points.size(); j++) {

                if (points.get(j)[3] == '_')
                    continue;
                
                space = doc.createTextNode("\n");
                transform.appendChild(space);

                Element inner_transform = doc.createElement("Transform");
                inner_transform.setAttribute("DEF","POINT"+j + "_id_" + points.get(j)[5].intValue());

                inner_transform.setAttribute("translation", nf2.format(points.get(j)[IEdge.X]) + " " + nf2.format(points.get(j)[IEdge.Y]) + " " + nf2.format(points.get(j)[IEdge.Z]));

                if (points.get(j)[4] == 0) { // TODO 2d_poly was >=0
                    Element shape = doc.createElement("Shape");
                    shape.setAttribute("USE","PIECE_"+ ((char)points.get(j)[3].intValue()) + (points.get(j)[6] != 0? ((char)points.get(j)[6].intValue()-1) : ""));
                    inner_transform.appendChild(shape);
                }

                // specialExists?
                if (points.get(j)[4] == 1.0) { // && this.shape == Shape.HEXA_PYRAMID) {
                    Element shape2 = doc.createElement("Shape");
                    shape2.setAttribute("USE","PIECE_"+ ((char)points.get(j)[3].intValue()) + "2");
                    inner_transform.appendChild(shape2);
                }

                if (points.get(j)[4] < 0) {
                    if (points.get(j)[4] < -1000) {
                        Element shape2 = doc.createElement("Shape");
///
//                        Element box = doc.createElement("Box");
//                        box.setAttribute("size","2 2 2");
//                        shape2.appendChild(box);

//                        Element appearance2= doc.createElement("Appearance");
//                        shape2.appendChild(appearance2);
                        // TODO setColor
///
                        shape2.setAttribute("USE","PIECE_LOCK" + (- points.get(j)[4].intValue() - 1000));
                        inner_transform.appendChild(shape2);
                    }
                    else {
                        Element shape2 = doc.createElement("Shape");
                        shape2.setAttribute("USE","PIECE_KEY" + (- points.get(j)[4].intValue()));
                        inner_transform.appendChild(shape2);
//                    System.out.println(((char)points.get(j)[3].intValue()) + " has key");
                    }
                }

                transform.appendChild(inner_transform);

            }

            space = doc.createTextNode("\n");
            transform.appendChild(space);
            space = doc.createTextNode("\n");
            scene.appendChild(space);
            space = doc.createTextNode("\n");
            root.appendChild(space);

            root.appendChild(scene);

            // Serialize the document onto System.out
            TransformerFactory xformFactory = TransformerFactory.newInstance();
            Transformer idTransform = xformFactory.newTransformer();
//            idTransform.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_PUBLIC, x3d.getPublicId());
            idTransform.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM, x3d.getSystemId());
            Source input = new DOMSource(doc);

            StringWriter writer = new StringWriter();
            Result output = new StreamResult(writer);
            idTransform.transform(input, output);

            String html =

                "<html>\n" +
                "<head>\n" +
                "    <script type='text/javascript' src='https://x3dom.org/release/x3dom-full.js'></script>\n" +
                "    <link rel='stylesheet' type='text/css' href='https://x3dom.org/release/x3dom.css' />\n" +
                "</head>\n" +
                "<body>\n"

                + writer +

                "\n</body>\n" +
                "</html>\n";

            Files.writeString(Path.of(filename + ".html"), html, StandardCharsets.UTF_8);
            Files.writeString(Path.of(filename + ".x3d"), writer);


//            String pfiles;
//            try {
//                pfiles = System.getenv("ProgramFiles");
//            }
//            catch (SecurityException e) {
//                pfiles = "C:\\Program Files";
//            }
//            Runtime.getRuntime().exec(pfiles + "\\Pinecoast\\SwirlViewer\\SwirlVw.exe " + filename + ".x3d");

//            if (process != null)
//                process.destroy();

            if (EngineStrategy.graphIt ) {// || EngineStrategy.GRAPH_FOR_ALL)
                if (! EngineStrategy.INTERNAL_VIEWER) {
					System.out.println(filename + ".x3d");
                    process = Runtime.getRuntime().exec("windows\\SwirlVw2.8.4.exe " + filename  + ".x3d");
				}
                else
                {

                    final String fname = filename + ".x3d";
//                    if (jf != null)
//                        jf.dispose();

                    new SwingWorker<Integer,Void>() {
                        public Integer doInBackground() {
//                            jf = new X3dViewer(new String[] {fname});
//                            jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                            return 0;
                        }
                        public void done() {
                            try {get(); } catch (Exception e) {e.printStackTrace();}
                        }
                    }.execute();

                }
            }


//            throw new IOException();
        }
        catch (FactoryConfigurationError e) {
            System.out.println("Could not locate a JAXP factory class");
        }
        catch (ParserConfigurationException e) {
            System.out.println("Could not locate a JAXP DocumentBuilder class");
        }
        catch (IOException e) {
            System.out.println("Could not locate the x3d viewer\n");
            System.out.println("please download http://www.pinecoast.com/SwirlVw_2804_setup.exe");
        }
        catch (Exception e) { // DOMException/ TransformerConfigurationException/ TransformerException
            e.printStackTrace();
        }

    }

//    @SuppressWarnings(LOCAL_VARIABLE, {"s"})
    /** build the xml of the grid. same as in the beanShell so no generic... */
    public void showGrid(IGrid grid) {
        int cells = grid.getCells().size();
        ArrayList orig;
        orig = new ArrayList();

        for (int i=0; i<cells; i++) {
            ICellPart a = grid.getCells().get(i);
            orig.add(a.getPartId());
            if (!a.isPlaceHolder())
                a.setPartId('A');
         }

        setArgs("customGrid");

        int currCellIndex = 0;
        while (currCellIndex<cells && ( grid.getCells().get(currCellIndex).isEmptyPartId() || grid.getCells().get(currCellIndex).isPlaceHolder()) )
            ++currCellIndex;

        graphIt(grid.getCells().get(currCellIndex));
        buildXml("A");

        for (int i=0; i<grid.getCells().size(); i++)
            grid.getCells().get(i).setPartId((Integer)orig.get(i));

    }


}
