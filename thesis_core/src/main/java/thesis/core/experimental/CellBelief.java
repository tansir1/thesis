package thesis.core.experimental;

public class CellBelief
{
   private float tgtProbs[];
   private float tgtHdgs[];
   private long timestamps[];

   public CellBelief(int numTgtTypes)
   {
      tgtProbs = new float[numTgtTypes];
      tgtHdgs = new float[numTgtTypes];
      timestamps = new long[numTgtTypes];

      reset();
   }

   public void reset()
   {
      float equalProb = 1.f / tgtProbs.length;
      for(int i=0; i<tgtProbs.length; ++i)
      {
         tgtProbs[i] = equalProb;//Assume equal probability of all target types
         tgtHdgs[i] = 0;//If target exists, assume they have 0 heading
         timestamps[i] = 0;
      }
   }

   public float getTargetProb(int tgtType)
   {
      return tgtProbs[tgtType];
   }

   public float getTargetHeading(int tgtType)
   {
      return tgtHdgs[tgtType];
   }

   public long getTargetEstTime(int tgtType)
   {
      return timestamps[tgtType];
   }

   public void updateTargetEstimates(int tgtType, float prob, float hdg, long timestamp)
   {
      tgtProbs[tgtType] = prob;
      tgtHdgs[tgtType] = hdg;
      timestamps[tgtType] = timestamp;

      float total = 0;
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
