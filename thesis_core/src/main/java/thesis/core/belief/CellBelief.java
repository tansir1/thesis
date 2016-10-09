package thesis.core.belief;

import thesis.core.common.CellCoordinate;
import thesis.core.common.SimTime;

public class CellBelief
{
   /**
    * Coefficient to use in the alpha filter for merging empty cell probability
    * data between two cell beliefs.
    */
   public static double NEWER_TGT_ALPHA = 0.5;// Default to 0.5

   /**
    * If the probability of the cell being empty is less than this value then
    * the Shannon uncertainty will be assumed to be zero. Prevents NaNs.
    */
   private static final double SHANNON_ZERO_THRESHOLD = 0.000001;

   /**
    * This is the time of the probability update if the data was updated by
    * direct sensor readings or it is a time approximated from merging belief
    * data to maintain synchronicity when propagating beliefs through more than
    * 2 agents.
    */
   private long pseudoTimestamp;

   private double probCellEmpty;

   private CellCoordinate coord;

   /**
    * Rate in % / second in which certainty decays.
    */
   private double beliefDecayRatePerFrame;

   public CellBelief(int row, int col, int numTgtTypes, double beliefDecayRateS)
   {
      this.beliefDecayRatePerFrame = (beliefDecayRateS / 1000) * SimTime.SIM_STEP_RATE_MS;

      coord = new CellCoordinate(row, col);

      reset();
   }

   public void reset()
   {
      probCellEmpty = 0.5;
      pseudoTimestamp = 0;
   }

   public void stepSimulation()
   {
      // The If statement prevents screen flickers of probCellEmpty when
      // rendered
      // due to probCellEmpty oscillating around 0.5
      if (Math.abs(0.5d - probCellEmpty) > beliefDecayRatePerFrame)
      {
         if (probCellEmpty < 0.5d)
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

   /**
    * @return The Shannon uncertainty value of the existance of a target.
    */
   public double getUncertainty()
   {
      double shannonUncert = 0;
      if (probCellEmpty > SHANNON_ZERO_THRESHOLD)
      {
         shannonUncert = (-probCellEmpty * Math.log10(probCellEmpty))
               - ((1 - probCellEmpty) * Math.log10(1 - probCellEmpty));
         //0.301 ~= to max uncertainty when p(empty) = 50%.  Scales return values to [0,1] instead of [0,0.301].
         shannonUncert /= 0.301;
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
         //double timeDiff = Math.abs(pseudoTimestamp - other.pseudoTimestamp);
         //pseudoTimestamp += (long) (INVERSE_NEWER_ALPHA * timeDiff);
         pseudoTimestamp = other.pseudoTimestamp;
      }
      // else: My data is newer so ignore the other belief's data
   }

   public void updateEmptyBelief(long simTime, double probEmpty)
   {
      pseudoTimestamp = simTime;
      probCellEmpty = probEmpty;
      // System.out.println(String.format("%d,%d,%.2f", coord.getRow(),
      // coord.getColumn(), probEmpty));
   }

   public long getPseudoTimestamp()
   {
      return pseudoTimestamp;
   }
}
