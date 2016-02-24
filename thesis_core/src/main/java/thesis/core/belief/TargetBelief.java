package thesis.core.belief;

public class TargetBelief
{
   /**
    * The total probabilty of all target types must exceed this value otherwise
    * normalization will not occur. Protects against division by zero.
    */
   static final double PROB_NORM_ZERO_THRESHOLD = 0.000001;

   private double typeProbs[];
   private double heading;
   private long pseudoTimestamp;
   private int trueTgtID;

   public TargetBelief(int numTgtTypes, int trueTgtID)
   {
      this.trueTgtID = trueTgtID;
      typeProbs = new double[numTgtTypes];
      reset();
   }

   public TargetBelief(TargetBelief copy)
   {
      heading = copy.heading;
      pseudoTimestamp = copy.pseudoTimestamp;
      trueTgtID = copy.trueTgtID;
      typeProbs = new double[copy.typeProbs.length];
      System.arraycopy(copy.typeProbs, 0, typeProbs, 0, typeProbs.length);
   }

   public void reset()
   {
      pseudoTimestamp = 0;
      heading = 0;

      double equalProb = 1d / typeProbs.length;
      for (int i = 0; i < typeProbs.length; ++i)
      {
         typeProbs[i] = equalProb;// Assume equal probability of all target
                                  // types
      }
   }

   public void merge(TargetBelief other, final double NEWER_TGT_ALPHA)
   {
      // Fake cross-track correlation between belief models from different
      // agents by using the true target ID of the detected targets
      if (other.trueTgtID != trueTgtID)
      {
         return;// The targets are not the same, do not merge
      }

      double INVERSE_NEWER_ALPHA = 1d - NEWER_TGT_ALPHA;

      // If the other belief has newer data then merge it in with an alpha
      // filter.
      heading = (NEWER_TGT_ALPHA * other.heading) + (INVERSE_NEWER_ALPHA * heading);
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
      return heading;
   }

   public void setHeadingEstimate(double heading)
   {
      this.heading = heading;
   }

   public long getTimestamp()
   {
      return pseudoTimestamp;
   }

   public void setTimestamp(long timestamp)
   {
      this.pseudoTimestamp = timestamp;
   }
}
