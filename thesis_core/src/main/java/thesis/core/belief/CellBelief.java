package thesis.core.belief;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import thesis.core.common.CellCoordinate;
import thesis.core.common.SimTime;

public class CellBelief
{
   /**
    * Coefficient to use in the alpha filter for merging the newest target data
    * between two cell beliefs.
    */
   public static double NEWER_TGT_ALPHA = 0.5;// Default to 0.5

   /**
    * If the probability of the cell being empty is less than this value then
    * the Shannon uncertainty will be assumed to be zero.  Prevents NaNs.
    */
   private static final double SHANNON_ZERO_THRESHOLD = 0.000001;

   private List<TargetBelief> tgtBeliefs;

   /**
    * This is the time of the probability update if the data was updated by
    * direct sensor readings or it is a time approximated from merging belief
    * data to maintain synchronicity when propagating beliefs through more than
    * 2 agents.
    */
   private long pseudoTimestamp;

   private int numTgtTypes = 0;// FIXME This hsouldn't be stored here

   private double probCellEmpty;

   private CellCoordinate coord;

   /**
    * Rate in % / second in which certainty decays.
    */
   private double beliefDecayRatePerFrame;

   public CellBelief(int row, int col, int numTgtTypes, double beliefDecayRateS)
   {
      this.numTgtTypes = numTgtTypes;
      this.beliefDecayRatePerFrame = (beliefDecayRateS / 1000) * SimTime.SIM_STEP_RATE_MS;

      tgtBeliefs = new ArrayList<TargetBelief>();

      coord = new CellCoordinate(row, col);

      reset();
   }

   public void reset()
   {
      probCellEmpty = 0.5;
      pseudoTimestamp = 0;
      tgtBeliefs.clear();
   }

   public void stepSimulation()
   {
      //The If statement prevents screen flickers of probCellEmpty when rendered
      //due to probCellEmpty oscillating around 0.5
      if(Math.abs(0.5d - probCellEmpty) > beliefDecayRatePerFrame)
      {
         if(probCellEmpty < 0.5d)
         {
            probCellEmpty += beliefDecayRatePerFrame;
         }
         else
         {
            probCellEmpty -= beliefDecayRatePerFrame;
         }
      }
   }

   public CellCoordinate getCoordinate()
   {
      return coord;
   }

   public double getProbabilityEmptyCell()
   {
      return probCellEmpty;
   }

   public double getProbabilityNotEmptyCell()
   {
      return 1d - probCellEmpty;
   }

   public TargetBelief getTargetBelief(int tgtID)
   {
      TargetBelief tgtBelief = null;
      boolean tgtFound = false;

      Iterator<TargetBelief> itr = tgtBeliefs.iterator();
      while (itr.hasNext() && !tgtFound)
      {
         tgtBelief = itr.next();
         if (tgtBelief.getTrueTargetID() == tgtID)
         {
            tgtFound = true;
         }
      }

      if (tgtBelief == null)
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

      while (itr.hasNext() && !tgtFound)
      {
         if (itr.next().getTrueTargetID() == tgtID)
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

      while (itr.hasNext() && !tgtFound)
      {
         tgtBelief = itr.next();
         if (tgtBelief.getTrueTargetID() == tgtID)
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

   /**
    * @return The Shannon uncertainty value of the existance of a target.
    */
   public double getUncertainty()
   {
      double shannonUncert = 0;
      if(probCellEmpty > SHANNON_ZERO_THRESHOLD)
      {
         shannonUncert = (-probCellEmpty * Math.log10(probCellEmpty)) - ((1-probCellEmpty)*Math.log10(1-probCellEmpty));
      }
      return shannonUncert;
   }

   public void mergeBelief(CellBelief other)
   {
      final double INVERSE_NEWER_ALPHA = 1d - NEWER_TGT_ALPHA;

      if (other.pseudoTimestamp > pseudoTimestamp)
      {
         // TODO How/Could/Should merging handle expertise in sensing
         // different target types? Currently if a weak sensor scans 'now' it
         // will trump a strong sensor that scanned a second ago.

         // If the other belief has newer data then merge it in with an alpha
         // filter.
         probCellEmpty = (NEWER_TGT_ALPHA * other.probCellEmpty) + (INVERSE_NEWER_ALPHA * probCellEmpty);

         // Move this belief's timestamp forward towards the other belief's
         // time. This is an artifact of the merging process and why time is
         // called 'pseudoTime' instead of just 'time.' The time must be
         // adjusted so that transitively merging this data with a 3rd belief
         // doesn't cause oscillations in the probabilities due to the order
         // of merging.
         double timeDiff = Math.abs(pseudoTimestamp - other.pseudoTimestamp);
         pseudoTimestamp += (long) (INVERSE_NEWER_ALPHA * timeDiff);

         mergeTargetBeliefs(other);
      }
      // else: My data is newer so ignore the other belief's data
   }

   public void updateEmptyBelief(long simTime, double probEmpty)
   {
      pseudoTimestamp = simTime;
      probCellEmpty = probEmpty;
      //System.out.println(String.format("%d,%d,%.2f", coord.getRow(), coord.getColumn(), probEmpty));
   }

   private void mergeTargetBeliefs(CellBelief other)
   {
      List<TargetBelief> otherBeliefs = new ArrayList<TargetBelief>(other.tgtBeliefs);
      Iterator<TargetBelief> itr = otherBeliefs.iterator();

      while(itr.hasNext())
      {
         TargetBelief otherBelief = itr.next();
         if(hasDetectedTarget(otherBelief.getTrueTargetID()))
         {
            TargetBelief myBelief = getTargetBelief(otherBelief.getTrueTargetID());
            myBelief.merge(otherBelief, NEWER_TGT_ALPHA);
         }
         else
         {
            //Other belief has information on new targets
            tgtBeliefs.add(new TargetBelief(otherBelief));
         }
      }
   }
}
