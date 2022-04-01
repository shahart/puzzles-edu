/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.generalpuzzle.infra.engines.dlx_hadoop;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.generalpuzzle.infra.engines.ParallelEngineStrategy;

/**
 * A generic solver for tile laying problems using Knuth's dancing link
 * algorithm. It provides a very fast backtracking data structure for problems
 * that can expressed as a sparse boolean matrix where the goal is to select a
 * subset of the rows such that each column has exactly 1 true in it.
 * 
 * The application gives each column a name and each row is named after the
 * set of columns that it has as true. Solutions are passed back by giving the 
 * selected rows' names.
 * 
 * The type parameter ColumnName is the class of application's column names.
 */
public final class DancingLinksGeneric<ColumnName> {
//  private static final Log LOG =   LogFactory.getLog(DancingLinksGeneric.class.getName());
    public static final Logger LOG = Logger.getLogger(DancingLinksGeneric.class.getName());

    public long branchesTotal; // tals
    public static boolean considerKeys;
    public int solutionInDepth;
    public long branches[];
    public long updatesTotal;
    public long updates[];
    public boolean S_heuristic = true;

    protected ParallelEngineStrategy parent;

    public void init(int size) {
    }

  /**
   * A cell in the table with up/down and left/right links that form doubly
   * linked lists in both directions. It also includes a link to the column
   * head.
   */
  private static class Node<ColumnName> {
    Node<ColumnName> left;
    Node<ColumnName> right;
    Node<ColumnName> up;
    Node<ColumnName> down;
    ColumnHeader<ColumnName> head;
      int rowId;
    
    Node(Node<ColumnName> l, Node<ColumnName> r, Node<ColumnName> u, 
         Node<ColumnName> d, ColumnHeader<ColumnName> h) {
      left = l;
      right = r;
      up = u;
      down = d;
      head = h;
    }
    
    Node() {
      this(null, null, null, null, null);
    }
  }
  
  /**
   * Column headers record the name of the column and the number of rows that 
   * satisfy this column. The names are provided by the application and can 
   * be anything. The size is used for the heuristic for picking the next 
   * column to explore.
   */
  private static final class ColumnHeader<ColumnName> extends Node<ColumnName> {
    final ColumnName name;
    int size;
      int id;
    
    ColumnHeader(ColumnName n, int s,int id) {
      name = n;
      size = s;
        this.id = id;
      head = this;
    }
    
    ColumnHeader() {
      this(null, 0,0);
    }
  }
  
  /**
   * The head of the table. Left/Right from the head are the unsatisfied 
   * ColumnHeader objects.
   */
  private final ColumnHeader<ColumnName> head;
  
  /**
   * The complete list of columns.
   */
  private final List<ColumnHeader<ColumnName>> columns;
  
  public DancingLinksGeneric(ParallelEngineStrategy parent) {
    head = new ColumnHeader<ColumnName>(null, 0,0);
    head.left = head;
    head.right = head;
    head.up = head;
    head.down = head;
      this.parent = parent;
    columns = new ArrayList<ColumnHeader<ColumnName>>(200);
  }
  
  /**
   * Add a column to the table
   * @param name The name of the column, which will be returned as part of 
   *             solutions
   * @param primary Is the column required for a solution?
   */
  public void addColumn(ColumnName name, boolean primary,int id) {
    ColumnHeader<ColumnName> top = new ColumnHeader<ColumnName>(name, 0,id);
    top.up = top;
    top.down = top;
    if (primary) {
      Node<ColumnName> tail = head.left;
      tail.right = top;
      top.left = tail;
      top.right = head;
      head.left = top;
    } else {
      top.left = top;
      top.right = top;
    }
    columns.add(top);
  }
  
  /**
   * Add a column to the table
   * @param name The name of the column, which will be included in the solution
   */
  public void addColumn(ColumnName name, int id) {
    addColumn(name, true,id);
  }
  
  /**
   * Get the number of columns.
   * @return the number of columns
   */
  public int getNumberColumns() {
    return columns.size();
  }
  
