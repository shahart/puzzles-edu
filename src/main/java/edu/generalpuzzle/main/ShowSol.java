package edu.generalpuzzle.main;

import edu.generalpuzzle.infra.*;
import edu.generalpuzzle.infra.engines.EngineStrategy;
import bsh.Interpreter;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 29/03/2009
 */
public class ShowSol { // show solution like \"A A A B B C\" without dangling edges (=keys)

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

        if (parts.keysUsed.size() > 0 || parts.locksUsed.size() > 0) {
            System.out.println("\nConsiderKeys is TRUE, you should use the ShowSol2 version");
        }

        String solInput = JOptionPane.showInputDialog("solution? (like A A A B B C)");
//                 "U U U L L L L W U U L N N W W Y Y N N N W W Z I Y T _ _ Z Z Z I Y T _ _ Z F F I T T T X F F V I P P X X X F V I P P P X V V V"; //

        int newLoc[] = new int[grid.getCells().size()];

        for (int i=0; i<newLoc.length; i++)
            newLoc[i] = i;

        EngineStrategy.set_INTERNAL_VIEWER(false);

        while (solInput != null && solInput.length() > 0) {

            solInput = solInput.trim();

            if (solInput.length()/2+1 < grid.getCells().size()) {
                System.out.println("\nSHORT SOLUTION !");
                while (solInput.length()/2 < grid.getCells().size()-1)
                    solInput += " _";
            }

            String sol = "";

            int gridCellIndex = 0;
                  
            for (int i=0; i<solInput.length(); i+=2) { // TODO add locks, need to save the rotationIndex in the flat presentation
                char partId = solInput.charAt(i);
                ICellPart cell = grid.getCells().get(gridCellIndex);

                cell.setPartId(partId);
                gridCellIndex++;

                if (sol.indexOf(partId) < 0)
                    sol += partId + " ";
            }

            grid.show(newLoc);

            GraphIt graphIt = GraphIt.getInstance();
            GraphIt.setArgs("ShowSol_" + args[0]);
            graphIt.graphIt(grid.getCells().get(0));
            graphIt.buildXml(sol);

            solInput = JOptionPane.showInputDialog("solution? (like A A A B B C)");
                // "U U U L L L L W U U L N N W W Y Y N N N W W Z I Y T _ _ Z Z Z I Y T _ _ Z F F I T T T X F F V I P P X X X F V I P P P X V V V"; //
        }
    }
}
