package thesis.core.uav.logic;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import thesis.core.belief.WorldBelief;
import thesis.core.common.CellCoordinate;
import thesis.core.sensors.SensorGroup;
import thesis.core.sensors.SensorProbs;
import thesis.core.sensors.SensorScanLogic;
import thesis.core.targets.TargetMgr;
import thesis.core.uav.Pathing;
import thesis.core.uav.UAVTypeConfigs;
import thesis.core.world.WorldGIS;

public class SearchTaskTests
{

   @Test
   public void searchChangeTest()
   {
      WorldGIS gis = new WorldGIS();
      gis.reset(1000, 1000, 2, 2);

      UAVTypeConfigs typeCfgs = new UAVTypeConfigs();
      typeCfgs.reset(1);
      typeCfgs.setUAVData(0, 10, 50);

      Pathing pathing = new Pathing(0, 0, typeCfgs);
      WorldBelief worldBlf = new WorldBelief(2, 2, 1, 0d);
      // All cells are known to be empty except one that is 50/50 chance.
      worldBlf.getCellBelief(0, 0).updateEmptyBelief(0, 0);
      worldBlf.getCellBelief(0, 1).updateEmptyBelief(0, 0);
      worldBlf.getCellBelief(1, 0).updateEmptyBelief(0, 0);
      worldBlf.getCellBelief(1, 0).updateEmptyBelief(0, 0.5);
      CellCoordinate expectedDestCoord = new CellCoordinate(1,0);

      TargetMgr tgtMgr = new TargetMgr();
      SensorProbs pyldProbs = new SensorProbs();
      SensorScanLogic snsrScanLogic = new SensorScanLogic(pyldProbs, tgtMgr, new Random());
      SensorGroup snsrGrp = new SensorGroup(snsrScanLogic, gis);

      SearchTask.strategy = SearchTask.Strategy.MostUncertain;
      SearchTask testMe = new SearchTask(0, gis, new Random());
      testMe.stepSimulation(worldBlf, pathing, snsrGrp);

      CellCoordinate actualDestCoord = gis.convertWorldToCell(pathing.getFlightPath().getEndPose().getCoordinate());
      assertEquals("Search selected incorrect destination to start", expectedDestCoord, actualDestCoord);

      //Change nothing, assert that the destination did not change
      testMe.stepSimulation(worldBlf, pathing, snsrGrp);
      assertEquals("Search changed destination when it should not have.", expectedDestCoord, actualDestCoord);

      //Zero out the uncertainty of current destination cell and raise it elsewhere.
      worldBlf.getCellBelief(expectedDestCoord).updateEmptyBelief(1, 0);
      expectedDestCoord.setCoordinate(0, 0);
      worldBlf.getCellBelief(expectedDestCoord).updateEmptyBelief(1, 0.5);

      //Test that search task switches to new uncertain cell
      testMe.stepSimulation(worldBlf, pathing, snsrGrp);
      actualDestCoord = gis.convertWorldToCell(pathing.getFlightPath().getEndPose().getCoordinate());
      assertEquals("Search did not switch upon uncertainty threshold reached.", expectedDestCoord, actualDestCoord);
   }
}
