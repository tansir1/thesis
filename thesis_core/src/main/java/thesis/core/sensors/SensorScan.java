package thesis.core.sensors;

import java.util.List;
import java.util.Random;

import thesis.core.common.CellCoordinate;
import thesis.core.experimental.CellBelief;
import thesis.core.experimental.WorldBelief;
import thesis.core.targets.Target;
import thesis.core.targets.TargetMgr;
import thesis.core.targets.TargetTypeConfigs;

public class SensorScan
{
   static final float detectAngleDegradationSlope = -0.005555556f;
   static final float minDetectValue = 0;

   private SensorProbs pyldProbs;
   private TargetTypeConfigs tgtTypeCfgs;
   private TargetMgr tgtMgr;
   private Random randGen;

   public SensorScan(SensorProbs pyldProbs, TargetMgr tgtMgr, Random randGen)
   {
      this.pyldProbs = pyldProbs;
      this.tgtMgr = tgtMgr;
      this.randGen = randGen;
   }

   public void simulateScan(int snsrType, float snsrHdg, WorldBelief belief, List<CellCoordinate> snsrFOV, long simTime)
   {

      /*List<Target> tgtTruth = Array;

      for (int i = 0; i < NUM_COORDS; ++i)
      {
         tgtTruth = tgtMgr.getTargetsInRegion(snsrFOV.get(i));
      }*/

      final int NUM_TGT_TYPES = tgtTypeCfgs.getNumTypes();
      final int NUM_COORDS = snsrFOV.size();

      for (int coordIdx = 0; coordIdx < NUM_COORDS; ++coordIdx)
      {
         for (int tgtIdx = 0; tgtIdx < NUM_TGT_TYPES; ++tgtIdx)
         {
            scanCell(snsrType, tgtIdx, snsrFOV.get(coordIdx), snsrHdg, belief);
         }
      }

   }

   private double computeSenseDetectMetric(int snsrType, int tgtType)
   {
      double maxMetric = 0;
      for (float relAngle = 0; relAngle < 180; relAngle += 1.0)
      {
         double prob = probOfDetect(snsrType, tgtType, relAngle, 0);
         double metric = prob / (1.0 - prob);
         if (metric > maxMetric)
         {
            maxMetric = metric;
         }
      }
      return maxMetric;
   }

   private double probOfDetect(int snsrType, int tgtType, float snsrHdg, double tgtHdg)
   {
      double relHdg = Math.abs(tgtHdg - snsrHdg);
      double deltaFromBestAngle = Math.abs(relHdg - tgtMgr.getTypeConfigs().getBestAngle(tgtType));

      double percentOfBestProbAngle = detectAngleDegradationSlope * deltaFromBestAngle + minDetectValue;
      double probDetection = (1.0 - percentOfBestProbAngle) * pyldProbs.getSensorDetectProb(snsrType, tgtType);
      return probDetection;
   }

   private void scanCell(int snsrType, int tgtType, CellCoordinate cell, float snsrHdg, WorldBelief belief, long simTime)
   {
      TargetTypeConfigs tgtTypeCfgs = tgtMgr.getTypeConfigs();
      List<Target> allTgtTruth = tgtMgr.getTargetsInRegion(cell);
      Target tgtTruth = null;

      final int NUM_TGTS_IN_CELL = allTgtTruth.size();

      for(int i=0; i<NUM_TGTS_IN_CELL; ++i)
      {
         if(allTgtTruth.get(i).getType() == tgtType)
         {
            tgtTruth = allTgtTruth.get(i);
         }
      }

      double sensorMetric = computeSenseDetectMetric(snsrType, tgtType);
      if (sensorMetric >= 1.0)
      {
         CellBelief cellBelief = belief.getCellBelief(cell);
         double probDetect = probOfDetect(snsrType, tgtType, snsrHdg, cellBelief.getTargetHeading(tgtType));
         if (randGen.nextDouble() < probDetect)
         {
            // Update heading
            float prevEstHdg = cellBelief.getTargetHeading(tgtType);
            float hdgConfCoeff = computeHeadingConfidenceCoeff(snsrType, tgtType, snsrHdg, prevEstHdg);
            float snsrEstHdg = computeHeadingEstimate(tgtType, hdgConfCoeff, tgtTruth, prevEstHdg);
            float newEstHdg = (1.0f - hdgConfCoeff) * prevEstHdg + hdgConfCoeff * snsrEstHdg;
            cellBelief.updateTargetEstimates(tgtType, , newEstHdg, simTime);
         }
      }
      // else sensor will make false positives, don't scan for tgt type
   }

   private float computeHeadingConfidenceCoeff(int snsrType, int tgtType, float snsrHdg, float tgtHdg)
   {
      float bestHdgCoeff = pyldProbs.getSensorHeadingCoeff(snsrType, tgtType);
      float bestProb = pyldProbs.getSensorDetectProb(snsrType, tgtType);

      float relHdg = Math.abs(tgtHdg - snsrHdg);
      float deltaFromBestAngle = Math.abs(relHdg - bestProb);

      float percentOfBestProbAngle = detectAngleDegradationSlope * deltaFromBestAngle + minDetectValue;
      float hdgConfCoeff = (1.0f - percentOfBestProbAngle) * bestHdgCoeff;
      return hdgConfCoeff;
   }

   private float computeHeadingEstimate(int tgtType, float hdgConfCoeff, Target tgtTruth, float prevEstHdg)
   {
      float errorRange = 180 * (1.0f - hdgConfCoeff);
      float halfRng = errorRange / 2.0f;

      float error = halfRng * randGen.nextFloat();
      if (randGen.nextBoolean())
      {
         error = -error;
      }

      float newEstHdg = 0;
      if(tgtTruth != null)
      {
         newEstHdg = tgtTruth.getHeading() + error;
      }
      else
      {
         newEstHdg = prevEstHdg + error;
      }
      return newEstHdg;
   }

}
