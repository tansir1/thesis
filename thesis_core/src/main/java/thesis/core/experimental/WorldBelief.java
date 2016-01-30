package thesis.core.experimental;

import thesis.core.common.CellCoordinate;

public class WorldBelief
{
   private CellBelief cells[][];

   public WorldBelief(int numRows, int numCols, int numTgtTypes)
   {
      cells = new CellBelief[numRows][numCols];
      for(int i=0; i<numRows; ++i)
      {
         for(int j=0; j<numCols; ++j)
         {
            cells[i][j] = new CellBelief(numTgtTypes);
         }
      }
   }

   public void mergeBelief(final WorldBelief other)
   {

   }

   public CellBelief getCellBelief(CellCoordinate cell)
   {
      return getCellBelief(cell.getRow(), cell.getColumn());
   }

   public CellBelief getCellBelief(int row, int col)
   {
      return cells[row][col];
   }

}
