package thesis.core.world;

/**
 * A discretized row and column position within the world.
 *
 * Treats the world like the first quadrant of a cartesian coordinate plane.
 * Rows increase in index value above the origin and decrease in index below the
 * origin. Columns increase traversing right from the origin and decrease when
 * iterating left of the origin.
 */
public class CellCoordinate
{
   /**
    * The index of a row within the world.
    */
   private int row;

   /**
    * The index of a column within the world.
    */
   private int col;

   /**
    * Initialize a cell coordinate at the origin.
    */
   public CellCoordinate()
   {
      row = 0;
      col = 0;
   }

   /**
    * Initialize a cell coordinate at the given location.
    * 
    * @param row
    *           The number of rows above the origin.
    * @param col
    *           The number of columns to the right of the origin.
    */
   public CellCoordinate(int row, int col)
   {
      this.row = row;
      this.col = col;
   }

   /**
    * Initialize a cell coordinate by cloning the given coordinate.
    * 
    * @param wc
    *           Clone this coordinate.
    */
   public CellCoordinate(CellCoordinate cc)
   {
      this.row = cc.row;
      this.col = cc.col;
   }

   /**
    * Get the row index above the world origin.
    * 
    * @return Index of the row above the origin.
    */
   public int getRow()
   {
      return row;
   }

   /**
    * Get the column index to the right of the world origin.
    * 
    * @return Index of the column to the right of the origin.
    */
   public int getColumn()
   {
      return col;
   }

   /**
    * Set the row index above the world origin.
    * 
    * @param row
    *           Index of the row above the origin.
    */
   public void setRow(int row)
   {
      this.row = row;
   }

   /**
    * Set the column index to the right of the world origin.
    * 
    * @param col
    *           Index of the column to the right of the origin.
    */
   public void setColumn(int col)
   {
      this.col = col;
   }

   /**
    * Set the row and column indexes.
    * 
    * @param row
    *           Row index above of the world origin.
    * @param col
    *           Column index to the right of the world origin.
    */
   public void setCoordinate(int row, int col)
   {
      this.row = row;
      this.col = col;
   }

   /**
    * Shift the current coordinate position by the specified amount.
    * 
    * @param deltaRow
    *           Move the coordinate row index by this much.
    * @param deltaCol
    *           Move the coordinate column index by this much.
    */
   public void translate(int deltaRow, int deltaCol)
   {
      this.row += deltaRow;
      this.col += deltaCol;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("Row: ");
      sb.append(row);
      sb.append(" Col: ");
      sb.append(col);
      return sb.toString();
   }
}
