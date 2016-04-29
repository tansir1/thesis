package thesis.core.uav.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thesis.core.belief.CellBelief;
import thesis.core.belief.WorldBelief;
import thesis.core.common.CellCoordinate;
import thesis.core.sensors.SensorGroup;
import thesis.core.uav.Pathing;
import thesis.core.utilities.LoggerIDs;
import thesis.core.world.WorldGIS;

public class SearchTask
{
   private static final Logger logger = LoggerFactory.getLogger(LoggerIDs.UAV_LOGIC);

   public enum Strategy
   {
      MostUncertain, RandomTopThird, Forage
   }

   /**
    * Global flag to configure the strategy used for selecting new search
    * destinations.
    */
   public static Strategy strategy = Strategy.Forage;

   /**
    * When the uncertainty of the searched cell falls below this value select a
    * new cell to search.
    */
   protected static final double UNCERTAINTY_THRESHOLD = 0.1;

   private CellCoordinate searchDest;
   private WorldGIS gis;
   private int hostUavId;
   private Random rand;

   public SearchTask(int hostUavId, WorldGIS gis, Random randGen)
   {
      this.hostUavId = hostUavId;
      this.gis = gis;
      this.rand = randGen;
   }

   public void reset(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      selectNewSearchDestination(curBelief, pathing, snsrGrp);
   }

   public void stepSimulation(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      if (searchDest == null || curBelief.getCellBelief(searchDest).getUncertainty() < UNCERTAINTY_THRESHOLD)
      {
         selectNewSearchDestination(curBelief, pathing, snsrGrp);
      }
   }

   private void selectNewSearchDestination(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      CellCoordinate oldDest = searchDest;

      switch (strategy)
      {
      case MostUncertain:
         selectMostUncertain(curBelief, pathing, snsrGrp);
         break;
      case RandomTopThird:
         selectRandomTopThird(curBelief, pathing, snsrGrp);
         break;
      case Forage:
         forage(curBelief);
         break;
      }

      logger.trace("UAV {} changed search destination from {} to {}.", hostUavId, oldDest, searchDest);

      pathing.computePathTo(gis.convertCellToWorld(searchDest));
      snsrGrp.stareAtAll(gis.convertCellToWorld(searchDest));
   }

   private void forage(WorldBelief curBelief)
   {
      final double PURE_RANDOM_WEIGHT = 0.33;
      final int numRows = curBelief.getNumRows();
      final int numCols = curBelief.getNumCols();

      int destCol = -1;
      int destRow = -1;

      if(rand.nextDouble() < PURE_RANDOM_WEIGHT)
      {
         //Pure random
         destCol = rand.nextInt(numCols);
         destRow = rand.nextInt(numRows);
      }
      else
      {
         //The size of the kernel square in cells
         final int kernelDivisor = 5;//kernel is a 5x5 grid of cells

         int rowsPerKernel = numRows / kernelDivisor;
         int colsPerKernel = numCols / kernelDivisor;

         int maxKernRow = 0;
         int maxKernCol = 0;
         double maxUncert = 0;

         //Iterate across all kernels in the world
         for (int worldRow = 0; worldRow < numRows; worldRow += rowsPerKernel)
         {
            for (int worldCol = 0; worldCol < numCols; worldCol += colsPerKernel)
            {
               double avgUncert = computeForageKernelUncert(curBelief, worldCol, worldRow, colsPerKernel,
                     rowsPerKernel);

               //Store the most uncertain kernel
               if(avgUncert > maxUncert)
               {
                  maxUncert = avgUncert;
                  maxKernCol = worldCol;
                  maxKernRow = worldRow;
               }
            }
         }

         //Pick a random cell within the kernel
         destCol = rand.nextInt(colsPerKernel) + maxKernCol;
         destRow = rand.nextInt(rowsPerKernel) + maxKernRow;
      }

      searchDest = curBelief.getCellBelief(destRow, destCol).getCoordinate();
   }

   private double computeForageKernelUncert(WorldBelief curBelief, int worldColStart, int worldRowStart,
         int kernWidth, int kernHeight)
   {
      final int worldWidth = curBelief.getNumCols();
      final int worldHeight = curBelief.getNumRows();

      SummaryStatistics stats = new SummaryStatistics();

      for (int row = 0; row < kernHeight && (row + worldRowStart) < worldWidth; ++row)
      {
         for (int col = 0; col < kernWidth && (col + worldColStart) < worldHeight; ++col)
         {
            stats.addValue(curBelief.getCellBelief(row + worldRowStart, col + worldColStart).getUncertainty());
         }
      }

      return stats.getMean();
   }

   private void selectRandomTopThird(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      final int numRows = curBelief.getNumRows();
      final int numCols = curBelief.getNumCols();
      final int numCells = numRows * numCols;

      final List<CellBelief> allBeliefs = new ArrayList<CellBelief>(numCells);

      for (int i = 0; i < numRows; ++i)
      {
         for (int j = 0; j < numCols; ++j)
         {
            allBeliefs.add(curBelief.getCellBelief(i, j));
         }
      }

      Collections.sort(allBeliefs, new CellBeliefComparator());

      int randIndx = -1;
      if (numCells > 2)
      {
         int oneThirdIndx = numCells / 3;
         if (Math.abs(allBeliefs.get(0).getUncertainty() - allBeliefs.get(oneThirdIndx).getUncertainty()) < 0.001)
         {
            // If the entire world is equally uncertain (like at simulation
            // startup) select a random cell in the world to go search.
            randIndx = rand.nextInt(numCells);
         }
         else
         {
            randIndx = rand.nextInt(numCells / 3);
         }
      }
      else
      {
         randIndx = rand.nextInt(numCells);
      }

      searchDest = allBeliefs.get(randIndx).getCoordinate();
   }

   private void selectMostUncertain(WorldBelief curBelief, Pathing pathing, SensorGroup snsrGrp)
   {
      final int numRows = curBelief.getNumRows();
      final int numCols = curBelief.getNumCols();

      CellBelief maxUncertCB = null;

      // This just finds the most uncertain cell. It would be better by
      // finding clusters of highly uncertain cells instead of the most
      // uncertain cell or searching the local area and jumping off to a new
      // area as in Levy Flights or Levy Walks.

      for (int i = 0; i < numRows; ++i)
      {
         for (int j = 0; j < numCols; ++j)
         {
            CellBelief itrCB = curBelief.getCellBelief(i, j);

            if (maxUncertCB == null)
            {
               maxUncertCB = itrCB;
            }
            else if (itrCB.getUncertainty() > maxUncertCB.getUncertainty())
            {
               maxUncertCB = itrCB;
            }
         }
      }

      searchDest = maxUncertCB.getCoordinate();
   }

   private static class CellBeliefComparator implements Comparator<CellBelief>
   {
      public CellBeliefComparator()
      {

      }

      @Override
      public int compare(CellBelief o1, CellBelief o2)
      {
         if (o1.getUncertainty() < o2.getUncertainty())
         {
            return -1;
         }
         else if (o1.getUncertainty() > o2.getUncertainty())
         {
            return 1;
         }
         else
         {
            return 0;
         }
      }

   }
}
