package thesis.core.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import thesis.core.common.CellCoordinate;
import thesis.core.common.HavenRouting;
import thesis.core.common.RoadNetwork;
import thesis.core.common.WorldPose;
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
      final int snsr1Type = 0;
      final int snsr2Type = 1;
      final int tgt1Type = 0;
      final int tgt2Type = 1;

      final int numRows = 2;
      final int numCols = 2;
      final int numTgtTypes = 2;
      final int numSnsrTypes = 2;
      final int numWpnTypes = 2;
      final Random randGen = new Random();
      randGen.setSeed(424242);

      //Don't need roads in this test but we need the object
      RoadNetwork emptyRoadNet = new RoadNetwork();
      emptyRoadNet.reset(numRows, numCols);

      //Don't need havens in this test but we need the object
      List<CellCoordinate> havens = new ArrayList<CellCoordinate>();

      //-------------Initialize world sim----------------------
      WorldBelief wb = new WorldBelief(numRows, numCols, numTgtTypes);
      PayloadProbs pyldProb = new PayloadProbs(numSnsrTypes, numWpnTypes, numTgtTypes);
      TargetTypeConfigs tgtTypeCfgs = new TargetTypeConfigs(numTgtTypes);
      TargetEntitiesCfg tgtEntCfgs = new TargetEntitiesCfg();
      WorldGIS worldGIS = new WorldGIS(100, 100, numRows, numCols);
      HavenRouting havenRouting = new HavenRouting(emptyRoadNet, worldGIS, havens, randGen);
      TargetMgr tgtMgr = new TargetMgr();

      //---------------Configure world sim---------------------
      tgtTypeCfgs.setTargetData(tgt1Type, -1f, 0);
      tgtTypeCfgs.setTargetData(tgt2Type, -1f, 45);
      tgtEntCfgs.reset(2);
      tgtEntCfgs.setTargetData(0, tgt1Type, new WorldPose());
      tgtEntCfgs.setTargetData(1, tgt2Type, new WorldPose());

      pyldProb.setSensorDetectProb(snsr1Type, tgt1Type, 0.5f);
      pyldProb.setSensorDetectProb(snsr1Type, tgt2Type, 0.5f);
      pyldProb.setSensorDetectProb(snsr2Type, tgt1Type, 0.5f);
      pyldProb.setSensorDetectProb(snsr2Type, tgt2Type, 0.5f);
      pyldProb.setSensorConfirmProb(snsr1Type, tgt1Type, 0.5f);
      pyldProb.setSensorConfirmProb(snsr1Type, tgt2Type, 0.5f);
      pyldProb.setSensorConfirmProb(snsr2Type, tgt1Type, 0.5f);
      pyldProb.setSensorConfirmProb(snsr2Type, tgt2Type, 0.5f);

      tgtMgr.reset(tgtTypeCfgs, tgtEntCfgs, havenRouting, worldGIS);


      //----------------Perform tests---------------------
   }
}
