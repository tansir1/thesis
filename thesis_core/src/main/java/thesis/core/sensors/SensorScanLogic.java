package thesis.core.sensors;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import thesis.core.belief.CellBelief;
import thesis.core.belief.TargetBelief;
import thesis.core.belief.WorldBelief;
import thesis.core.common.CellCoordinate;
import thesis.core.targets.Target;
import thesis.core.targets.TargetMgr;
import thesis.core.targets.TargetTypeConfigs;

public class SensorScanLogic
{
   static final double detectAngleDegradationSlope = -0.005555556f;
   static final double minDetectValue = 0.001;

   private SensorProbs snsrProbs;
   private TargetMgr tgtMgr;
   private Random randGen;

   public SensorScanLogic(SensorProbs pyldProbs, TargetMgr tgtMgr, Random randGen)
   {
      this.snsrProbs = pyldProbs;
      this.tgtMgr = tgtMgr;
      this.randGen = randGen;
   }

   public void simulateScan(int snsrType, double snsrHdg, WorldBelief belief, List<CellCoordinate> snsrFOV, long simTime)
   {
      //final int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
      final int NUM_COORDS = snsrFOV.size();

      for (int coordIdx = 0; coordIdx < NUM_COORDS; ++coordIdx)
      {
         newScan(snsrType, snsrFOV.get(coordIdx), snsrHdg, simTime, belief);
//         for (int tgtIdx = 0; tgtIdx < NUM_TGT_TYPES; ++tgtIdx)
//         {
//            scanCell(snsrType, tgtIdx, snsrFOV.get(coordIdx), snsrHdg, belief, 0L);
//         }
      }

   }

   private void newScan(int snsrType, CellCoordinate cell, double snsrHdg, long simTime, WorldBelief worldBelief)
   {
      CellBelief cellBelief = worldBelief.getCellBelief(cell);
      List<Target> trueTgts = detectTargets(snsrType, cell, snsrHdg, cellBelief);

      if(!trueTgts.isEmpty())
      {
         for(Target trueTgt : trueTgts)
         {
            int trueTgtType = trueTgt.getType();
            TargetBelief trueTgtBelief = cellBelief.getTargetBelief(trueTgt.getID());
            double estTgtHdg = trueTgtBelief.getHeadingEstimate();

            // Simulate the sensor scan of the environment
            double probOfScanTgt = 0;

            int detectedTgtType = classifyTarget(snsrType, estTgtHdg, snsrHdg, trueTgtType);
            if (trueTgtType == detectedTgtType)
            {
               probOfScanTgt = probOfDetect(snsrType, detectedTgtType, snsrHdg, estTgtHdg);
            }
            else
            {
               probOfScanTgt = probOfMisclassify(snsrType, trueTgtType, detectedTgtType, snsrHdg, estTgtHdg);
            }

            //double prevEstHdg = cellBelief.getTargetHeading(detectedTgtType);
            double bayesianUpdate = computeTargetBayesianUpdate(snsrType, cellBelief, snsrHdg, probOfScanTgt, trueTgtBelief.getTypeProbability(detectedTgtType), detectedTgtType, estTgtHdg);
            double hdgUpdate = computeHeadingUpdate(snsrType, snsrHdg, cell, estTgtHdg, detectedTgtType);

            trueTgtBelief.setHeadingEstimate(hdgUpdate);
            trueTgtBelief.setTimestamp(simTime);
            trueTgtBelief.setTypeProbability(detectedTgtType, bayesianUpdate);
         }
         cellBelief.updateBayesian(simTime, true);
      }
      else
      {
         cellBelief.updateBayesian(simTime, false);
      }
   }

   private List<Target> detectTargets(int snsrType, CellCoordinate cell, double snsrHdg, CellBelief cellBelief)
   {
      List<Target> tgtsTruth = tgtMgr.getTargetsInRegion(cell);
      if (tgtsTruth.size() > 0)
      {
         Iterator<Target> itr = tgtsTruth.iterator();
         Target tgt = itr.next();

         //Determine if the target was detected or not
         double estTgtHdg = 0;
         if(cellBelief.hasDetectedTarget(tgt.getID()))
         {
            //Target was seen in the past, get the last estimated heading
            estTgtHdg = cellBelief.getTargetBelief(tgt.getID()).getHeadingEstimate();
         }

         double probDetect = probOfDetect(snsrType, tgt.getType(), snsrHdg, estTgtHdg);
         if (randGen.nextDouble() > probDetect)
         {
            //Target not detected, remove it from the returned results list
            itr.remove();
         }
         //else target was detected
      }
      return tgtsTruth;
   }

