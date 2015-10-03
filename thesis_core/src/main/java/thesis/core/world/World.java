package thesis.core.world;

/**
 *Conversion routines between discrete grid coordinates and continuous world coordinates.
 */
public class World
{
   /**
    * Width of the rectangular world in kilometers.
    */
   private double width;

   /**
    * Height of the rectangular world in kilometers.
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
    * The lateral (vertical) distance in kilometers spanned by a row.
    */
   private double distPerRow;

   /**
    * The longitudinal (horizontal) distance in kilometers spanned by a column.
    */
   private double distPerCol;

   /**
    * 
    * @param width
    *           Width of the rectangular world in kilometers.
    * @param height
    *           Height of the rectangular world in kilometers.
    * @param numRows
    *           Divide the world into this many rows.
    * @param numCols
    *           Divide the world in this many columns.
    */
   public World(double width, double height, int numRows, int numCols)
   {
      if (width < 0)
      {
         throw new IllegalArgumentException("World width cannot be less than 0km.");
      }

      if (height < 0)
      {
         throw new IllegalArgumentException("World height cannot be less than 0km.");
      }

      if (numRows < 0)
      {
         throw new IllegalArgumentException("Number of rows in the world cannot be less than 0.");
      }

      if (numCols < 0)
      {
         throw new IllegalArgumentException("Number of columns in the world cannot be less than 0.");
      }

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
    * @return Width of the world in kilometers.
    */
   public double getWidth()
   {
      return width;
   }

   /**
    * Get the physical height of the world.
    * 
    * @return Height of the world in kilometers.
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

      double north = from.getRow() * distPerRow + (distPerRow * 0.5);
      double east = from.getColumn() * distPerCol + (distPerCol * 0.5);

      to.setCoordinate(north, east);
   }
}
