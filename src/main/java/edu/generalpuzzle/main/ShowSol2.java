package edu.generalpuzzle.main;

import edu.generalpuzzle.infra.*;
import edu.generalpuzzle.infra.engines.EngineStrategy;
import bsh.Interpreter;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 11/04/2009
 */
public class ShowSol2 { // show solution like \"A A A B B C\" wit dangling edges (=keys)

    public static void main(String args[]) throws bsh.EvalError {

        String argsToScript[];

        if (args.length == 0) {
            String puzzle = JOptionPane.showInputDialog("puzzle + extra argument (like 2d 8 8)");
            if (puzzle == null || puzzle.length() == 0) 
                return;
        
            args = puzzle.split(" ");
        }

        argsToScript = new String[args.length-1];
        for (int i=1; i<args.length; i++)
            argsToScript[i-1] = args[i]; //

        Interpreter interpreter = new Interpreter();

        interpreter.set("bsh.args", argsToScript);

        IGrid grid;

        try { grid = (IGrid) interpreter.source("config/" + args[0] + "_grid.bsh"); }
        catch (Exception e) { e.printStackTrace(); return; }

        if (grid == null) {
            System.out.println(interpreter.get("error"));
            return;
        }

        Parts parts;
        try { parts = (Parts) interpreter.source("config/" + args[0] + "_parts.bsh"); }
        catch (Exception e) { e.printStackTrace(); return; }

        for (IPart part: parts.getParts())
            parts.complete(part);

        String solInput = JOptionPane.showInputDialog("solution? (like I0 W2 P4 L5 U0 T2 N0 X0 Z3 F0 V0 Y2)");

        int newLoc[] = new int[grid.getCells().size()];

        for (int i=0; i<newLoc.length; i++)
            newLoc[i] = i;

        EngineStrategy.set_INTERNAL_VIEWER(false);

        while (solInput != null && solInput.length() > 0) {

            solInput = solInput.trim();

            if ((solInput.length()+1)/3 < parts.getParts().size())
                System.out.println("\nSHORT SOLUTION !");

            grid.getLeftCells();

            String sol = "";
           for (int i=0; i<solInput.length(); i+=3) {
                char partId = solInput.charAt(i);
               if (i+1 == solInput.length()) break;
                parts.get(partId).rotate(rotationChar(solInput.charAt(i+1)));

                grid.canPut(parts.get(partId));
                grid.goForward();

                if (sol.indexOf(partId) < 0)
                    sol += partId + " ";
            }

            for (ICellPart cell:grid.getCells())
                if (cell.getcPartId() >= 'A' && cell.getcPartId() <= 'Z') { // cell.isEmptyPartId() is NOT good ?
                    IPart ipart= parts.get((char)cell.getPartId());
                    ICellPart pcell = ipart.getCells()[cell.cellInPart];
                    cell.keysExists = pcell.keysExists;
                    for (int i=0; i<pcell.keys.length; i++)
                        cell.keys[i] = pcell.keys[i];
                }
            
            grid.show(newLoc);

            GraphIt graphIt = GraphIt.getInstance();
            GraphIt.setArgs("ShowSol2_" + args[0]);
            graphIt.graphIt(grid.getCells().get(0));
            graphIt.buildXml(sol);

            solInput = ""; //JOptionPane.showInputDialog("solution? (like I0 W2 P4 L5 U0 T2 N0 X0 Z3 F0 V0 Y2)");
        }
    }

    private static int rotationChar(char c) {
        if (c <= '9')
            return c - '0';
        else
            return c - 'a' + 10;
    }
}