   private int classifyTarget(int snsrType, double estTgtHdg, double snsrHdg, int trueTgtType)
   {
      int estimatedTgtType = trueTgtType;

      int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
      for (int i = 0; i < NUM_TGT_TYPES; ++i)
      {
         if(i == trueTgtType)
         {
            continue;//Do not misclassify the true type as the true type
         }

         // -1f is the default probability if no misclassification data
         // is available
         double misclassProb = probOfMisclassify(snsrType, trueTgtType, i, snsrHdg, estTgtHdg);
         if (randGen.nextDouble() < misclassProb)
         {
            // TODO Iterating target types in order weights occurrence
            // of misclassifcation to target types with lower type ID
            // numbers. Should probably iterate randomly through all
            // types.

            // Sensor will misclassify the target type
            estimatedTgtType = i;
            break;
         }
      }
      return estimatedTgtType;
   }

   private double computeTargetBayesianUpdate(int snsrType, CellBelief cellBelief, double snsrHdg, double probOfSensorResult, double prevProbOfSensorResult, int detectedTgtType, double tgtHdgEst)
   {
      // ---Update bayesian belief model for the target type in the scanned
      // cell---

      //@formatter:off
      // prob(tgt type exists Y) = prob(detect tgt type Y) * prob(previous belief tgt Y exists)
      //                           ---------------------------------------------------------
      //                           sum(prob(detect tgt type X as Y) * prob(previous belief off type X)) for all tgt type X
      //@formatter:on

      double bayesianNumerator = probOfSensorResult * prevProbOfSensorResult;

      // Bayesian update denominator. Acts as a normalizing factor.
      // double sumAllTgtProbs = getSumProbsAllTargets(snsrType, snsrHdg,
      // cellBelief, tgtType, probOfScanTgt);
      double sumAllTgtProbs = getSumProbsAllTargets(snsrType, snsrHdg, cellBelief, detectedTgtType, probOfSensorResult, prevProbOfSensorResult, tgtHdgEst);

      // New probability that the detected target type actually exists at the
      // cell location
      double bayesianUpdate = bayesianNumerator / sumAllTgtProbs;
      return bayesianUpdate;
   }

   private double computeHeadingUpdate(int snsrType, double snsrHdg, CellCoordinate cell, double prevTgtHdgEst, int detectedTgtType)
   {
      // Update heading
      double hdgConfCoeff = computeHeadingConfidenceCoeff(snsrType, detectedTgtType, snsrHdg, prevTgtHdgEst);
      double snsrEstHdg = computeHeadingEstimate(detectedTgtType, hdgConfCoeff, prevTgtHdgEst, cell);
      // Weighted alpha filter to update heading value
      double newEstHdg = (1d - hdgConfCoeff) * prevTgtHdgEst + hdgConfCoeff * snsrEstHdg;

      return newEstHdg;
   }

   private int determineSensorResult(int snsrType, CellCoordinate cell, double snsrHdg, CellBelief cellBelief)
   {
      int detectedTgtType = TargetTypeConfigs.NULL_TGT_TYPE;

      List<Target> tgtsTruth = tgtMgr.getTargetsInRegion(cell);
      if (tgtsTruth.size() > 0)
      {
         for (Target tgt : tgtsTruth)
         {
            double estTgtHdg = cellBelief.getTargetHeading(tgt.getType());
            double probDetect = probOfDetect(snsrType, tgt.getType(), snsrHdg, estTgtHdg);
            if (randGen.nextDouble() < probDetect)
            {
               // Detected the target. Determine if it gets misclassified.

               int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
               for (int i = 0; i < NUM_TGT_TYPES; ++i)
               {
                  // -1f is the default probability if no misclassification data
                  // is available
                  double misclassProb = probOfMisclassify(snsrType, tgt.getType(), i, snsrHdg, estTgtHdg);
                  if (randGen.nextDouble() < misclassProb)
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
                  // the true value
                  detectedTgtType = tgt.getType();
               }

               // The target was detected correctly or misclassified, either way
               // exit the target scanning loop.
               break;
            }
         }
      }
      else
      {

      }

      // TODO Should we able to detect an empty cell? If so, should there be a
      // chance to miss it? What does that mean?

      return detectedTgtType;
   }

