package thesis.core.uav.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
      MostUncertain,
      RandomTopThird
   }

   /**
    * Global flag to configure the strategy used for selecting new search destinations.
    */
   public static Strategy strategy = Strategy.RandomTopThird;

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
      this.gis = gis;
      this.rand = randGen;
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

      switch(strategy)
      {
      case MostUncertain:
         selectMostUncertain(curBelief, pathing, snsrGrp);
         break;
      case RandomTopThird:
         selectRandomTopThird(curBelief, pathing, snsrGrp);
         break;
      }

      logger.trace("UAV {} changed search destination from {} to {}.", hostUavId, oldDest, searchDest);

      pathing.computePathTo(gis.convertCellToWorld(searchDest));
      snsrGrp.stareAtAll(gis.convertCellToWorld(searchDest));
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

      Collections.sort(allBeliefs, new CellBeliefComparator(rand));

      int randIndx = -1;
      if(numCells > 2)
      {
         randIndx = rand.nextInt(numCells / 3);
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
      //area as in Levy Flights or Levy Walks.

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
      private Random rand;

      public CellBeliefComparator(Random rand)
      {
         this.rand = rand;
      }

      @Override
      public int compare(CellBelief o1, CellBelief o2)
      {
         //If they're extremely close then randomly select one as higher than the other.
         //This helps diversify the search area, particularly on application startup
         if(Math.abs(o1.getUncertainty() - o2.getUncertainty()) < 0.01)
         {
            return rand.nextBoolean() ? -1 : 1;
         }


         if(o1.getUncertainty() < o2.getUncertainty())
         {
            return -1;
         }
         else if(o1.getUncertainty() > o2.getUncertainty())
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