  /**
   * Get the name of a given column as a string
   * @param index the index of the column
   * @return a string representation of the name
   */
  public String getColumnName(int index) {
    return columns.get(index).name.toString();
  }

  /**
   * Add a row to the table. 
   * @param values the columns that are satisfied by this row
   */
  public void addRow(boolean[] values, int rowId) {
    Node<ColumnName> prev = null;
    for(int i=0; i < values.length; ++i) {
      if (values[i]) {
        ColumnHeader<ColumnName> top = columns.get(i);
        top.size += 1;
        Node<ColumnName> bottom = top.up;
        Node<ColumnName> branch = new Node<ColumnName>(null, null, bottom,
                                                     top, top);
        branch.rowId = rowId;
        bottom.down = branch;
        top.up = branch;
        if (prev != null) {
          Node<ColumnName> front = prev.right;
          branch.left = prev;
          branch.right = front;
          prev.right = branch;
          front.left = branch;
        } else {
          branch.left = branch;
          branch.right = branch;
        }
        prev = branch;
      }
    }
  }

  /**
   * Applications should implement this to receive the solutions to their
   * problems.
   */
  public interface SolutionAcceptor<ColumnName> {
    /**
     * A callback to return a solution to the application.
     * @param value a List of List of ColumnNames that were satisfied by each
     *              selected row
     */
    public void solution(List<List<ColumnName>> value, int rowIds[]);

      public void track(char part, int depth2);
      public void track2(List<ColumnName> part, int row);
      public boolean middlePut(List<List<ColumnName>> value, int rowsIds[]);

  }


  /**
   * Find the column with the fewest choices.
   * @return The column header
   */
  private ColumnHeader<ColumnName> findBestColumn() {
    int lowSize = Integer.MAX_VALUE;
    ColumnHeader<ColumnName> result = null;
    ColumnHeader<ColumnName> current = (ColumnHeader<ColumnName>) head.right;
    while (current != head) {
      if (current.size < lowSize) { // && current.id != 5) {
        lowSize = current.size;
        result = current;
      if (!S_heuristic)
          return result;
      }
      current = (ColumnHeader<ColumnName>) current.right;
    }
    return result;
  }

  /**
   * Hide a column in the table
   * @param col the column to hide
   */
  private int coverColumn(ColumnHeader<ColumnName> col) {
//    LOG.fine("cover " + col.head.name);
    // remove the column
      int updates=0;
    col.right.left = col.left;
    col.left.right = col.right;
    Node<ColumnName> row = col.down;
    while (row != col) {
      Node<ColumnName> branch = row.right;
      while (branch != row) {
        branch.down.up = branch.up;
        branch.up.down = branch.down;
        branch.head.size -= 1;
        branch = branch.right;
          ++updates;
      }
      row = row.down;
    }
      updatesTotal+=updates;
      return updates;
  }

  /**
   * Uncover a column that was hidden.
   * @param col the column to unhide
   */
  private void uncoverColumn(ColumnHeader<ColumnName> col) {
//    LOG.fine("uncover " + col.head.name);
    Node<ColumnName> row = col.up;
    while (row != col) {
      Node<ColumnName> branch = row.left;
      while (branch != row) {
        branch.head.size += 1;
        branch.down.up = branch;
        branch.up.down = branch;
        branch = branch.left;
      }
      row = row.up;
    }
    col.right.left = col;
    col.left.right = col;
  }

  /**
   * Get the name of a row by getting the list of column names that it
   * satisfies.
   * @param row the row to make a name for
   * @return the list of column names
   */
  private List<ColumnName> getRowName(Node<ColumnName> row) {
    List<ColumnName> result = new ArrayList<ColumnName>();
    result.add(row.head.name);
    Node<ColumnName> branch = row.right;
    while (branch != row) {
      result.add(branch.head.name);
      branch = branch.right;
    }
    return result;
  }

  int depth;

