package thesis.core.experimental;

public class CellBelief
{
   private double tgtProbs[];
   private double tgtHdgs[];
   private long timestamps[];

   public CellBelief(int numTgtTypes)
   {
      tgtProbs = new double[numTgtTypes];
      tgtHdgs = new double[numTgtTypes];
      timestamps = new long[numTgtTypes];

      reset();
   }

   public void reset()
   {
      double equalProb = 1d / tgtProbs.length;
      for(int i=0; i<tgtProbs.length; ++i)
      {
         tgtProbs[i] = equalProb;//Assume equal probability of all target types
         tgtHdgs[i] = 0;//If target exists, assume they have 0 heading
         timestamps[i] = 0;
      }
   }

   public double getTargetProb(int tgtType)
   {
      return tgtProbs[tgtType];
   }

   public double getTargetHeading(int tgtType)
   {
      return tgtHdgs[tgtType];
   }

   public long getTargetEstTime(int tgtType)
   {
      return timestamps[tgtType];
   }

   public void updateTargetEstimates(int tgtType, double prob, double hdg, long timestamp)
   {
      tgtProbs[tgtType] = prob;
      tgtHdgs[tgtType] = hdg;
      timestamps[tgtType] = timestamp;

      double total = 0;
      for(int i=0; i<tgtProbs.length; ++i)
      {
         total += tgtProbs[i];
      }

      for(int i=0; i<tgtProbs.length; ++i)
      {
         tgtProbs[i] /= total;
      }

   }
}
