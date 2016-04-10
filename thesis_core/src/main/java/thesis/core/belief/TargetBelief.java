package thesis.core.belief;

import thesis.core.common.WorldCoordinate;
import thesis.core.common.WorldPose;

public class TargetBelief
{
   /**
    * The total probabilty of all target types must exceed this value otherwise
    * normalization will not occur. Protects against division by zero.
    */
   static final double PROB_NORM_ZERO_THRESHOLD = 0.000001;

   private double typeProbs[];
   private long pseudoTimestamp;
   private int trueTgtID;
   private WorldPose pose;
   private TargetTaskStatus taskStatus;

   public TargetBelief(int numTgtTypes, int trueTgtID)
   {
      this.trueTgtID = trueTgtID;
      typeProbs = new double[numTgtTypes];
      pose = new WorldPose();
      taskStatus = new TargetTaskStatus();
      reset();
   }

   public TargetBelief(TargetBelief copy)
   {
      pseudoTimestamp = copy.pseudoTimestamp;
      trueTgtID = copy.trueTgtID;
      pose = new WorldPose(copy.pose);
      typeProbs = new double[copy.typeProbs.length];
      System.arraycopy(copy.typeProbs, 0, typeProbs, 0, typeProbs.length);
      taskStatus.copyFrom(copy.taskStatus);
   }

   public void reset()
   {
      taskStatus.reset();

      pseudoTimestamp = 0;
      pose.setHeading(0);
      pose.getCoordinate().setCoordinate(0, 0);

      double equalProb = 1d / typeProbs.length;
      for (int i = 0; i < typeProbs.length; ++i)
      {
         // Assume equal probability of all target types
         typeProbs[i] = equalProb;
      }
   }

   public void merge(TargetBelief other, final double NEWER_TGT_ALPHA)
   {
      if (other.pseudoTimestamp < pseudoTimestamp)
      {
         return;// My data is newer, ignore the other belief
      }

      // Fake cross-track correlation between belief models from different
      // agents by using the true target ID of the detected targets
      if (other.trueTgtID != trueTgtID)
      {
         return;// The targets are not the same, do not merge
      }

      double INVERSE_NEWER_ALPHA = 1d - NEWER_TGT_ALPHA;

      // If the other belief has newer data then merge it in with an alpha
      // filter.
      double heading = (NEWER_TGT_ALPHA * other.pose.getHeading()) + (INVERSE_NEWER_ALPHA * pose.getHeading());
      pose.setHeading(heading);

      pose.getCoordinate().interpolateTowards(other.getCoordinate(), NEWER_TGT_ALPHA);

      for (int i = 0; i < typeProbs.length; ++i)
      {
         typeProbs[i] = (NEWER_TGT_ALPHA * other.typeProbs[i]) + (INVERSE_NEWER_ALPHA * typeProbs[i]);
      }

      // Move this belief's timestamp forward towards the other belief's
      // time. This is an artifact of the merging process and why time is
      // called 'pseudoTime' instead of just 'time.' The time must be
      // adjusted so that transitively merging this data with a 3rd belief
      // doesn't cause oscillations in the probabilities due to the order
      // of merging.
      double timeDiff = Math.abs(pseudoTimestamp - other.pseudoTimestamp);
      pseudoTimestamp += (long) (INVERSE_NEWER_ALPHA * timeDiff);

      taskStatus.merge(other.taskStatus);
   }

   public int getTrueTargetID()
   {
      return trueTgtID;
   }

   public double getTypeProbability(int type)
   {
      return typeProbs[type];
   }

   public void setTypeProbability(int type, double prob)
   {
      // Prevent degenerate cases where the bayesian state gets railed and
      // blocks further
      // updates from adjusting the values due to everything being exactly zero
      // and one
      // prob = Math.max(prob, 0.001);
      // prob = Math.min(prob, 0.999);

      typeProbs[type] = prob;

      // Normalize and ensure all target type probabilities sum to 1
      double normalize = 0;
      for (int i = 0; i < typeProbs.length; ++i)
      {
         normalize += typeProbs[i];
      }

      if (normalize > PROB_NORM_ZERO_THRESHOLD)
      {
         for (int i = 0; i < typeProbs.length; ++i)
         {
            typeProbs[i] /= normalize;
         }
      }

   }

   public double getHeadingEstimate()
   {
      return pose.getHeading();
   }

   public void setHeadingEstimate(double heading)
   {
      pose.setHeading(heading);
   }

   public WorldCoordinate getCoordinate()
   {
      return pose.getCoordinate();
   }

   public WorldPose getPose()
   {
      return pose;
   }

   public long getTimestamp()
   {
      return pseudoTimestamp;
   }

   public void setTimestamp(long timestamp)
   {
      this.pseudoTimestamp = timestamp;
   }

   public void setCoordinate(WorldCoordinate coord)
   {
      pose.getCoordinate().setCoordinate(coord);
   }

   public TargetTaskStatus getTaskStatus()
   {
      return taskStatus;
   }

   public int getHighestProbabilityTargetType()
   {
      int maxType = -1;
      double maxProb = -1;
      for (int i = 0; i < typeProbs.length; ++i)
      {
         if (typeProbs[i] > maxProb)
         {
            maxType = i;
            maxProb = typeProbs[i];
         }
      }
      return maxType;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + trueTgtID;
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TargetBelief other = (TargetBelief) obj;
      if (trueTgtID != other.trueTgtID)
         return false;
      return true;
   }

}
