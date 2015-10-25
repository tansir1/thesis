package thesis.core.world;

import java.util.List;

import thesis.core.common.CellCoordinate;
import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;
import thesis.core.common.graph.Graph;
import thesis.core.serialization.world.WorldConfig;

/**
 * Conversion routines between discrete grid coordinates and continuous world
 * coordinates.
 */
public class World
{
   /**
    * Width of the rectangular world.
    */
   private Distance width;

   /**
    * Height of the rectangular world.
    */
   private Distance height;

   /**
    * The world is divided into this many rows.
    */
   private int numRows;

   /**
    * The world is divided into this many columns.
    */
   private int numCols;

   /**
    * The lateral (vertical) distance spanned by a row.
    */
   private Distance distPerRow;

   /**
    * The longitudinal (horizontal) distance spanned by a column.
    */
   private Distance distPerCol;

   /**
    * The locations of all safe havens for targets.
    */
   private List<WorldCoordinate> havens;

   /**
    * All cells containing roads.
    */
   //private List<CellCoordinate> roadLocations;

   /**
    * The road network graph.
    */
   private Graph<WorldCoordinate> roadNet;

   /**
    *
    * @param cfg
    *           Configuration data describing the world.
    */
   public World(WorldConfig cfg)
   {
      if(cfg == null)
      {
         throw new NullPointerException("World configuration data cannot be null.");
      }

      this.width = new Distance(cfg.getWorldWidth());
      this.height = new Distance(cfg.getWorldHeight());
      this.numRows = cfg.getNumRows();
      this.numCols = cfg.getNumColumns();

      this.roadNet = cfg.getRoadNetwork();

      distPerRow = new Distance();
      distPerCol = new Distance();
      distPerRow.setAsMeters(height.asMeters() / (numRows * 1.0));
      distPerCol.setAsMeters(width.asMeters() / (numCols * 1.0));

      havens = cfg.getHavens();
      //roadLocations = new ArrayList<CellCoordinate>();
   }

   /**
    * Get the physical width of the world.
    *
    * @return Width of the world.
    */
   public Distance getWidth()
   {
      return width;
   }

   /**
    * Get the physical height of the world.
    *
    * @return Height of the world.
    */
   public Distance getHeight()
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
    * Get the location of all safe havens in the world.
    *
    * @return The location of each haven.
    */
   public List<WorldCoordinate> getHavenLocations()
   {
      return havens;
   }

   /**
    * Get the graph representing the road network.  Graph vertices are intersections or dead ends.  Edges are roads.
    *
    * @return A graph representing the road network.
    */
   public Graph<WorldCoordinate> getRoadNetwork()
   {
      return roadNet;
   }

   /**
    * Get all cells containing roads.
    *
    * @return All the cells that contain roads.
    */
   /*public List<CellCoordinate> getRoadLocations()
   {
      return roadLocations;
   }*/

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

      int row = (int) (from.getNorth().asMeters() / distPerRow.asMeters());
      int col = (int) (from.getEast().asMeters() / distPerCol.asMeters());

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

      double northM = from.getRow() * distPerRow.asMeters() + (distPerRow.asMeters() * 0.5);
      double eastM = from.getColumn() * distPerCol.asMeters() + (distPerCol.asMeters() * 0.5);

      Distance north = new Distance();
      Distance east = new Distance();

      north.setAsMeters(northM);
      east.setAsMeters(eastM);

      to.setCoordinate(north, east);
   }



}