   private double computeSenseDetectMetric(int snsrType, int tgtType)
   {
      // double bestAngle = tgtMgr.getTypeConfigs().getBestAngle(tgtType);
      // double maxMetric = probOfDetect(snsrType, tgtType, bestAngle, 0);
      double maxMetric = 0;

      for (double relAngle = 0; relAngle < 180; relAngle += 1.0)
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
   private double probOfDetect(int snsrType, int tgtType, double snsrHdg, double tgtHdg)
   {
      // double relHdg = Math.abs(tgtHdg - snsrHdg);
      // double deltaFromBestAngle = Math.abs(relHdg -
      // tgtMgr.getTypeConfigs().getBestAngle(tgtType));
      //
      // double percentOfBestProbAngle = (1.0 -
      // Math.abs(detectAngleDegradationSlope * deltaFromBestAngle))
      // + minDetectValue;
      // double probDetection = percentOfBestProbAngle *
      // snsrProbs.getSensorDetectProb(snsrType, tgtType);
      // return probDetection;
      return snsrProbs.getSensorDetectProb(snsrType, tgtType);
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
   private double probOfMisclassify(int snsrType, int suspectedRealTgtType, int detectedType, double snsrHdg,
         double suspectedTgtTypeHdgEst)
   {
      //TODO Add error catching if suspected type and detected type match

      double worstProbOfMisclass = snsrProbs.getSensorMisclassifyProb(snsrType, suspectedRealTgtType, detectedType);
      double probOfMisClass = 0.0000001;

      if (worstProbOfMisclass > 0d)
      {
         double relHdg = Math.abs(suspectedTgtTypeHdgEst - snsrHdg);
         double deltaFromBestAngle = Math.abs(relHdg - tgtMgr.getTypeConfigs().getBestAngle(suspectedRealTgtType));

         double percentOfBestProbAngle = Math.abs(detectAngleDegradationSlope * deltaFromBestAngle) + minDetectValue;
         probOfMisClass = percentOfBestProbAngle * worstProbOfMisclass;
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

   private void scanCell(int snsrType, int tgtType, CellCoordinate cell, double snsrHdg, WorldBelief belief,
         long simTime)
   {
      // if (computeSenseDetectMetric(snsrType, tgtType) < 1d)
      // {
      // // Sensor performance is not good enough to operate in these
      // // conditions. Do nothing.
      // return;
      // }

      CellBelief cellBelief = belief.getCellBelief(cell);

      // Simulate the sensor scan of the environment
      int detectedTgtType = 0;
      double probOfScanTgt = 0;

      detectedTgtType = determineSensorResult(snsrType, cell, snsrHdg, cellBelief);
      if (detectedTgtType == TargetTypeConfigs.NULL_TGT_TYPE)
      {
         // System.out.println("FAILED TO DETECT ANYTHING!");
         return;
      }

      if (tgtType == detectedTgtType)
      {
         probOfScanTgt = probOfDetect(snsrType, detectedTgtType, snsrHdg, cellBelief.getTargetHeading(detectedTgtType));
      }
      else
      {
         double suspectedTgtTypeHdgEst = cellBelief.getTargetHeading(tgtType);
         probOfScanTgt = probOfMisclassify(snsrType, tgtType, detectedTgtType, snsrHdg, suspectedTgtTypeHdgEst);
      }

      // ---Update bayesian belief model for the target type in the scanned
      // cell---

      //@formatter:off
      // prob(tgt type exists Y) = prob(detect tgt type Y) * prob(previous belief tgt Y exists)
      //                           ---------------------------------------------------------
      //                           sum(prob(detect tgt type X as Y) * prob(previous belief off type X)) for all tgt type X
      //@formatter:on

      double probDetectTgtExistsPreviously = cellBelief.getTargetProb(tgtType);
      double bayesianNumerator = probOfScanTgt * probDetectTgtExistsPreviously;

      // Bayesian update denominator. Acts as a normalizing factor.
      // double sumAllTgtProbs = getSumProbsAllTargets(snsrType, snsrHdg,
      // cellBelief, tgtType, probOfScanTgt);
      double sumAllTgtProbs = getSumProbsAllTargets(snsrType, snsrHdg, cellBelief, detectedTgtType, probOfScanTgt);

      // New probability that the detected target type actually exists at the
      // cell location
      double bayesianUpdate = bayesianNumerator / sumAllTgtProbs;

      // Update heading
      double prevEstHdg = cellBelief.getTargetHeading(detectedTgtType);
      double hdgConfCoeff = computeHeadingConfidenceCoeff(snsrType, detectedTgtType, snsrHdg, prevEstHdg);
      double snsrEstHdg = computeHeadingEstimate(detectedTgtType, hdgConfCoeff, prevEstHdg, cell);
      // Weighted alpha filter to update heading value
      double newEstHdg = (1d - hdgConfCoeff) * prevEstHdg + hdgConfCoeff * snsrEstHdg;

      // Prevent degenerate cases where the bayesian state gets railed and
      // blocks further
      // updates from adjusting the values due to everything being exactly zero
      // and one
      // bayesianUpdate = Math.max(bayesianUpdate, 0.001);
      // bayesianUpdate = Math.min(bayesianUpdate, 0.999);

      cellBelief.updateTargetEstimates(tgtType, bayesianUpdate, newEstHdg, simTime);
   }

   private double getSumProbsAllTargets(int snsrType, double snsrHdg, CellBelief cellBelief, int detectedTgtType,
         double probDetectTgtType, double prevProbOfSensorResult, double tgtHdgEst)
   {
      final int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
      double accumulator = 0;

      for (int i = 0; i < NUM_TGT_TYPES; ++i)
      {
         if (i != detectedTgtType)
         {
            // Probability that the true target is an 'i' and it was
            // misclassified as detectedTgtType
            double probMisClass = probOfMisclassify(snsrType, i, detectedTgtType, snsrHdg, tgtHdgEst);

            accumulator += probMisClass * prevProbOfSensorResult;
         }
         else
         {
            double probDetect = probOfDetect(snsrType, detectedTgtType, snsrHdg, tgtHdgEst);
            accumulator += probDetect * prevProbOfSensorResult;
         }

      }

      return accumulator;
   }

   private double getSumProbsAllTargetsORIG(int snsrType, double snsrHdg, CellBelief cellBelief, int detectedTgtType,
         double probDetectTgtType)
   {
      final int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
      double accumulator = 0;

      for (int i = 0; i < NUM_TGT_TYPES; ++i)
      {
         double probExistsPrior = cellBelief.getTargetProb(i);
         double tgtHdgEst = cellBelief.getTargetHeading(i);

         if (i != detectedTgtType)
         {
            // Probability that the true target is an 'i' and it was
            // misclassified as detectedTgtType
            double probMisClass = probOfMisclassify(snsrType, i, detectedTgtType, snsrHdg, tgtHdgEst);

            accumulator += probMisClass * probExistsPrior;
         }
         else
         {
            double probDetect = probOfDetect(snsrType, detectedTgtType, snsrHdg, tgtHdgEst);
            accumulator += probDetect * probExistsPrior;
         }

      }
      return accumulator;
   }

   private double computeHeadingConfidenceCoeff(int snsrType, int tgtType, double snsrHdg, double tgtHdg)
   {
      double bestHdgCoeff = snsrProbs.getSensorHeadingCoeff(snsrType, tgtType);
      double bestAngle = tgtMgr.getTypeConfigs().getBestAngle(tgtType);

      double relHdg = Math.abs(tgtHdg - snsrHdg);
      double deltaFromBestAngle = Math.abs(relHdg - bestAngle);

      double percentOfBestProbAngle = (1.0 - Math.abs(detectAngleDegradationSlope * deltaFromBestAngle))
            + minDetectValue;
      double hdgConfCoeff = percentOfBestProbAngle * bestHdgCoeff;
      return hdgConfCoeff;
   }

   private double computeHeadingEstimate(int tgtType, double hdgConfCoeff, double prevEstHdg, CellCoordinate cell)
   {
      // Assume all sensors can get a measurement within 90 degrees even at
      // worst case scenario
      double errorRange = 90 * (1.0f - hdgConfCoeff);
      double halfRng = errorRange / 2.0f;

      double error = halfRng * randGen.nextDouble();
      if (randGen.nextBoolean())
      {
         error = -error;
      }

      Target tgtTruth = tgtMgr.getTargetInRegion(cell, tgtType);

      double newEstHdg = 0;
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
