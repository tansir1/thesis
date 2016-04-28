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

public class SensorScanLogic
{
   private static final double detectAngleDegradationSlope = -0.005555556f;
   private static final double minDetectValue = 0.001;

   //These prevent NaN and positive/negative infinity issues
   private static final double MIN_BAYES_LIMIT = 0.000000000001;
   private static final double MAX_BAYES_LIMIT = 0.999999999999;

   private SensorProbs snsrProbs;
   private TargetMgr tgtMgr;
   private Random randGen;

   public SensorScanLogic(SensorProbs pyldProbs, TargetMgr tgtMgr, Random randGen)
   {
      this.snsrProbs = pyldProbs;
      this.tgtMgr = tgtMgr;
      this.randGen = randGen;
   }

   public SensorProbs getSensorProbabilities()
   {
      return snsrProbs;
   }

   public void simulateScan(int snsrType, double snsrHdg, WorldBelief belief, List<CellCoordinate> snsrFOV,
         long simTime)
   {
      final int NUM_COORDS = snsrFOV.size();

      for (int coordIdx = 0; coordIdx < NUM_COORDS; ++coordIdx)
      {
         scanCell(snsrType, snsrFOV.get(coordIdx), snsrHdg, simTime, belief);
      }
   }

   private void scanCell(int snsrType, CellCoordinate cell, double snsrHdg, long simTime, WorldBelief worldBelief)
   {
      CellBelief cellBelief = worldBelief.getCellBelief(cell);
      List<Target> trueTgts = detectTargets(snsrType, cell, snsrHdg, cellBelief, worldBelief);

      if (!trueTgts.isEmpty())
      {
         for (Target trueTgt : trueTgts)
         {
            int trueTgtType = trueTgt.getType();
            TargetBelief trueTgtBelief = worldBelief.getTargetBelief(trueTgt.getID());
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

            double bayesianUpdate = computeTargetBayesianUpdate(snsrType, cellBelief, snsrHdg, probOfScanTgt, detectedTgtType, trueTgtBelief);
            double hdgUpdate = computeHeadingUpdate(snsrType, snsrHdg, cell, estTgtHdg, detectedTgtType);

            trueTgtBelief.setHeadingEstimate(hdgUpdate);
            trueTgtBelief.setTimestamp(simTime);
            trueTgtBelief.setTypeProbability(detectedTgtType, bayesianUpdate);
            //TODO Assumes sensor can perfectly identify target location
            trueTgtBelief.setCoordinate(trueTgt.getCoordinate());

            computeCellEmptyBayesian(false, snsrType, cellBelief, simTime, trueTgts, probOfScanTgt);
         }

      }
      else
      {
         computeCellEmptyBayesian(true, snsrType, cellBelief, simTime, trueTgts, -1);
      }
   }

   private List<Target> detectTargets(int snsrType, CellCoordinate cell, double snsrHdg, CellBelief cellBelief, WorldBelief worldBelief)
   {
      List<Target> tgtsTruth = tgtMgr.getTargetsInRegion(cell);
      if (tgtsTruth.size() > 0)
      {
         Iterator<Target> itr = tgtsTruth.iterator();
         Target tgt = itr.next();

         // Determine if the target was detected or not
         double estTgtHdg = 0;
         if (worldBelief.hasDetectedTarget(tgt.getID()))
         {
            // Target was seen in the past, get the last estimated heading
            estTgtHdg = worldBelief.getTargetBelief(tgt.getID()).getHeadingEstimate();
         }

         double probDetect = probOfDetect(snsrType, tgt.getType(), snsrHdg, estTgtHdg);
         if (randGen.nextDouble() > probDetect)
         {
            // Target not detected, remove it from the returned results list
            itr.remove();
         }
         // else target was detected
      }
      return tgtsTruth;
   }

