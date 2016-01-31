package thesis.core.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import thesis.core.EntityTypeCfgs;
import thesis.core.common.CellCoordinate;
import thesis.core.common.HavenRouting;
import thesis.core.common.SimTime;
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

      TargetTypeConfigs tgtTypeCfgs = entCfgs.getTgtTypeCfgs();
      tgtTypeCfgs.reset(numTgtTypes);
      tgtTypeCfgs.setTargetData(0, -1f, 45);
      tgtTypeCfgs.setTargetData(1, -1f, 115);

      SensorProbs pyldProb = entCfgs.getSnsrProbs();
      pyldProb.reset(numSnsrTypes, numTgtTypes);
      pyldProb.setSensorDetectProb(0, 0, 0.3f);
      pyldProb.setSensorDetectProb(0, 1, 0.7f);
      pyldProb.setSensorDetectProb(0, 2, 0.6f);

      pyldProb.setSensorConfirmProb(0, 0, 0.4f);
      pyldProb.setSensorConfirmProb(0, 1, 0.7f);
      pyldProb.setSensorConfirmProb(0, 2, 0.6f);

      pyldProb.setSensorHeadingCoeff(0, 0, 0.5f);
      pyldProb.setSensorHeadingCoeff(0, 1, 0.5f);
      pyldProb.setSensorHeadingCoeff(0, 2, 0.5f);

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
      CellCoordinate cell = new CellCoordinate(0, 0);

      WorldPose pose = new WorldPose();
      pose.setHeading(22);

      worldGIS.convertCellToWorld(cell, pose.getCoordinate());

      List<TargetStartCfg> startCfgs = new ArrayList<TargetStartCfg>();

      TargetStartCfg startCfg = new TargetStartCfg();
      startCfg.getLocation().setCoordinate(pose.getCoordinate());
      startCfg.setOrientation(pose.getHeading());
      startCfg.setTargetType(1);
      startCfgs.add(startCfg);

      return startCfgs;
   }

   @Test
   public void scanTest()
   {
      final int numTgtTypes = 3;
      final int numRows = 1;
      final int numCols = 1;

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
      SensorScan testMe = new SensorScan(entCfgs.getSnsrProbs(), tgtMgr, randGen);

      int numSimulations = 400;

      for (int i = 0; i < numSimulations; ++i)
      {
         //System.out.println(String.format("--------Simulation Frame %d---------", i));
         testMe.simulateScan(0, 115, wb, allCells, i * SimTime.SIM_STEP_RATE_MS);

         for (int cellIdx = 0; cellIdx < numCols; ++cellIdx)
         {
            CellBelief cell = wb.getCellBelief(0, cellIdx);
            System.out.print(Integer.toString(i) + ",");
            for (int tgtTypeIdx = 0; tgtTypeIdx < numTgtTypes; ++tgtTypeIdx)
            {
               // System.out.println(String.format("Cell %d Tgt %d - Prob:%.2f,
               // Hdg:%.2f", cellIdx, tgtTypeIdx,
               // cell.getTargetProb(tgtTypeIdx),
               // cell.getTargetHeading(tgtTypeIdx)));
               System.out.print(
                     String.format("%.2f,%.2f,", cell.getTargetProb(tgtTypeIdx), cell.getTargetHeading(tgtTypeIdx)));
            }
            System.out.println("");
         }

      }
   }
}
