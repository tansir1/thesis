package thesis.core.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import thesis.core.EntityTypeCfgs;
import thesis.core.common.CellCoordinate;
import thesis.core.common.HavenRouting;
import thesis.core.common.WorldPose;
import thesis.core.experimental.CellBelief;
import thesis.core.experimental.WorldBelief;
import thesis.core.serialization.world.TargetStartCfg;
import thesis.core.targets.TargetMgr;
import thesis.core.targets.TargetTypeConfigs;
import thesis.core.world.World;
import thesis.core.world.WorldGIS;

public class SensorScanTests
{

   private EntityTypeCfgs initEntityCfgs(final int numTgtTypes)
   {
      final int numSnsrTypes = 1;

      EntityTypeCfgs entCfgs = new EntityTypeCfgs();
      /*
       * SensorTypeConfigs snsrTypeCfgs = entCfgs.getSnsrTypeCfgs();
       * snsrTypeCfgs.reset(numSnsrTypes); snsrTypeCfgs.setSensorData(0, 45, 0,
       * 1000, 10);
       */

      TargetTypeConfigs tgtTypeCfgs = entCfgs.getTgtTypeCfgs();
      tgtTypeCfgs.reset(numTgtTypes);
      tgtTypeCfgs.setTargetData(0, -1f, 45);
      tgtTypeCfgs.setTargetData(1, -1f, 115);

      SensorProbs pyldProb = entCfgs.getSnsrProbs();
      pyldProb.reset(numSnsrTypes, numTgtTypes);
      pyldProb.setSensorDetectProb(0, 0, 0.6f);
      pyldProb.setSensorDetectProb(0, 1, 0.3f);
      pyldProb.setSensorDetectProb(0, 2, 0.6f);

      pyldProb.setSensorConfirmProb(0, 0, 0.7f);
      pyldProb.setSensorConfirmProb(0, 1, 0.4f);
      pyldProb.setSensorConfirmProb(0, 2, 0.7f);

      pyldProb.setSensorMisclassifyProb(0, 0, 1, 0.2f);
      pyldProb.setSensorMisclassifyProb(0, 0, 2, 0.2f);
      pyldProb.setSensorMisclassifyProb(0, 1, 0, 0.2f);
      pyldProb.setSensorMisclassifyProb(0, 1, 2, 0.2f);
      pyldProb.setSensorMisclassifyProb(0, 2, 0, 0.2f);
      pyldProb.setSensorMisclassifyProb(0, 2, 1, 0.2f);

      return entCfgs;
   }

   private List<TargetStartCfg> initTargets(WorldGIS worldGIS)
   {
      /*
       * Tgt1 | Tgt2 |
       */
      CellCoordinate cell1 = new CellCoordinate(0, 0);
      CellCoordinate cell2 = new CellCoordinate(0, 1);

      WorldPose pose1 = new WorldPose();
      WorldPose pose2 = new WorldPose();

      worldGIS.convertCellToWorld(cell1, pose1.getCoordinate());
      worldGIS.convertCellToWorld(cell2, pose2.getCoordinate());

      List<TargetStartCfg> startCfgs = new ArrayList<TargetStartCfg>();

      TargetStartCfg startCfg = new TargetStartCfg();
      startCfg.getLocation().setCoordinate(pose1.getCoordinate());
      startCfg.setOrientation(pose1.getHeading());
      startCfg.setTargetType(1);
      startCfgs.add(startCfg);

      startCfg = new TargetStartCfg();
      startCfg.getLocation().setCoordinate(pose2.getCoordinate());
      startCfg.setOrientation(pose2.getHeading());
      startCfg.setTargetType(2);
      startCfgs.add(startCfg);

      return startCfgs;
   }

   @Test
   public void scanTest()
   {
      final int numTgtTypes = 3;
      final int numRows = 1;
      final int numCols = 2;

      EntityTypeCfgs entCfgs = initEntityCfgs(numTgtTypes);

      final Random randGen = new Random();
      randGen.setSeed(424242);

      World world = new World();
      world.getWorldGIS().reset(100, 100, numRows, numCols);
      world.getRoadNetwork().reset(1, 1);
      world.getHavens().reset(1);

      // -------------Initialize world sim----------------------
      WorldBelief wb = new WorldBelief(numRows, numCols, numTgtTypes);
      HavenRouting havenRouting = new HavenRouting(world, randGen);
      TargetMgr tgtMgr = new TargetMgr();
      tgtMgr.reset(entCfgs.getTgtTypeCfgs(), initTargets(world.getWorldGIS()), havenRouting, world.getWorldGIS());

      // ----------------Perform tests---------------------
      List<CellCoordinate> allCells = new ArrayList<CellCoordinate>();
      allCells.add(new CellCoordinate(0, 0));
      allCells.add(new CellCoordinate(0, 1));
      SensorScan testMe = new SensorScan(entCfgs.getSnsrProbs(), tgtMgr, randGen);

      int numSimulations = 4;

      for (int i = 0; i < numSimulations; ++i)
      {
         System.out.println(String.format("--------Pass %d---------", i));
         testMe.simulateScan(0, 45, wb, allCells, 0L);

         for (int cellIdx = 0; cellIdx < numCols; ++cellIdx)
         {
            CellBelief cell = wb.getCellBelief(0, cellIdx);

            for (int tgtTypeIdx = 0; tgtTypeIdx < numTgtTypes; ++tgtTypeIdx)
            {
               System.out.println(String.format("Cell %d Tgt %d - Prob:%.2f, Hdg:%.2f", cellIdx, tgtTypeIdx,
                     cell.getTargetProb(tgtTypeIdx), cell.getTargetHeading(tgtTypeIdx)));
            }
         }

      }
   }
}
