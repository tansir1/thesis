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

   private SensorProbs snsrProbs;
   private TargetMgr tgtMgr;
   private Random randGen;

   public SensorScan(SensorProbs pyldProbs, TargetMgr tgtMgr, Random randGen)
   {
      this.snsrProbs = pyldProbs;
      this.tgtMgr = tgtMgr;
      this.randGen = randGen;
   }

   public void simulateScan(int snsrType, float snsrHdg, WorldBelief belief, List<CellCoordinate> snsrFOV, long simTime)
   {
      final int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
      final int NUM_COORDS = snsrFOV.size();

      for (int coordIdx = 0; coordIdx < NUM_COORDS; ++coordIdx)
      {
         for (int tgtIdx = 0; tgtIdx < NUM_TGT_TYPES; ++tgtIdx)
         {
            scanCell(snsrType, tgtIdx, snsrFOV.get(coordIdx), snsrHdg, belief, 0L);
         }
      }

   }

   private int determineSensorResult(int snsrType, CellCoordinate cell, float snsrHdg, CellBelief cellBelief)
   {
      // This assumes a 100% detection rate of an empty cell
      int detectedTgtType = TargetTypeConfigs.NULL_TGT_TYPE;

      List<Target> tgtsTruth = tgtMgr.getTargetsInRegion(cell);
      if (tgtsTruth.size() > 0)
      {
         for (Target tgt : tgtsTruth)
         {
            float estTgtHdg = cellBelief.getTargetHeading(tgt.getType());
            double probDetect = probOfDetect(snsrType, tgt.getType(), snsrHdg, estTgtHdg);
            if (randGen.nextDouble() < probDetect)
            {
               // Detected the target. Determine if it gets misclassified.

               int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
               for (int i = 0; i < NUM_TGT_TYPES; ++i)
               {
                  // -1f is the default probability if no misclassification data
                  // is available
                  float misclassProb = snsrProbs.getSensorMisclassifyProb(snsrType, tgt.getType(), i);
                  if (randGen.nextFloat() < misclassProb)
                  {
                     // TODO Iterating target types in order weights occurrence
                     // of misclassifcation to target types with lower type ID
                     // numbers. Should probably iterate randomly through all
                     // types.

                     // Sensor will misclassify the target type
                     detectedTgtType = i;
                     break;
                  }
               }

               if (detectedTgtType == TargetTypeConfigs.NULL_TGT_TYPE)
               {
                  // Target was not misclassified so set the detected type to
                  // the true
                  // value
                  detectedTgtType = tgt.getType();
               }

               // The target was detected correctly or misclassified, either way
               // exit the target scanning loop.
               break;
            }
         }
      }

      // TODO Should we able to detect an empty cell? If so, should there be a
      // chance to miss it? What does that mean?

      return detectedTgtType;
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

   /**
    * Computes the probability that the given sensor detects the given target
    * type when they are offset by the given angles. Assumes the best
    * probability of detection occurs when the relative angle between the sensor
    * and target is at zero degrees off from the target's "best" angle. Any
    * offset from "best" suffers a linear degradation approaching zero when the
    * relative difference is 180 degrees off from "best" angle.
    *
    * @param snsrType
    * @param tgtType
    * @param snsrHdg
    * @param tgtHdg
    * @return
    */
   private double probOfDetect(int snsrType, int tgtType, float snsrHdg, double tgtHdg)
   {
      double relHdg = Math.abs(tgtHdg - snsrHdg);
      double deltaFromBestAngle = Math.abs(relHdg - tgtMgr.getTypeConfigs().getBestAngle(tgtType));

      double percentOfBestProbAngle = detectAngleDegradationSlope * deltaFromBestAngle + minDetectValue;
      double probDetection = (1.0 - percentOfBestProbAngle) * snsrProbs.getSensorDetectProb(snsrType, tgtType);
      return probDetection;
   }

   /**
    * Compute the probability that the detected target type is actually a
    * mistake and that the true target type is instead the suspected target
    * type. The function asssumes that the worst case scenario for
    * misclassification occurs when the relative heading between the sensor and
    * suspected target are at 180 degrees from the target's best detection angle
    * (example: staring at the the target from the back instead of the front).
    * The probability of misclassification decays linearly as the relative
    * heading approaches zero degrees off from the suspected target's best
    * angle.
    *
    * @param snsrType
    *           The type of sensor performing the classification.
    * @param suspectedRealTgtType
    *           Compute the probability that the detectedType is wrong and the
    *           target is actually this type.
    * @param detectedType
    *           The type of target the sensor believes it detected.
    * @param snsrHdg
    *           Heading of the sensor performing the classification.
    * @param suspectedTgtTypeHdgEst
    *           The estimated heading of the suspected target type at the same
    *           location as the detected type.
    * @return The probability that the suspected target type was misclassified
    *         as the detected target type.
    */
   private double probOfMisclassify(int snsrType, int suspectedRealTgtType, int detectedType, float snsrHdg,
         float suspectedTgtTypeHdgEst)
   {
      double worstProbOfMisclass = snsrProbs.getSensorMisclassifyProb(snsrType, suspectedRealTgtType, detectedType);
      double probOfMisClass = 0;

      if (worstProbOfMisclass > 0d)
      {
         double relHdg = Math.abs(suspectedTgtTypeHdgEst - snsrHdg);
         double deltaFromBestAngle = Math.abs(relHdg - tgtMgr.getTypeConfigs().getBestAngle(suspectedRealTgtType));

         double percentOfBestProbAngle = detectAngleDegradationSlope * deltaFromBestAngle + minDetectValue;
         probOfMisClass = percentOfBestProbAngle * probOfMisClass;
      }
      else
      {
         // worstProbOfMisclass defaults to -1f

         // Sensor is not capable of misclassifying suspectedRealTgtType as
         // detectedType (or no data was entered into the configuration files
         // for this combo).
      }

      return probOfMisClass;
   }

   private void scanCell(int snsrType, int tgtType, CellCoordinate cell, float snsrHdg, WorldBelief belief,
         long simTime)
   {
      if (computeSenseDetectMetric(snsrType, tgtType) < 1d)
      {
         // Sensor performance is not good enough to operate in these
         // conditions. Do nothing.
         return;
      }

      CellBelief cellBelief = belief.getCellBelief(cell);

      // Simulate the sensor scan of the environment
      int detectedTgtType = determineSensorResult(snsrType, cell, snsrHdg, cellBelief);

      // ---Update bayesian belief model for the target type in the scanned
      // cell---

      // prob(tgt type exists Y) = prob(detect tgt type Y) * prob(previous
      // belief tgt Y exists)
      // ---------------------------------------------------------
      // sum(prob(detect tgt type X as Y) * prob(previous belief off type X))
      // for all tgt type X
      double probDetect = probOfDetect(snsrType, detectedTgtType, snsrHdg,
            cellBelief.getTargetEstTime(detectedTgtType));
      double probDetectTgtExistsPreviously = cellBelief.getTargetProb(detectedTgtType);
      double bayesianNumerator = probDetect * probDetectTgtExistsPreviously;

      // Bayesian update denominator. Acts as a normalizing factor.
      double sumAllTgtProbs = getSumProbsAllTargets(snsrType, snsrHdg, cellBelief, detectedTgtType, probDetect);

      // New probability that the detected target type actually exists at the
      // cell location
      float bayesianUpdate = (float)(bayesianNumerator / sumAllTgtProbs);

      // Update heading
      float prevEstHdg = cellBelief.getTargetHeading(detectedTgtType);
      float hdgConfCoeff = computeHeadingConfidenceCoeff(snsrType, detectedTgtType, snsrHdg, prevEstHdg);
      float snsrEstHdg = computeHeadingEstimate(detectedTgtType, hdgConfCoeff, prevEstHdg, cell);
      //Weighted alpha filter to update heading value
      float newEstHdg = (1.0f - hdgConfCoeff) * prevEstHdg + hdgConfCoeff * snsrEstHdg;

      cellBelief.updateTargetEstimates(tgtType, bayesianUpdate, newEstHdg, simTime);

   }

   private double getSumProbsAllTargets(int snsrType, float snsrHdg, CellBelief cellBelief, int detectedTgtType,
         double probDetectTgtType)
   {
      final int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
      double accumulator = 0;

      for (int i = 0; i < NUM_TGT_TYPES; ++i)
      {
         double probExistsPrior = cellBelief.getTargetProb(i);

         if (i != detectedTgtType)
         {
            // Probability that the true target is an 'i' and it was
            // misclassified as detectedTgtType
            double probMisClass = probOfMisclassify(snsrType, i, detectedTgtType, snsrHdg,
                  cellBelief.getTargetHeading(i));

            accumulator += probMisClass * probExistsPrior;
         }
         else
         {
            accumulator += probDetectTgtType * probExistsPrior;
         }

      }
      accumulator /= NUM_TGT_TYPES;
      return accumulator;
   }

   private float computeHeadingConfidenceCoeff(int snsrType, int tgtType, float snsrHdg, float tgtHdg)
   {
      float bestHdgCoeff = snsrProbs.getSensorHeadingCoeff(snsrType, tgtType);
      float bestProb = snsrProbs.getSensorDetectProb(snsrType, tgtType);

      float relHdg = Math.abs(tgtHdg - snsrHdg);
      float deltaFromBestAngle = Math.abs(relHdg - bestProb);

      float percentOfBestProbAngle = detectAngleDegradationSlope * deltaFromBestAngle + minDetectValue;
      float hdgConfCoeff = (1.0f - percentOfBestProbAngle) * bestHdgCoeff;
      return hdgConfCoeff;
   }

   private float computeHeadingEstimate(int tgtType, float hdgConfCoeff, float prevEstHdg, CellCoordinate cell)
   {
      float errorRange = 180 * (1.0f - hdgConfCoeff);
      float halfRng = errorRange / 2.0f;

      float error = halfRng * randGen.nextFloat();
      if (randGen.nextBoolean())
      {
         error = -error;
      }

      Target tgtTruth = tgtMgr.getTargetInRegion(cell, tgtType);

      float newEstHdg = 0;
      if (tgtTruth != null)
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
