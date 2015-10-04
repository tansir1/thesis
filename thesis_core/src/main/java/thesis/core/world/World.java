package thesis.core.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.utilities.LoggerIDs;

/**
 * Conversion routines between discrete grid coordinates and continuous world
 * coordinates.
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
    * The locations of all safe havens for targets.
    */
   private Set<CellCoordinate> havens;

   /**
    * The shared random number generator.
    */
   private Random randGen;

   /**
    * All cells containing roads.
    */
   private List<CellCoordinate> roadLocations;

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
   public World(double width, double height, int numRows, int numCols, Random randGen)
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

      if (randGen == null)
      {
         throw new NullPointerException("Random generator cannot be null.");
      }

      this.width = width;
      this.height = height;
      this.numRows = numRows;
      this.numCols = numCols;
      this.randGen = randGen;

      distPerRow = height / (numRows * 1.0);
      distPerCol = width / (numCols * 1.0);

      havens = new HashSet<CellCoordinate>();
      roadLocations = new ArrayList<CellCoordinate>();

      generateRoadNetwork();
      generateHavens();
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
    * Get the location of all safe havens in the world.
    * 
    * @return The location of each haven.
    */
   public Set<CellCoordinate> getHavenLocations()
   {
      return havens;
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

   /**
    * Randomly selects locations along the roads to place havens.
    */
   private void generateHavens()
   {
      // This percentage of grid cells will contain safe havens for targets
      final double percentHavenCells = 0.05;
      int numHavens = (int) (roadLocations.size() * percentHavenCells);
      numHavens = Math.max(numHavens, 3);//Require at least 3 havens

      Logger logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      logger.debug("Generating {} safe havens.", numHavens);

      // Generate the haven locations on the roads
      for (int i = 0; i < numHavens; ++i)
      {
         int index = randGen.nextInt(roadLocations.size());
         CellCoordinate havenCell = roadLocations.get(index);
         // In case we randomly generate two havens at the same location, move
         // the second one
         while (havens.contains(havenCell))
         {
            index = randGen.nextInt(roadLocations.size());
            havenCell = roadLocations.get(index);
         }
         havens.add(havenCell);
      }
   }

   /**
    * Generates road connections between the road seeds.
    * 
    * @param roadSeeds
    *           Create connections between these seeds.
    * @return A set of road connections between the seeds.
    */
   private Set<RoadGroup> generateInterRoadSeedConnections(List<CellCoordinate> roadSeeds)
   {
      /*
       * This is a naive algorithm that just connects each seed to the nearest N
       * seeds. It is possible for multiple small clusters of seeds to exist
       * such that two separate road graphs are created instead of a single
       * graph.
       */

      // The number of destinations to store per origin in the road group
      final int NUM_DESTINATIONS = 3;
      Set<RoadGroup> seedConnections = new HashSet<RoadGroup>();

      // This is inefficient because it computes the distances to between nodes
      // twice
      // However it doesn't matter too much since this only occurs at world
      // creation
      for (CellCoordinate seed : roadSeeds)
      {
         WorldCoordinate seedWC = convertCellToWorld(seed);

         // Get the distance from the current seed to all other seeds
         List<DistanceToCoord> distances = new ArrayList<DistanceToCoord>();
         for (CellCoordinate otherSeed : roadSeeds)
         {
            if (seed == otherSeed)
            {
               // The current seed cannot be a destination to itself
               continue;
            }
            DistanceToCoord dtc = new DistanceToCoord();
            dtc.asCell = otherSeed;
            dtc.asWorld = convertCellToWorld(otherSeed);
            dtc.distance = seedWC.distanceTo(dtc.asWorld);
            distances.add(dtc);
         }

         Collections.sort(distances);

         // Get the N closest nodes to the origin node
         RoadGroup rg = new RoadGroup(seed);
         for (int i = 0; i < NUM_DESTINATIONS; ++i)
         {
            rg.addDestination(distances.get(i).asCell);
         }
         seedConnections.add(rg);
      }

      return seedConnections;
   }

   /**
    * Fills in the road locations for the edges between road seeds in the road
    * network graph.
    * 
    * @param pt1
    *           Connect this point to the other point.
    * @param pt2
    *           Connect this point to the other point.
    */
   private void connectRoadGroupVertices(CellCoordinate pt1, CellCoordinate pt2)
   {
      CellCoordinate start = new CellCoordinate(pt1);
      CellCoordinate end = new CellCoordinate(pt2);

      if (end.getColumn() < start.getColumn())
      {
         CellCoordinate temp = new CellCoordinate(start);
         start.setCoordinate(end);
         end.setCoordinate(temp);
      }

      // Bresenham algorithm to generate lines connecting road seed locations
      float deltaX = end.getColumn() - start.getColumn();
      float deltaY = end.getRow() - start.getRow();
      float error = 0;

      if ((end.getColumn() - start.getColumn()) > 0)
      {
         float deltaErr = Math.abs(deltaY / deltaX);
         int y = start.getRow();
         for (int colWalk = start.getColumn(); colWalk < end.getColumn(); ++colWalk)
         {
            roadLocations.add(new CellCoordinate(y, colWalk));
            error += deltaErr;
            while (error >= 0.5)
            {
               y += (int) (Math.signum(end.getRow() - start.getRow()));
               roadLocations.add(new CellCoordinate(y, colWalk));
               error -= 1.0;
            }
         }
      }
      else// It's a purely vertical road (line)
      {
         int lowerRow = end.getRow();
         int higherRow = start.getRow();

         if (lowerRow > higherRow)
         {
            int temp = lowerRow;
            lowerRow = higherRow;
            higherRow = temp;
         }

         for (int rowWalk = lowerRow; rowWalk <= higherRow; ++rowWalk)
         {
            roadLocations.add(new CellCoordinate(rowWalk, start.getColumn()));
         }
      }
   }

   /**
    * Checks if the new cell location satisfies all the rules for new road seed
    * generation.
    * 
    * @param existingCells
    *           All pre-existing road seed coordinates.
    * @param newCell
    *           The potential new seed location to validate.
    * @return True if the new location is a valid location, false otherwise.
    */
   private boolean isValidRoadSeedLocation(List<CellCoordinate> existingCells, CellCoordinate newCell)
   {
      boolean valid = true;

      // Prevents the seeds from clustering together
      double interSeedDistBuffer = Math.min(width, height) * 0.2;

      if (existingCells.contains(newCell))
      {
         // Cannot put two seeds on top of each other
         valid = false;
      }

      WorldCoordinate newCellWC = convertCellToWorld(newCell);
      WorldCoordinate otherSeedWC = new WorldCoordinate();
      for (CellCoordinate otherSeed : existingCells)
      {
         convertCellToWorld(otherSeed, otherSeedWC);
         if (newCellWC.distanceTo(otherSeedWC) < interSeedDistBuffer)
         {
            valid = false;
            break;
         }
      }

      return valid;
   }

   /**
    * Randomly/procedurally generate a network of roads for the world.
    */
   private void generateRoadNetwork()
   {
      // This percentage of grid cells will contain road seed locations
      final double percentRoadCells = 0.001;
      int numSeeds = (int) (numRows * numCols * percentRoadCells);
      numSeeds = Math.max(numSeeds, 4);

      Logger logger = LoggerFactory.getLogger(LoggerIDs.SIM_MODEL);
      logger.debug("Generating road network with {} seeds.", numSeeds);

      List<CellCoordinate> roadSeeds = new ArrayList<CellCoordinate>();
      // Generate seed locations
      for (int i = 0; i < numSeeds; ++i)
      {
         int row = randGen.nextInt(numRows);
         int col = randGen.nextInt(numCols);

         CellCoordinate roadCell = new CellCoordinate(row, col);
         while (!isValidRoadSeedLocation(roadSeeds, roadCell))
         {
            // Regenerate a new location until we get a valid one
            row = randGen.nextInt(numRows);
            col = randGen.nextInt(numCols);
            roadCell.setCoordinate(row, col);
         }
         logger.debug("Road seed {} at {}.", i, roadCell);
         roadSeeds.add(roadCell);
      }

      // Determine which seeds to connect together
      for (RoadGroup rg : generateInterRoadSeedConnections(roadSeeds))
      {
         // Generate the road tiles along the seed graph edges
         for (CellCoordinate destination : rg.getDestinations())
         {
            connectRoadGroupVertices(rg.origin, destination);
         }
      }
   }

   private static class DistanceToCoord implements Comparable<DistanceToCoord>
   {
      public CellCoordinate asCell;
      public double distance;
      public WorldCoordinate asWorld;

      @Override
      public int compareTo(DistanceToCoord o)
      {
         if (distance < o.distance)
            return -1;
         else if (distance > o.distance)
            return 1;
         else
            return 0;
      }
   }
}
