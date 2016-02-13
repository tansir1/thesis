package thesis.core.belief;

public class CellBelief
{
   /**
    * Coefficient to use in the alpha filter for merging the newest target data
    * between two cell beliefs.
    */
   public static double NEWER_TGT_ALPHA = 0.5;//Default to 0.5

   private double tgtProbs[];
   private double tgtHdgs[];

   /**
    * This is the time of the probability update if the data was updated by
    * direct sensor readings or it is a time approximated from merging belief
    * data to maintain synchronicity when propagating beliefs through more than
    * 2 agents.
    */
   private long pseudoTimestamp[];

   public CellBelief(int numTgtTypes)
   {
      tgtProbs = new double[numTgtTypes];
      tgtHdgs = new double[numTgtTypes];
      pseudoTimestamp = new long[numTgtTypes];

      reset();
   }

   public void reset()
   {
      double equalProb = 1d / tgtProbs.length;
      for (int i = 0; i < tgtProbs.length; ++i)
      {
         tgtProbs[i] = equalProb;// Assume equal probability of all target types
         tgtHdgs[i] = 0;// If target exists, assume they have 0 heading
         pseudoTimestamp[i] = 0;
      }
   }

   public void mergeBelief(CellBelief other)
   {
      final double INVERSE_NEWER_ALPHA = 1d - NEWER_TGT_ALPHA;
      for (int i = 0; i < tgtProbs.length; ++i)
      {
         if (other.pseudoTimestamp[i] > pseudoTimestamp[i])
         {
            // TODO How/Could/Should merging handle expertise in sensing
            // different target types? Currently if a weak sensor scans 'now' it
            // will trump a strong sensor that scanned a second ago.

            // If the other belief has newer data then merge it in with an alpha
            // filter.
            tgtProbs[i] = (NEWER_TGT_ALPHA * other.tgtProbs[i]) + (INVERSE_NEWER_ALPHA * tgtProbs[i]);
            tgtHdgs[i] = (NEWER_TGT_ALPHA * other.tgtHdgs[i]) + (INVERSE_NEWER_ALPHA * tgtHdgs[i]);

            // Move this belief's timestamp forward towards the other belief's
            // time. This is an artifact of the merging process and why time is
            // called 'pseudoTime' instead of just 'time.' The time must be
            // adjusted so that transitively merging this data with a 3rd belief
            // doesn't cause oscillations in the probabilities due to the order
            // of merging.
            double timeDiff = Math.abs(pseudoTimestamp[i] - other.pseudoTimestamp[i]);
            pseudoTimestamp[i] += (long) (INVERSE_NEWER_ALPHA * timeDiff);
         }
         // else: My data is newer so ignore the other belief's data
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
      return pseudoTimestamp[tgtType];
   }

   public void updateTargetEstimates(int tgtType, double prob, double hdg, long timestamp)
   {
      tgtProbs[tgtType] = prob;
      tgtHdgs[tgtType] = hdg;
      pseudoTimestamp[tgtType] = timestamp;
   }

   public int getNumTgtTypes()
   {
      return tgtProbs.length;
   }
}
