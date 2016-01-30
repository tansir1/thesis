package thesis.core.sensors;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import thesis.core.EntityTypeCfgs;
import thesis.core.common.CellCoordinate;
import thesis.core.common.HavenRouting;
import thesis.core.common.RoadNetwork;
import thesis.core.common.WorldPose;
import thesis.core.experimental.WorldBelief;
import thesis.core.serialization.DBConnections;
import thesis.core.serialization.TargetEntitiesCfg;
import thesis.core.targets.TargetMgr;
import thesis.core.targets.TargetTypeConfigs;
import thesis.core.world.WorldGIS;

public class SensorScanTests
{

   private EntityTypeCfgs initEntityCfgs(final int numTgtTypes)
   {
      final int numSnsrTypes = 2;


      EntityTypeCfgs entCfgs = new EntityTypeCfgs();

      SensorTypeConfigs snsrTypeCfgs = entCfgs.getSnsrTypeCfgs();
      snsrTypeCfgs.reset(numSnsrTypes);
      snsrTypeCfgs.setSensorData(0, 45, 0, 1000, 10);
      snsrTypeCfgs.setSensorData(1, 45, 0, 1000, 10);

      TargetTypeConfigs tgtTypeCfgs = entCfgs.getTgtTypeCfgs();
      tgtTypeCfgs.reset(numTgtTypes);
      tgtTypeCfgs.setTargetData(0, -1f, 45);
      tgtTypeCfgs.setTargetData(1, -1f, 115);

      SensorProbs pyldProb = entCfgs.getSnsrProbs();
      pyldProb.reset(numSnsrTypes, numTgtTypes);
      pyldProb.setSensorDetectProb(0, 0, 0.6f);
      pyldProb.setSensorDetectProb(0, 1, 0.3f);
      pyldProb.setSensorDetectProb(1, 0, 0.2f);
      pyldProb.setSensorDetectProb(1, 1, 0.7f);

      pyldProb.setSensorConfirmProb(0, 0, 0.7f);
      pyldProb.setSensorConfirmProb(0, 1, 0.4f);
      pyldProb.setSensorConfirmProb(1, 0, 0.1f);
      pyldProb.setSensorConfirmProb(1, 1, 0.8f);

      return entCfgs;
   }

   private void initTargets(TargetEntitiesCfg tgtEntCfgs, WorldGIS worldGIS)
   {
      /*
       * No | 0
       * ------
       *  1 | No
       */
      CellCoordinate cell1 = new CellCoordinate(0,1);
      CellCoordinate cell2 = new CellCoordinate(1,0);

      WorldPose pose1 = new WorldPose();
      WorldPose pose2 = new WorldPose();

      worldGIS.convertCellToWorld(cell1, pose1.getCoordinate());
      worldGIS.convertCellToWorld(cell2, pose2.getCoordinate());

      tgtEntCfgs.reset(2);
      tgtEntCfgs.setTargetData(0, 0, pose1);
      tgtEntCfgs.setTargetData(1, 1, pose2);
   }

   @Test
   public void scanTest()
   {
      final int numTgtTypes = 2;
      final int numRows = 2;
      final int numCols = 2;

      DBConnections dbConns = new DBConnections();
      assertTrue("Failed to open configuration db.", dbConns.openConfigDB());

      EntityTypeCfgs entCfgs = initEntityCfgs(numTgtTypes);

      final Random randGen = new Random();
      randGen.setSeed(424242);

      //Don't need roads in this test but we need the object
      RoadNetwork emptyRoadNet = new RoadNetwork();
      emptyRoadNet.reset(numRows, numCols);

      //Don't need havens in this test but we need the object
      List<CellCoordinate> havens = new ArrayList<CellCoordinate>();

      //-------------Initialize world sim----------------------
      WorldBelief wb = new WorldBelief(numRows, numCols, numTgtTypes);
      WorldGIS worldGIS = new WorldGIS();
      worldGIS.reset(100, 100, numRows, numCols);
      HavenRouting havenRouting = new HavenRouting(emptyRoadNet, worldGIS, havens, randGen);
      TargetEntitiesCfg tgtEntCfgs = new TargetEntitiesCfg();
      initTargets(tgtEntCfgs, worldGIS);
      TargetMgr tgtMgr = new TargetMgr();
      tgtMgr.reset(entCfgs.getTgtTypeCfgs(), tgtEntCfgs, havenRouting, worldGIS);

      //----------------Perform tests---------------------
      List<CellCoordinate> allCells = new ArrayList<CellCoordinate>();
      allCells.add(new CellCoordinate(0,0));
      allCells.add(new CellCoordinate(0,1));
      allCells.add(new CellCoordinate(1,0));
      allCells.add(new CellCoordinate(1,1));
      SensorScan testMe = new SensorScan(entCfgs.getSnsrProbs(), tgtMgr, randGen);
      testMe.simulateScan(0, 0, wb, allCells, 0L);
   }
}
