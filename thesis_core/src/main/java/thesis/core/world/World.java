package thesis.core.world;

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

      int row = (int) (wc.getNorth() / distPerRow);
      int col = (int) (wc.getEast() / distPerCol);

      return new CellCoordinate(row, col);
   }

   /**
    * Converts a physical world location to a discretized cell location.
    * 
    * @param wc
    *           Convert this world coordinate into a cell coordinate.
    * @param cc
    *           The value of the world coordinate conversion will be stored in
    *           this cell coordinate.
    */
   public void convertWorldToCell(WorldCoordinate wc, CellCoordinate cc)
   {
      if (wc == null)
      {
         throw new NullPointerException("wc cannot be null.");
      }

      int row = (int) (wc.getNorth() / distPerRow);
      int col = (int) (wc.getEast() / distPerCol);

      cc.setCoordinate(row, col);
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

      double north = cc.getRow() * distPerRow;
      double east = cc.getColumn() * distPerCol;

      return new WorldCoordinate(north, east);
   }

   /**
    * Converts the given cell location to a world location. The world location
    * will be at the center of the cell.
    * 
    * @param cc
    *           The coordinate to convert.
    * @param wc
    *           The converted cell coordinate data will be stored in this world
    *           coordinate.
    */
   public void convertCellToWorld(CellCoordinate cc, WorldCoordinate wc)
   {
      if (cc == null)
      {
         throw new NullPointerException("cc cannot be null");
      }

      double north = cc.getRow() * distPerRow;
      double east = cc.getColumn() * distPerCol;

      wc.setCoordinate(north, east);
   }
}