    /**
   * Find a solution the the problem.
   * @param partial a temporary datastructure to keep the current partial
   *                answer in
   * @param output the acceptor for the results that are found
   * @return the number of solutions found
   */
  private int search(List<Node<ColumnName>> partial, SolutionAcceptor<ColumnName> output) {

//        if (EngineStrategy.inPause >= 1) parent.pause();

//        if (IEngineStrategy.inPause >= 1) pause();

//        List<ColumnName> part2 = getRowName(partial.get(partial.size()-1)); // row
//        for(ColumnName item: part2) {
//            if (item instanceof DlxEngineStrategy.Part) {
//                DlxEngineStrategy.Part part = (DlxEngineStrategy.Part) item;
//                char partId = part.getName().charAt(0);
//                if (partId >= 'A' && partId <= 'Z')
//                    output.track(partId, depth);
//                break;
//            }
//        }

//        if (partial.size() == 2) { // getSplits(4) in DlxEngineStrategy
//            for (int i=0; i< partial.size(); i++) {
//                List<ColumnName> part2 = getRowName(partial.get(i)); // row
//                for(ColumnName item: part2) {
//                    if (item instanceof DlxEngineStrategy.Part) {
//                        DlxEngineStrategy.Part part = (DlxEngineStrategy.Part) item;
//                        char partId = part.getName().charAt(0);
//                        if (partId >= 'A' && partId <= 'Z')
//                            System.out.print(partId);
//                        break;
//                    }
//                }
//            }
//            System.out.println();
//        }

      branchesTotal++;
      branches[depth]++;
    int results = 0;

        List<List<ColumnName>> result = null;
        int rowIds[] = null;
        if (considerKeys) {
            result = new ArrayList<List<ColumnName>>(depth);
             rowIds = new int [depth];
            int rowIdx = 0;
            for(Node<ColumnName> row: partial) {
                result.add(getRowName(row));
                rowIds[rowIdx++] = row.rowId;
            }
            if (! output.middlePut(result, rowIds))
                return results;
        }

    if (head.right == head   
            ||  depth == solutionInDepth) { // var8 // TODO rem   ove
        if (! considerKeys) {
            result = new ArrayList<List<ColumnName>>(depth);
            rowIds = new int [depth];
            int rowIdx = 0;
            for(Node<ColumnName> row: partial) {
                result.add(getRowName(row));
                rowIds[rowIdx++] = row.rowId;
            }
        }
      output.solution(result, rowIds);
      results += 1;
    } else {
      ColumnHeader<ColumnName> col = findBestColumn();
      if (col.size > 0) {
          // DO
        updates[depth]+=coverColumn(col);
        Node<ColumnName> row = col.down;
        while (row != col) {


          partial.add(row);
            depth++;
          Node<ColumnName> branch = row.right;
          while (branch != row) {
            updates[depth]+=coverColumn(branch.head);
            branch = branch.right;
          }
            // DEEPER
          results += search(partial, output);
            // UNDO
            depth--;//= partial.size();
          partial.remove(depth );
//            output.track(' ', depth);
          branch = row.left;
          while (branch != row) {
            uncoverColumn(branch.head);
            branch = branch.left;
          }
          row = row.down;
        }
        uncoverColumn(col);
      }
    }
    return results;
  }

  /**
   * Generate a list of prefixes down to a given depth. Assumes that the
   * problem is always deeper than depth.
   * @param depth the depth to explore down
   * @param choices an array of length depth to describe a prefix
   * @param prefixes a working datastructure
   */
  private void searchPrefixes(int depth, int[] choices, // TODO rename
                              List<int[]> prefixes) {
    branches[choices.length - depth] ++;
    if (depth == 0) {
      prefixes.add(choices.clone());
    } else {
      ColumnHeader<ColumnName> col = findBestColumn();
      if (col.size > 0) {
        updates[choices.length- depth] += coverColumn(col);
        Node<ColumnName> row = col.down;
        int rowId = 0;
        while (row != col) {
          Node<ColumnName> branch = row.right;
          while (branch != row) {
            updates[choices.length - depth] += coverColumn(branch.head);
            branch = branch.right;
          }
          choices[choices.length - depth] = rowId;
          searchPrefixes(depth - 1, choices, prefixes);
          branch = row.left;
          while (branch != row) {
            uncoverColumn(branch.head);
            branch = branch.left;
          }
          row = row.down;
          rowId += 1;
        }
        uncoverColumn(col);
      }
    }
  }

