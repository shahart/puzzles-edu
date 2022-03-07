package edu.generalpuzzle.infra;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Created by IntelliJ IDEA.
 * Date: 29/05/2008
 */
public class ICellPart {

    public enum edge {REF };

    public int getRots() { return edge.values().length; }

    protected CellId id;
    public ICellPart cell[];

    public int keys[]; // dangling edges
    public boolean keysExists = false;


    public static IEdge dummyEdge;

//    protected static CellId dummyCellId;

    protected int partId; // belongs to which part
    private char cpartId;
    public int cellInPart;
    protected static final int CLEAR_PIECE_ID = Integer.MAX_VALUE;
    protected static final int PLACE_HOLDER_ID = CLEAR_PIECE_ID-1;

    private int breadcrumb = 0;

    protected int special = 0; // graphIt supports 2 types of cells

    private int gridCellId = CLEAR_PIECE_ID; // occupy this cell of the grid //TODO was static, why?
    private ICellPart gridCell = null;

    public  void init(CellId id) {
        this.id = id;
        this.cell = new ICellPart[dummyEdge.getSize()];
        this.keys = new int[dummyEdge.getSize()];
     }

    public void setId(CellId id) {
        this.id = id;
    }

    /** dummyEdge, used for various tasks */
	public IEdge getDummyEdge() {
        return dummyEdge;
    }

    public ICellPart() {
        partId = CLEAR_PIECE_ID;
        cpartId = '*';
    }

    public ICellPart(CellId id) {
        this.id = id;
        this.partId = CLEAR_PIECE_ID;
        this.cpartId = '*';
    }

    public CellId getId() {
        return id;
    }

    /** returns the neighbour in the edge direction */
	protected void additionalEdges(int edge, ICellPart cellPart) {
        int symmetricEdge = dummyEdge.symmetricEdge(edge);
        cellPart.getCell()[symmetricEdge] = this;
    }

    /** special cell, as in hexPrism */
	public void setSpecial(int special) {
        this.special = special;
    }

    public int getSpecial() {
        return special;
    }

    /** see IPart.putKey */
	public void putKey(int edge, int key) {
        if (keys[edge] != 0)
            System.out.println("error: dangling edges already exists, in part " + (char)partId);

        keys[edge] = key;
        keysExists = true;
        if (cell[edge] != null) {
            System.out.println("error: edge dangling inside the part!");
            int symmetricEdge = dummyEdge.symmetricEdge(edge);
            if (cell[edge].keys[symmetricEdge] != 0)
                System.out.println("error: dangling edges mismatch, in part " + (char)partId + ". overriding");
            cell[edge].keys[symmetricEdge] = key;
        }
    }

    public void put(int edge, ICellPart cellPart, boolean createSymmetric) {
        if( (this == cellPart)) {
            System.out.println("error: self reference");
            return;
        }

        if (cellPart == null) {
            System.out.println("error: no neighbour, probably removing an cell");
            cell[edge] = null;
            return;
        }

        cell[edge] = cellPart;

        if (createSymmetric)
            additionalEdges(edge, cellPart);
    }

    public ICellPart[] getCell() {
        return cell;
    }

    public ICellPart getCell(int edge) {
        return cell[edge];
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(cpartId);
        sb.append(" #");
        if (id == null)
            sb.append("null");
        else
            sb.append(id);
        sb.append(" edges [" );
        if (cell != null)
            for (int edge=0; edge<cell.length; edge++)
                if (cell[edge] != null)
                    sb.append(" " + dummyEdge.stringValue(edge) + ":" + Integer.toString(edge) + (keys[edge] != 0 ? ("# " + keys[edge]) : "") + ">>" + cell[edge].getId());
                else if (keys[edge] != 0)
                    sb.append(" " + dummyEdge.stringValue(edge) + ":" + Integer.toString(edge) + " # " + keys[edge]);
        sb.append("] ");
        return sb.toString();
    }

    public int getPartId() {
        return partId;
    }

    public char getcPartId() {
        return cpartId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
        if (partId < 255)
            this.cpartId = (char)partId;
    }

    public void setCellInPart(int i) {
        cellInPart = i;
    }

    public int getCellInPart() {
        return cellInPart;
    }

    public void clearPartId() {
        partId = CLEAR_PIECE_ID;
        cpartId = '*';
    }

    public boolean isEmptyPartId() {
        return partId == CLEAR_PIECE_ID;
    }

    /** breadcrumb, used when marking in DFS for example */
	public void setBreadcrumb(int breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    public void clearBreadcrumb() {
        breadcrumb = 0;
    }

    public int getBreadcrumb() {
        return breadcrumb;
    }

    public boolean isBreadcrumb() {
        return breadcrumb > 0;
    }

    public ICellPart getGridCell() {
        return gridCell;
    }

    public int getGridCellId() {
        return gridCellId;
    }

    public void setGridCell(ICellPart gridCell) {
        this.gridCell = gridCell;
    }

    public void setGridCellId(int gridCellId) {
        this.gridCellId = gridCellId;
    }

    public void clearGridCellId() {
        this.gridCellId = CLEAR_PIECE_ID;
    }

    public boolean isClearGridCellId() {
        return gridCellId == CLEAR_PIECE_ID;
    }

    public boolean isPlaceHolder() {
        return partId == PLACE_HOLDER_ID;
    }

    public void setPlaceHolder() {
         partId = PLACE_HOLDER_ID;
         cpartId = '_';
    }

    /** do a transformation, for example, clock-wise */
	protected void swap(int ... rotate) {
        ICellPart firstCell = cell[rotate [0]];
        int firstKey = keys[rotate [0]];
        for (int i = 0; i < rotate.length-1; i++) {
            cell[rotate [i]] = cell[rotate [i+1]];
            keys[rotate [i]] = keys[rotate [i+1]];
        }
        cell[rotate [rotate.length-1]] = firstCell;
        keys[rotate [rotate.length-1]] = firstKey;
    }

    public void rotate(int param) {
        System.out.println("error: implement concrete ICellPart::rotate(int param)");
    }

    public int availRotations() {
        return getRots() - (edge.values()[0].toString().equals("REF") ? 1 : 0); // dummyEdge.getSize() / 2;
    }

//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        final ICellPart that = (ICellPart) o;
//
//        if (id != null ? !id.equals(that.id) : that.id != null) return false;
//
//        return true;
//    }

    public boolean equals(Object o) {
        final ICellPart that = (ICellPart) o;
        return id.getId() == that.id.getId();
    }

    // defined, as equals was defined
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }

    public Element graphCell(Document doc) {
        Element box = doc.createElement("Box");
        box.setAttribute("size","7 7 7");
        return box;
    }

}
