package thesis.core.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import thesis.core.common.CellCoordinate;
import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;

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
   private Set<CellCoordinate> havens;

   /**
    * All cells containing roads.
    */
   private List<CellCoordinate> roadLocations;

   /**
    * All of the edges in the road network.
    */
   private Set<RoadGroup> roadNetEdges;

   /**
    * 
    * @param width
    *           Width of the rectangular world.
    * @param height
    *           Height of the rectangular world.
    * @param numRows
    *           Divide the world into this many rows.
    * @param numCols
    *           Divide the world in this many columns.
    */
   public World(Distance width, Distance height, int numRows, int numCols)
   {
      if(width == null)
      {
         throw new NullPointerException("World width cannot be null.");
      }

      if(height == null)
      {
         throw new NullPointerException("World height cannot be null.");
      }

      if (width.asFeet() < 0)
      {
         throw new IllegalArgumentException("World width cannot be less than 0km.");
      }

      if (height.asFeet() < 0)
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

      this.width = new Distance(width);
      this.height = new Distance(height);
      this.numRows = numRows;
      this.numCols = numCols;

      this.roadNetEdges = new HashSet<RoadGroup>();

      distPerRow = new Distance();
      distPerCol = new Distance();
      distPerRow.setAsMeters(height.asMeters() / (numRows * 1.0));
      distPerCol.setAsMeters(width.asMeters() / (numCols * 1.0));

      havens = new HashSet<CellCoordinate>();
      roadLocations = new ArrayList<CellCoordinate>();
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
   public Set<CellCoordinate> getHavenLocations()
   {
      return havens;
   }

   /**
    * Get all the road connection points in the road network.
    * 
    * @return An unmodifiable view of all the road intersections and edges.
    */
   public Set<RoadGroup> getRoadNetworkEdges()
   {
      return Collections.unmodifiableSet(roadNetEdges);
   }

   /**
    * Get all cells containing roads.
    * 
    * @return All the cells that contain roads.
    */
   public List<CellCoordinate> getRoadLocations()
   {
      return roadLocations;
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
