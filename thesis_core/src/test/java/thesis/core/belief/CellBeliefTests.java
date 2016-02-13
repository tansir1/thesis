package thesis.core.belief;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CellBeliefTests
{

   @Test
   public void mergeTest()
   {
      final double HDG_TOLERANCE = 0.01;
      final double PROB_TOLERANCE = 0.0001;

      CellBelief.NEWER_TGT_ALPHA = 0.7;
      CellBelief cb1 = new CellBelief(2);
      CellBelief cb2 = new CellBelief(2);

      cb1.updateTargetEstimates(0, 0.5, 10, 1000);
      cb1.updateTargetEstimates(1, 0.9, 100, 1000);

      cb2.updateTargetEstimates(0, 0, 0, 0);
      cb2.updateTargetEstimates(1, 0, 0, 0);

      cb2.mergeBelief(cb1);
      assertEquals("M1:Tgt0: Invalid merge of heading data", 7d, cb2.getTargetHeading(0), HDG_TOLERANCE);
      assertEquals("M1:Tgt0: Invalid merge of probability data", 0.35d, cb2.getTargetProb(0), PROB_TOLERANCE);
      assertEquals("M1:Tgt0: Invalid merge of pseudotime data", 300, cb2.getTargetEstTime(0));

      assertEquals("M1:Tgt1: Invalid merge of heading data", 70d, cb2.getTargetHeading(1), HDG_TOLERANCE);
      assertEquals("M1:Tgt1: Invalid merge of probability data", 0.63d, cb2.getTargetProb(1), PROB_TOLERANCE);
      assertEquals("M1:Tgt1: Invalid merge of pseudotime data", 300, cb2.getTargetEstTime(1));

      cb2.mergeBelief(cb1);
      assertEquals("M2:Tgt0: Invalid merge of heading data", 9.1, cb2.getTargetHeading(0), HDG_TOLERANCE);
      assertEquals("M2:Tgt0: Invalid merge of probability data", 0.4549, cb2.getTargetProb(0), PROB_TOLERANCE);
      assertEquals("M1:Tgt0: Invalid merge of pseudotime data", 510, cb2.getTargetEstTime(0));

      assertEquals("M2:Tgt1: Invalid merge of heading data", 91d, cb2.getTargetHeading(1), HDG_TOLERANCE);
      assertEquals("M2:Tgt1: Invalid merge of probability data", 0.819, cb2.getTargetProb(1), PROB_TOLERANCE);
      assertEquals("M1:Tgt1: Invalid merge of pseudotime data", 510, cb2.getTargetEstTime(1));

      //cb1 has newer data than cb2, no merging should occur at all
      double cb1tgt0Hdg = cb1.getTargetHeading(0);
      double cb1tgt0Prob = cb1.getTargetProb(0);
      long cb1tgt0Time = cb1.getTargetEstTime(0);

      cb1.mergeBelief(cb2);
      assertEquals("Merged old hdg data", cb1tgt0Hdg, cb1.getTargetHeading(0), HDG_TOLERANCE);
      assertEquals("Merged old prob data", cb1tgt0Prob, cb1.getTargetProb(0), PROB_TOLERANCE);
      assertEquals("Merged old time data", cb1tgt0Time, cb1.getTargetEstTime(0));
   }
}