  /**
   * Generate a list of row choices to cover the first moves.
   * @param depth the length of the prefixes to generate
   * @return a list of integer arrays that list the rows to pick in order
   */
  public List<int[]> split(int depth) {
    int[] choices = new int[depth];
    List<int[]> result = new ArrayList<int[]>(10000);
    searchPrefixes(depth, choices, result);
    return result;
  }

  /**
   * Make one move from a prefix
   * @param goalRow the row that should be choosen
   * @return the row that was found
   */
  private Node<ColumnName> advance(int goalRow) {
      branches[depth] ++;
      ColumnHeader<ColumnName> col = findBestColumn();
    if (col.size > 0) {
      updates[depth] += coverColumn(col);
      Node<ColumnName> row = col.down;
      int id = 0;
      while (row != col) {
        if (id == goalRow) {
          Node<ColumnName> branch = row.right;
          while (branch != row) {
            updates[depth] += coverColumn(branch.head);
            branch = branch.right;
          }
          return row;
        }
        id += 1;
        row = row.down;
      }
    }
    return null;
  }

  /**
   * Undo a prefix exploration
   * @param row
   */
  private void rollback(Node<ColumnName> row) {
    Node<ColumnName> branch = row.left;
    while (branch != row) {
      uncoverColumn(branch.head);
      branch = branch.left;
    }
    uncoverColumn(row.head);
  }

    public void show(int [] prefix, SolutionAcceptor<ColumnName> output) {
        List<Node<ColumnName>> choices = new ArrayList<Node<ColumnName>>();
        parent.lastSolutionDL = "";
        for(int prefi: prefix) {
          choices.add(advance(prefi));
        }
        for (int i=0; i< choices.size(); i++) { //choices.size(); i++) {
//            if (choices.get(i) == null)
//                continue;
            List<ColumnName> part2 = getRowName(choices.get(i)); // row
            int r = choices.get(i).rowId;
            output.track2(part2, r);
            // getRowId from chouces.get(i)
        }
        for(int i=prefix.length-1; i >=0; --i) {
          rollback(choices.get(i));
        }
//        System.out.println(parent.lastSolutionDL);
    }

  /**
   * Given a prefix, find solutions under it.
   * @param prefix a list of row choices that control which part of the search
   *               tree to explore
   * @param output the output for each solution
   * @return the number of solutions
   */
  public int solve(int[] prefix, SolutionAcceptor<ColumnName> output) {
    List<Node<ColumnName>> choices = new ArrayList<Node<ColumnName>>();
      depth = prefix.length;//prefix.length;
    for(int prefi: prefix) {
      choices.add(advance(prefi));
    }
//      depth = choices.size();
//      List<ColumnName> part2 = getRowName(choices.get(0));
//      for(ColumnName item: part2) {
//          if (item instanceof DlxEngineStrategy.Part) {
//              DlxEngineStrategy.Part part = (DlxEngineStrategy.Part) item;
//              char partId = part.getName().charAt(0);
//              if (partId >= 'A' && partId <= 'Z')
//                  output.track(partId, 0);
//              break;
//          }
//      }


            for (int i=0; i< choices.size(); i++) {
                List<ColumnName> part2 = getRowName(choices.get(i)); // row
                int r = choices.get(i).rowId;
                output.track2(part2, r);
                 // getRowId from chouces.get(i)
            }
//      System.out.println();


    int result = search(choices, output);
    for(int i=prefix.length-1; i >=0; --i) {
      rollback(choices.get(i));
    }
    return result;
  }
  
  /**
   * Solve a complete problem
   * @param output the acceptor to receive answers
   * @return the number of solutions
   */
  public int solve(SolutionAcceptor<ColumnName> output) {
    return search(new ArrayList<Node<ColumnName>>(), output);
  }
  
}
