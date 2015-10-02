package thesis.core.world;

public class World
{
   /**
    * Width of the rectangular world in kilometers.
    */
   private float width;

   /**
    * Height of the rectangular world in kilometers.
    */
   private float height;

   /**
    * The world is divided into this many rows.
    */
   private int numRows;

   /**
    * The world is divided into this many columns.
    */
   private int numCols;

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
   public World(float width, float height, int numRows, int numCols)
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
   }

   /**
    * Get the physical width of the world.
    * 
    * @return Width of the world in kilometers.
    */
   public float getWidth()
   {
      return width;
   }

   /**
    * Get the physical height of the world.
    * 
    * @return Height of the world in kilometers.
    */
   public float getHeight()
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
      
      return null;
   }

   public WorldCoordinate convertCellToWorld(CellCoordinate cc)
   {
      if (cc == null)
      {
         throw new NullPointerException("cc cannot be null");
      }
      
      return null;
   }
}