   private int classifyTarget(int snsrType, double estTgtHdg, double snsrHdg, int trueTgtType)
   {
      int estimatedTgtType = trueTgtType;

      int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
      for (int i = 0; i < NUM_TGT_TYPES; ++i)
      {
         if (i == trueTgtType)
         {
            continue;// Do not misclassify the true type as the true type
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

   private double computeTargetBayesianUpdate(int snsrType, CellBelief cellBelief, double snsrHdg,
         double probOfSensorResult, int detectedTgtType, TargetBelief tgtBelief)
   {
      // ---Update bayesian belief model for the target type in the scanned
      // cell---

      //@formatter:off
      // prob(tgt type exists Y) = prob(detect tgt type Y) * prob(previous belief tgt Y exists)
      //                           ---------------------------------------------------------
      //                           sum(prob(detect tgt type X as Y) * prob(previous belief off type X)) for all tgt type X
      //@formatter:on

      double bayesianNumerator = probOfSensorResult * tgtBelief.getTypeProbability(detectedTgtType);

      // Bayesian update denominator. Acts as a normalizing factor.
      // double sumAllTgtProbs = getSumProbsAllTargets(snsrType, snsrHdg,
      // cellBelief, tgtType, probOfScanTgt);
      double sumAllTgtProbs = getSumProbsAllTargets(snsrType, snsrHdg, cellBelief, detectedTgtType, probOfSensorResult,
            tgtBelief);

      // New probability that the detected target type actually exists at the
      // cell location
      double bayesianUpdate = bayesianNumerator / sumAllTgtProbs;
      return bayesianUpdate;
   }

   private double computeHeadingUpdate(int snsrType, double snsrHdg, CellCoordinate cell, double prevTgtHdgEst,
         int detectedTgtType)
   {
      // Update heading
      double hdgConfCoeff = computeHeadingConfidenceCoeff(snsrType, detectedTgtType, snsrHdg, prevTgtHdgEst);
      double snsrEstHdg = computeHeadingEstimate(detectedTgtType, hdgConfCoeff, prevTgtHdgEst, cell);
      // Weighted alpha filter to update heading value
      double newEstHdg = (1d - hdgConfCoeff) * prevTgtHdgEst + hdgConfCoeff * snsrEstHdg;

      return newEstHdg;
   }

   private void computeCellEmptyBayesian(boolean emptyCellDetected, int snsrType, CellBelief cellBelief, long simTime,
         List<Target> trueTgts, double probScanTgt)
   {
      double prevEmptyProb = cellBelief.getProbabilityEmptyCell();
      double prevNotEmptyProb = cellBelief.getProbabilityNotEmptyCell();

      double probDetectEmpty = snsrProbs.getSensorDetectEmptyProb(snsrType);
      double probMisclassAsEmpty = 1d;

      if(trueTgts.isEmpty())
      {
         probMisclassAsEmpty = 0.5d;
      }
      else
      {
         for (Target trueTgt : trueTgts)
         {
            double missedTgt = 1d - snsrProbs.getSensorDetectTgtProb(snsrType, trueTgt.getType());
            probMisclassAsEmpty *= missedTgt;
         }
      }


      if (emptyCellDetected)
      {
         double numerator = probDetectEmpty * prevEmptyProb;
         double denominator = (probDetectEmpty * prevEmptyProb) + (probMisclassAsEmpty * prevNotEmptyProb);
         double bayes = numerator / denominator;
         bayes = Math.min(bayes, MAX_BAYES_LIMIT);
         bayes = Math.max(bayes, MIN_BAYES_LIMIT);
         cellBelief.updateEmptyBelief(simTime, bayes);
      }
      else
      {
         double numerator = probScanTgt * prevNotEmptyProb;
         // 0% chance of false positive target detections, numerator and
         // denominator are equal
         double denominator = (probScanTgt * prevNotEmptyProb);

         double bayes = numerator / denominator;
         bayes = Math.min(bayes, MAX_BAYES_LIMIT);
         bayes = Math.max(bayes, MIN_BAYES_LIMIT);
         cellBelief.updateEmptyBelief(simTime, 1d - bayes);
      }
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
      double relHdg = Math.abs(tgtHdg - snsrHdg);
      double deltaFromBestAngle = Math.abs(relHdg - tgtMgr.getTypeConfigs().getBestAngle(tgtType));

      double percentOfBestProbAngle = (1.0 - Math.abs(detectAngleDegradationSlope * deltaFromBestAngle))
            + minDetectValue;
      double probDetection = percentOfBestProbAngle * snsrProbs.getSensorDetectTgtProb(snsrType, tgtType);
      return probDetection;
      // return snsrProbs.getSensorDetectTgtProb(snsrType, tgtType);
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
      // TODO Add error catching if suspected type and detected type match

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

   private double getSumProbsAllTargets(int snsrType, double snsrHdg, CellBelief cellBelief, int detectedTgtType,
         double probDetectTgtType, TargetBelief tgtBelief)
   {
      final int NUM_TGT_TYPES = tgtMgr.getTypeConfigs().getNumTypes();
      double accumulator = 0;

      for (int i = 0; i < NUM_TGT_TYPES; ++i)
      {
         if (i != detectedTgtType)
         {
            // Probability that the true target is an 'i' and it was
            // misclassified as detectedTgtType
            double probMisClass = probOfMisclassify(snsrType, i, detectedTgtType, snsrHdg, tgtBelief.getHeadingEstimate());

            accumulator += probMisClass * tgtBelief.getTypeProbability(i);
         }
         else
         {
            double probDetect = probOfDetect(snsrType, detectedTgtType, snsrHdg, tgtBelief.getHeadingEstimate());
            accumulator += probDetect * tgtBelief.getTypeProbability(i);
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
