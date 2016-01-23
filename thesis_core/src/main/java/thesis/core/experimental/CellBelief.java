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
      for(int i=0; i<tgtProbs.length; ++i)
      {
         tgtProbs[i] = 0;//Assume targets do not exist at location
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
   }
}
