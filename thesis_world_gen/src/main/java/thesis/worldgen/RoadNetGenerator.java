package thesis.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.common.CellCoordinate;
import thesis.core.common.RoadNetwork;
import thesis.core.utilities.LoggerIDs;

public class RoadNetGenerator
{
   /**
    * The minimum allowed distance between two seeds is proportional to
    * the min(NUM_ROWS,NUM_COLS) * this percentage.
    */
   private static final double MIN_INTERSECTION_SPACING_PERCENT = 0.15f;

   private final int NUM_ROWS;
   private final int NUM_COLS;

   public RoadNetGenerator(int numRows, int numCols)
   {
      this.NUM_COLS = numCols;
      this.NUM_ROWS = numRows;
   }

   public void generate(Random randGen, RoadNetwork roadNet)
   {
      roadNet.reset(NUM_ROWS, NUM_COLS);

      // This percentage of grid cells will contain road seed locations
      final double percentRoadCells = 0.001;
      int numSeeds = (int) (NUM_ROWS * NUM_COLS * percentRoadCells);
      numSeeds = Math.max(numSeeds, 6);

      Logger logger = LoggerFactory.getLogger(LoggerIDs.MAIN);
      logger.debug("Generating road network with {} seeds.", numSeeds);

      List<CellCoordinate> roadSeeds = new ArrayList<CellCoordinate>();

      // Generate seed locations
      for (int i = 0; i < numSeeds; ++i)
      {
         int row = randGen.nextInt(NUM_ROWS);
         int col = randGen.nextInt(NUM_COLS);

         CellCoordinate seedCoord = new CellCoordinate(row, col);
         while (!isValidRoadSeedLocation(roadSeeds, seedCoord))
         {
            // Regenerate a new location until we get a valid one
            row = randGen.nextInt(NUM_ROWS);
            col = randGen.nextInt(NUM_COLS);
            seedCoord.setCoordinate(row, col);
         }

         logger.debug("Road seed {} at {}.", i, seedCoord);
         roadSeeds.add(seedCoord);
         roadNet.setTraversable(seedCoord, true);
      }

      for (int i = 0; i < (roadSeeds.size() - 1); ++i)
      {
         computeRoadIntersection(roadSeeds.get(i), roadSeeds.get(i + 1), i % 2 == 0, roadNet);
      }

      computeRoadIntersection(roadSeeds.get(roadSeeds.size()-1), roadSeeds.get(0), roadSeeds.size() % 2 == 0, roadNet);

      // TODO Add more random connections between random seeds
   }

   private void computeRoadIntersection(CellCoordinate start, CellCoordinate end, boolean isVertSplit,
         RoadNetwork roadNet)
   {
      if (isVertSplit)
      {
         // Iterate rows from start to end
         if (end.getRow() < start.getRow())
         {
            CellCoordinate temp = start;
            start = end;
            end = temp;
         }

         int offset = end.getRow() - start.getRow();
         for (int i = 1; i < offset; ++i)
         {
            roadNet.setTraversable(start.getRow() + i, start.getColumn(), true);
         }

         // Iterate columns from start to end
         if (end.getColumn() < start.getColumn())
         {
            CellCoordinate temp = start;
            start = end;
            end = temp;
         }

         offset = end.getColumn() - start.getColumn();
         for (int i = 1; i < offset; ++i)
         {
            roadNet.setTraversable(end.getRow(), start.getColumn() + i, true);
         }

      }
      else
      {
         // Iterate columns from start to end
         if (end.getColumn() < start.getColumn())
         {
            CellCoordinate temp = start;
            start = end;
            end = temp;
         }

         int offset = end.getColumn() - start.getColumn();
         for (int i = 1; i < offset; ++i)
         {
            roadNet.setTraversable(end.getRow(), start.getColumn() + i, true);
         }

         // Iterate rows from start to end
         if (end.getRow() < start.getRow())
         {
            CellCoordinate temp = start;
            start = end;
            end = temp;
         }

         offset = end.getRow() - start.getRow();
         for (int i = 1; i < offset; ++i)
         {
            roadNet.setTraversable(start.getRow() + i, start.getColumn(), true);
         }
      }
   }

   /**
    * Checks if the new cell location satisfies all the rules for new road seed
    * generation.
    *
    * @param existingLocations
    *           All pre-existing road seed coordinates.
    * @param newLocation
    *           The potential new seed location to validate.
    * @return True if the new location is a valid location, false otherwise.
    */
   private boolean isValidRoadSeedLocation(List<CellCoordinate> existingLocations, CellCoordinate newLocation)
   {
      boolean valid = true;

      // Prevents the seeds from clustering together
      double interSeedDistBuffer = Math.min(NUM_ROWS, NUM_COLS) * MIN_INTERSECTION_SPACING_PERCENT;

      if (existingLocations.contains(newLocation))
      {
         // Cannot put two seeds on top of each other
         valid = false;
      }

      for (CellCoordinate otherSeed : existingLocations)
      {
         if (newLocation.distanceTo(otherSeed) < interSeedDistBuffer)
         {
            valid = false;
            break;
         }
      }

      return valid;
   }
}
