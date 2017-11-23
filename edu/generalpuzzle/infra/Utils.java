package edu.generalpuzzle.infra;

/** static methods, no need to be inside an instance to activate them <p>
 * Created by IntelliJ IDEA.
 * Date: 06/08/2008
 */
public class Utils {

//    public static boolean dfsCompare(ICellPart original, ICellPart copy) {
//        return dfsComparePart(original, copy, original.getPartId());
//    }

    private static boolean keysMatch(ICellPart original, ICellPart copy) {
        if (Parts.keysUsed.size() == 0 || Parts.locksUsed.size() == 0)
            return true;

        if (original.keysExists != copy.keysExists)
            return false;
        else if (original.keysExists)
            for (int i=0; i<original.keys.length; i++)
                if (original.keys[i] != copy.keys[i])
                    return false;

        return true;
    }

    /** check duplicity of the parts, starting at these cells  */
	public static boolean dfsComparePart(ICellPart original, ICellPart copy, int partId) {

        if (original != null) {
            if (copy == null)
                return false;
            else if (! original.isBreadcrumb()) {
                if (copy.isBreadcrumb())
                    return false;
                else if ((partId == original.getPartId() || original.isPlaceHolder()) && copy.getPartId() == original.getPartId() && keysMatch(original, copy)  && copy.getSpecial() == original.getSpecial()) {
                    original.setBreadcrumb(copy.getId().getId());
                    copy.setBreadcrumb(copy.getId().getId());

                    for (int edge=0; edge<original.getCell().length; edge++)
                        if (original.getCell(edge) != null && (original.getCell(edge).getPartId() == partId || original.getCell(edge).isPlaceHolder() ))
                            if (! dfsComparePart(original.getCell(edge), copy.getCell(edge), partId)) // Edge3D.symmetricEdge(edge))))
                                return false;

                    // note: if diff edges in copy vs. original we will find it
                }
                else
                    return false;
            }
        }
        else if (copy != null)
            return false;

        return true;
    }

    /** removes the breadcrumbs needed by the DFS compare */
	public static void dfsUncompare(ICellPart original) {
        if (original != null && original.isBreadcrumb()) {
            original.clearBreadcrumb();

            for (int edge=0; edge<original.getCell().length; edge++)
                dfsUncompare(original.getCell(edge));
        }
    }


}
