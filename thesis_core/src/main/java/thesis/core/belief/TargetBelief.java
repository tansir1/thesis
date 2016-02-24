package thesis.core.belief;

public class TargetBelief
{
   private double typeProbs[];
   private double heading;
   private long timestamp;
   private int trueTgtID;

   public TargetBelief(int numTgtTypes, int trueTgtID)
   {
      this.trueTgtID = trueTgtID;
      typeProbs = new double[numTgtTypes];
      reset();
   }

   public void reset()
   {
      timestamp = 0;
      heading = 0;

      double equalProb = 1d / typeProbs.length;
      for (int i = 0; i < typeProbs.length; ++i)
      {
         typeProbs[i] = equalProb;// Assume equal probability of all target types
      }
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

      //Normalize and ensure all target type probabilities sum to 1
      double normalize = 0;
      for (int i = 0; i < typeProbs.length; ++i)
      {
         normalize += typeProbs[i];
      }

      for (int i = 0; i < typeProbs.length; ++i)
      {
         typeProbs[i] /= normalize;
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
      return timestamp;
   }

   public void setTimestamp(long timestamp)
   {
      this.timestamp = timestamp;
   }
}
