package thesis.core.sensors;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import thesis.core.EntityTypeCfgs;
import thesis.core.belief.CellBelief;
import thesis.core.belief.TargetBelief;
import thesis.core.belief.WorldBelief;
import thesis.core.common.CellCoordinate;
import thesis.core.common.HavenRouting;
import thesis.core.common.WorldPose;
import thesis.core.serialization.world.TargetStartCfg;
import thesis.core.targets.TargetMgr;
import thesis.core.targets.TargetTypeConfigs;
import thesis.core.world.World;
import thesis.core.world.WorldGIS;

public class SensorScanTests
{

   private EntityTypeCfgs initEntityCfgs(final int numTgtTypes, boolean allowMisclass)
   {
      final int numSnsrTypes = 1;

      EntityTypeCfgs entCfgs = new EntityTypeCfgs();

      TargetTypeConfigs tgtTypeCfgs = entCfgs.getTgtTypeCfgs();
      tgtTypeCfgs.reset(numTgtTypes);
      tgtTypeCfgs.setTargetData(0, -1f, 45);
      tgtTypeCfgs.setTargetData(1, -1f, 115);
      tgtTypeCfgs.setTargetData(1, -1f, 0);

      SensorProbs pyldProb = entCfgs.getSnsrProbs();
      pyldProb.reset(numSnsrTypes, numTgtTypes);
      pyldProb.setSensorDetectTgtProb(0, 0, 0.6f);
      pyldProb.setSensorDetectTgtProb(0, 1, 0.6f);
      pyldProb.setSensorDetectTgtProb(0, 2, 0.6f);

      pyldProb.setSensorDetectEmptyProb(0, 0.7f);

      pyldProb.setSensorConfirmProb(0, 0, 0.4f);
      pyldProb.setSensorConfirmProb(0, 1, 0.7f);
      pyldProb.setSensorConfirmProb(0, 2, 0.6f);

      pyldProb.setSensorHeadingCoeff(0, 0, 0.5f);
      pyldProb.setSensorHeadingCoeff(0, 1, 0.5f);
      pyldProb.setSensorHeadingCoeff(0, 2, 0.5f);

      if(allowMisclass)
      {
         pyldProb.setSensorMisclassifyProb(0, 0, 1, 0.2f);
         pyldProb.setSensorMisclassifyProb(0, 0, 2, 0.2f);
         pyldProb.setSensorMisclassifyProb(0, 1, 0, 0.2f);
         pyldProb.setSensorMisclassifyProb(0, 1, 2, 0.2f);
         pyldProb.setSensorMisclassifyProb(0, 2, 0, 0.2f);
         pyldProb.setSensorMisclassifyProb(0, 2, 1, 0.2f);
      }
      else
      {
         pyldProb.setSensorMisclassifyProb(0, 0, 1, 0f);
         pyldProb.setSensorMisclassifyProb(0, 0, 2, 0f);
         pyldProb.setSensorMisclassifyProb(0, 1, 0, 0f);
         pyldProb.setSensorMisclassifyProb(0, 1, 2, 0f);
         pyldProb.setSensorMisclassifyProb(0, 2, 0, 0f);
         pyldProb.setSensorMisclassifyProb(0, 2, 1, 0f);
      }

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

   private void printScanResults(int scanCount, CellBelief cellBelief, TargetBelief tgtBelief, PrintWriter pw, int numTgtTypes)
   {
      //scanCount,probEmptyCell,probNotEmptyCell,tgtHdgEst,probTgtType1,probTgtTyp2,...,probTgtTypeN

      pw.print(Integer.toString(scanCount) + ",");
      pw.print(String.format("%.2f,%.2f,", cellBelief.getProbabilityEmptyCell(), cellBelief.getProbabilityNotEmptyCell()));
      pw.print(String.format("%.2f", tgtBelief.getHeadingEstimate()));

      for (int tgtTypeIdx = 0; tgtTypeIdx < numTgtTypes; ++tgtTypeIdx)
      {
         pw.print(String.format(",%.2f", tgtBelief.getTypeProbability(tgtTypeIdx)));
      }
      pw.println();
   }

   @Test
   public void singleTargetSingleCellScanTest() throws IOException
   {
      final int numTgtTypes = 3;
      final int numRows = 1;
      final int numCols = 1;

      EntityTypeCfgs entCfgs = initEntityCfgs(numTgtTypes, true);

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
      SensorScanLogic testMe = new SensorScanLogic(entCfgs.getSnsrProbs(), tgtMgr, randGen);

      int numSimulations = 4000;

      File testDataFile = new File("../utils/sensorScanTest.csv");
      PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(testDataFile)));

      testMe.simulateScan(0, 115, wb, allCells, 0);
      CellBelief cellBelief = wb.getCellBelief(0, 0);
      TargetBelief tgtBelief = cellBelief.getTargetBelief(0);

      for (int i = 1; i < numSimulations; ++i)
      {
         testMe.simulateScan(0, 115, wb, allCells, i);
         printScanResults(i, cellBelief, tgtBelief, pw, numTgtTypes);
      }
      pw.close();

      assertEquals("Detected an incorrect number of targets in cell.", 1, cellBelief.getNumTargetBeliefs());
   }
}
