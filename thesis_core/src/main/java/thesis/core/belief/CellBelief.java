package thesis.core.belief;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CellBelief
{
   /**
    * Coefficient to use in the alpha filter for merging the newest target data
    * between two cell beliefs.
    */
   public static double NEWER_TGT_ALPHA = 0.5;//Default to 0.5

   private List<TargetBelief> tgtBeliefs;

   /**
    * This is the time of the probability update if the data was updated by
    * direct sensor readings or it is a time approximated from merging belief
    * data to maintain synchronicity when propagating beliefs through more than
    * 2 agents.
    */
   private long pseudoTimestamp;

   private int numTgtTypes = 0;//FIXME This hsouldn't be stored here

   private double probTgtExists;

   public CellBelief(int numTgtTypes)
   {
      this.numTgtTypes = numTgtTypes;
      tgtBeliefs = new ArrayList<TargetBelief>();

      reset();
   }

   public void reset()
   {
      probTgtExists = 0.5;
      pseudoTimestamp = 0;
      tgtBeliefs.clear();
   }

   public double getProbabilityEmptyCell()
   {
      return 1d - probTgtExists;
   }

   public double getProbabilityNotEmptyCell()
   {
      return probTgtExists;
   }

   public TargetBelief getTargetBelief(int tgtID)
   {
      TargetBelief tgtBelief = null;
      boolean tgtFound = false;

      Iterator<TargetBelief> itr = tgtBeliefs.iterator();
      while(itr.hasNext() && !tgtFound)
      {
         tgtBelief = itr.next();
         if(tgtBelief.getTrueTargetID() == tgtID)
         {
            tgtFound = true;
         }
      }

      if(tgtBelief == null)
      {
         tgtBelief = new TargetBelief(numTgtTypes, tgtID);
         tgtBeliefs.add(tgtBelief);
      }
      return tgtBelief;
   }

   public boolean hasDetectedTarget(int tgtID)
   {
      boolean tgtFound = false;
      Iterator<TargetBelief> itr = tgtBeliefs.iterator();

      while(itr.hasNext() && !tgtFound)
      {
         if(itr.next().getTrueTargetID() == tgtID)
         {
            tgtFound = true;
         }
      }
      return tgtFound;
   }

   public void removeTarget(int tgtID)
   {
      boolean tgtFound = false;
      Iterator<TargetBelief> itr = tgtBeliefs.iterator();
      TargetBelief tgtBelief = null;

      while(itr.hasNext() && !tgtFound)
      {
         tgtBelief = itr.next();
         if(tgtBelief.getTrueTargetID() == tgtID)
         {
            itr.remove();
            tgtFound = true;
         }
      }
   }

   public int getNumTargetBeliefs()
   {
      return tgtBeliefs.size();
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

   public void updateBayesian(long simTime, boolean detectedTgt)
   {
      pseudoTimestamp = simTime;

      //The cell is empty or it isn't, 50% chance either way
      double denominator = (0.5 * probTgtExists) + ((1d - probTgtExists) * 0.5);
      if(detectedTgt)
      {
         probTgtExists = (0.5 * probTgtExists) / denominator;
      }
      else
      {
         probTgtExists = (0.5 * (1d-probTgtExists)) / denominator;
      }
   }
}
