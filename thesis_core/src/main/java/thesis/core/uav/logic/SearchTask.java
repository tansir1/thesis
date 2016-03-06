package thesis.core.uav.logic;

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

   /**
    * When the uncertainty of the searched cell falls below this value select a
    * new cell to search.
    */
   protected static final double UNCERTAINTY_THRESHOLD = 0.1;

   private CellCoordinate searchDest;
   private WorldGIS gis;
   private int hostUavId;

   public SearchTask(int hostUavId, WorldGIS gis)
   {
      this.gis = gis;
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
      final int numRows = curBelief.getNumRows();
      final int numCols = curBelief.getNumCols();

      CellBelief maxUncertCB = null;

      // This just finds the most uncertain cell. It could be better by
      // finding clusters of highly uncertain cells instead of the most
      // uncertain cell.

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

      CellCoordinate oldDest = searchDest;
      searchDest = maxUncertCB.getCoordinate();
      logger.trace("UAV {} changed search destination from {} to {}.", hostUavId, oldDest, searchDest);

      pathing.computePathTo(gis.convertCellToWorld(searchDest));
      snsrGrp.stareAtAll(gis.convertCellToWorld(searchDest));
   }
}
