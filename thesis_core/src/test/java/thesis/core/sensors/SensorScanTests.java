package thesis.core.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import thesis.core.common.CellCoordinate;
import thesis.core.common.HavenRouting;
import thesis.core.common.RoadNetwork;
import thesis.core.experimental.PayloadProbs;
import thesis.core.experimental.WorldBelief;
import thesis.core.serialization.TargetEntitiesCfg;
import thesis.core.targets.TargetMgr;
import thesis.core.targets.TargetTypeConfigs;
import thesis.core.world.WorldGIS;

public class SensorScanTests
{

   @Test
   public void scanTest()
   {
      int numRows = 2;
      int numCols = 2;
      int numTgtTypes = 2;
      int numSnsrTypes = 2;
      int numWpnTypes = 2;
      Random randGen = new Random();

      WorldBelief wb = new WorldBelief(numRows, numCols, numTgtTypes);
      PayloadProbs pyldProb = new PayloadProbs(numSnsrTypes, numWpnTypes, numTgtTypes);
      TargetTypeConfigs tgtTypeCfgs = new TargetTypeConfigs(numTgtTypes);
      TargetEntitiesCfg tgtEntCfgs = new TargetEntitiesCfg();

      //Don't need roads in this test
      RoadNetwork emptyRoadNet = new RoadNetwork();
      emptyRoadNet.reset(numRows, numCols);

      //Don't need havens in this test
      List<CellCoordinate> havens = new ArrayList<CellCoordinate>();

      WorldGIS worldGIS = new WorldGIS(100, 100, numRows, numCols);
      HavenRouting havenRouting = new HavenRouting(emptyRoadNet, worldGIS, havens, randGen);

      TargetMgr tgtMgr = new TargetMgr();
      tgtMgr.reset(tgtTypeCfgs, tgtEntCfgs, havenRouting, worldGIS);
   }
}
