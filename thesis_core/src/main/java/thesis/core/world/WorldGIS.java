package thesis.core.world;

import thesis.core.common.CellCoordinate;
import thesis.core.common.WorldCoordinate;

/**
 * Conversion routines between discrete grid coordinates and continuous world
 * coordinates.
 */
public class WorldGIS
{
   /**
    * Width of the rectangular world in meters.
    */
   private double width;

   /**
    * Height of the rectangular world in meters.
    */
   private double height;

   /**
    * The world is divided into this many rows.
    */
   private int numRows;

   /**
    * The world is divided into this many columns.
    */
   private int numCols;

   /**
    * The lateral (vertical) distance spanned by a row in meters.
    */
   private double distPerRow;

   /**
    * The longitudinal (horizontal) distance spanned by a column in meters.
    */
   private double distPerCol;

   public WorldGIS()
   {

   }

   public void copy(WorldGIS copy)
   {
      this.reset(copy.width, copy.height, copy.numRows, copy.numCols);
   }

   /**
    * @param width Width of the world in meters.
    * @param height Height of the world in meters.
    * @param numRows Tessellate the world into this many rows.
    * @param numCols Tessellate the world into this many columns.
    */
   public void reset(double width, double height, int numRows, int numCols)
   {
      this.width = width;
      this.height = height;
      this.numRows = numRows;
      this.numCols = numCols;

      distPerRow = height / (numRows * 1.0);
      distPerCol = width / (numCols * 1.0);
   }

   /**
    * Get the physical width of the world.
    *
    * @return Width of the world in meters.
    */
   public double getWidth()
   {
      return width;
   }

   /**
    * Get the physical height of the world.
    *
    * @return Height of the world in meters.
    */
   public double getHeight()
   {
      return height;
   }

   /**
    * Get how many rows exist in the world.
    *
    * @return The world is divided into this many rows.
    */
   public int getRowCount()
   {
      return numRows;
   }

   /**
    * Get how many columns exist in the world.
    *
    * @return The world is divided into this many columns.
    */
   public int getColumnCount()
   {
      return numCols;
   }


   /**
    * Converts a physical world location to a discretized cell location.
    *
    * This allocates a new CellCoordinate.
    *
    * @param wc
    *           Convert this world location.
    * @return The discretized cell coordinates encapsulating the given world
    *         coordinates.
    */
   public CellCoordinate convertWorldToCell(WorldCoordinate wc)
   {
      if (wc == null)
      {
         throw new NullPointerException("wc cannot be null.");
      }

      CellCoordinate to = new CellCoordinate();
      convertWorldToCell(wc, to);
      return to;
   }

   /**
    * Converts a physical world location to a discretized cell location.
    *
    * @param from
    *           Convert this world coordinate into a cell coordinate.
    * @param to
    *           The value of the world coordinate conversion will be stored in
    *           this cell coordinate.
    */
   public void convertWorldToCell(WorldCoordinate from, CellCoordinate to)
   {
      if (from == null)
      {
         throw new NullPointerException("from cannot be null.");
      }

      int row = (int) (from.getNorth() / distPerRow);
      int col = (int) (from.getEast() / distPerCol);

      to.setCoordinate(row, col);
   }

   /**
    * Converts the given cell location to a world location. The world location
    * will be at the center of the cell.
    *
    * This allocates a new WorldCoordinate.
    *
    * @param cc
    *           The coordinate to convert.
    * @return The center of the cell in world coordinates.
    */
   public WorldCoordinate convertCellToWorld(CellCoordinate cc)
   {
      if (cc == null)
      {
         throw new NullPointerException("cc cannot be null");
      }

      WorldCoordinate to = new WorldCoordinate();
      convertCellToWorld(cc, to);
      return to;
   }

   /**
    * Converts the given cell location to a world location. The world location
    * will be at the center of the cell.
    *
    * @param from
    *           The coordinate to convert.
    * @param to
    *           The converted cell coordinate data will be stored in this world
    *           coordinate.
    */
   public void convertCellToWorld(CellCoordinate from, WorldCoordinate to)
   {
      if (from == null)
      {
         throw new NullPointerException("from cannot be null");
      }

      if (to == null)
      {
         throw new NullPointerException("to cannot be null");
      }

      convertCellToWorld(from.getRow(), from.getColumn(), to);
   }

   public void convertCellToWorld(int fromRow, int fromCol, WorldCoordinate to)
   {
      if (to == null)
      {
         throw new NullPointerException("to cannot be null");
      }

      double north = fromRow * distPerRow + (distPerRow * 0.5);
      double east = fromCol * distPerCol + (distPerCol * 0.5);

      to.setCoordinate(north, east);
   }

   /**
    * @return The maximum distance between the farthest points in the world in meters.
    */
   public double getMaxWorldDistance()
   {
      WorldCoordinate origin = new WorldCoordinate();
      WorldCoordinate maxWidthHeight = new WorldCoordinate(height, width);
      return origin.distanceTo(maxWidthHeight);
   }


   @Override
   public String toString()
   {
      return "WorldGIS [width=" + width + ", height=" + height + ", numRows=" + numRows + ", numCols=" + numCols
            + "]";
   }
}
