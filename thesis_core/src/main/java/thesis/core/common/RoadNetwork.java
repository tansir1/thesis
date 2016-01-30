package thesis.core.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoadNetwork
{
   private boolean network[][];

   private List<CellCoordinate> traversableCache;
   private boolean dirtyCache;

   public RoadNetwork()
   {
      dirtyCache = false;
      traversableCache = new ArrayList<CellCoordinate>();
   }

   public void copy(RoadNetwork copy)
   {
      int numRows = copy.network.length;
      int numCols = copy.network[0].length;

      this.reset(numRows, numCols);

      List<CellCoordinate> cells = copy.getTraversableCells();
      for(CellCoordinate cell : cells)
      {
         setTraversable(cell, true);
      }
   }

   public void reset(int numRows, int numCols)
   {
      network = new boolean[numRows][numCols];
      for (int i = 0; i < numRows; ++i)
      {
         for (int j = 0; j < numCols; ++j)
         {
            network[i][j] = false;
         }
      }
   }

   public int getNumTraversable()
   {
      return getTraversableCells().size();
   }

   public void setTraversable(int row, int col, boolean traversable)
   {
      dirtyCache = true;
      network[row][col] = traversable;
   }

   public boolean isTraversable(int row, int col)
   {
      return network[row][col];
   }

   public void setTraversable(CellCoordinate cell, boolean traversable)
   {
      setTraversable(cell.getRow(), cell.getColumn(), traversable);
   }

   public boolean isTraversable(CellCoordinate cell)
   {
      return isTraversable(cell.getRow(), cell.getColumn());
   }

   public List<CellCoordinate> getTraversableCells()
   {
      if(dirtyCache)
      {
         dirtyCache = false;
         traversableCache.clear();

         for(int i=0; i<network.length; ++i)
         {
            for(int j=0; j<network[i].length; ++j)
            {
               if(network[i][j])
               {
                  traversableCache.add(new CellCoordinate(i, j));
               }
            }
         }
      }
      return traversableCache;
   }

   public List<CellCoordinate> findPath(int startRow, int startCol, int endRow, int endCol)
   {
      CellCoordinate start = new CellCoordinate(startRow, startCol);
      CellCoordinate end = new CellCoordinate(endRow, endCol);

      return findPath(start, end);
   }

   /**
    * Find a path from start to end using breadth first search.
    * @param start
    * @param end
    * @return The list of cells to traverse in order from start to end or an empty list of no such path exists.
    */
   public List<CellCoordinate> findPath(CellCoordinate startCoord, CellCoordinate endCoord)
   {
      List<Vertex> vertices = new ArrayList<Vertex>();
      for(int i=0; i<network.length; ++i)
      {
         for(int j=0; j<network[i].length; ++j)
         {
            if(network[i][j])
            {
               Vertex vert = new Vertex();
               vert.col = j;
               vert.row = i;
               vert.searchCost = Integer.MAX_VALUE;
               vert.searchParent = null;
               vertices.add(vert);
            }
         }
      }

      Vertex startVertex = findMatchingVertex(startCoord.getRow(), startCoord.getColumn(), vertices);
      Vertex endVertex = findMatchingVertex(endCoord.getRow(), endCoord.getColumn(), vertices);

      // Simple breadth-first-search algorithm
      Queue<Vertex> searchQ = new LinkedList<Vertex>();
      searchQ.add(startVertex);
      startVertex.searchCost = 0;
      boolean targetFound = false;
      while (!searchQ.isEmpty() && !targetFound)
      {
         Vertex searchMe = searchQ.remove();

         for (Vertex connected : getConnectedVertices(searchMe, vertices))
         {
            if (connected.searchCost == Integer.MAX_VALUE)
            {
               connected.searchCost = searchMe.searchCost + 1;
               connected.searchParent = searchMe;

               if (connected.row == endVertex.row && connected.col == endVertex.col)
               {
                  targetFound = true;
                  break;
               }
               else
               {
                  searchQ.add(connected);
               }
            }
         }
      }

      // Generate the path in reverse by vertex
      List<Vertex> pathByVertex = new ArrayList<Vertex>();
      pathByVertex.add(endVertex);
      Vertex iterator = endVertex;
      while (iterator.searchParent != null)
      {
         pathByVertex.add(iterator.searchParent);
         iterator = iterator.searchParent;
      }

      // pathByVertex has the path from end to start, reverse it
      Collections.reverse(pathByVertex);

      //Convert to CellCoordinates
      final int numCells = pathByVertex.size();
      List<CellCoordinate> path = new ArrayList<CellCoordinate>();
      for(int i=0; i<numCells; ++i)
      {
         CellCoordinate cell = new CellCoordinate();
         cell.setColumn(pathByVertex.get(i).col);
         cell.setRow(pathByVertex.get(i).row);
         path.add(cell);
      }

      return path;
   }

   private List<Vertex> getConnectedVertices(Vertex from, List<Vertex> allVertices)
   {
      List<Vertex> connected = new ArrayList<Vertex>();

      if (from.row > 0) // Check row below
      {
         if (network[from.row - 1][from.col])
         {
            Vertex v = findMatchingVertex(from.row - 1, from.col, allVertices);
            if(v.searchCost == Integer.MAX_VALUE)
            {
               connected.add(v);
            }
         }
      }

      if (from.row < (network.length - 1)) // Check row above
      {
         if (network[from.row + 1][from.col])
         {
            Vertex v = findMatchingVertex(from.row + 1, from.col, allVertices);
            if(v.searchCost == Integer.MAX_VALUE)
            {
               connected.add(v);
            }
         }
      }

      if (from.col > 0) // Check col before
      {
         if (network[from.row][from.col - 1])
         {
            Vertex v = findMatchingVertex(from.row, from.col - 1, allVertices);
            if(v.searchCost == Integer.MAX_VALUE)
            {
               connected.add(v);
            }
         }
      }

      if (from.col < (network[from.row].length - 1)) // Check col after
      {
         if (network[from.row][from.col + 1])
         {
            Vertex v = findMatchingVertex(from.row, from.col + 1, allVertices);
            if(v.searchCost == Integer.MAX_VALUE)
            {
               connected.add(v);
            }
         }
      }

      return connected;
   }

   private Vertex findMatchingVertex(int row, int col, List<Vertex> vertices)
   {
      int NUM_VERTS = vertices.size();
      Vertex temp = null;
      for (int i = 0; i < NUM_VERTS; ++i)
      {
         temp = vertices.get(i);
         if (temp.row == row && temp.col == col)
         {
            break;
         }
      }
      return temp;
   }

   private static class Vertex
   {
      public int row;
      public int col;
      public int searchCost;
      public Vertex searchParent;
   }
}
