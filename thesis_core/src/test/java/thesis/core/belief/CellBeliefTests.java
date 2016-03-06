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
      CellBelief cb1 = new CellBelief(0, 0, 2);
      CellBelief cb2 = new CellBelief(0, 0, 2);

      TargetBelief cb1tgt = cb1.getTargetBelief(0);
      TargetBelief cb2tgt = cb2.getTargetBelief(0);

      cb1tgt.setTypeProbability(0, 0.5);
      cb1tgt.setTypeProbability(1, 0.9);
      cb1tgt.setTimestamp(1000);
      cb1tgt.setHeadingEstimate(10);

      cb2tgt.setTypeProbability(0, 0);
      cb2tgt.setTypeProbability(1, 0);
      cb2tgt.setTimestamp(0);
      cb2tgt.setHeadingEstimate(0);

      cb1.updateEmptyBelief(1000, 0);

      cb2.mergeBelief(cb1);
      assertEquals("M1: Invalid merge of heading data", 7d, cb2tgt.getHeadingEstimate(), HDG_TOLERANCE);
      assertEquals("M1: Invalid merge of pseudotime data", 300, cb2tgt.getTimestamp());
      assertEquals("M1:Type0: Invalid merge of probability data", 0.25d, cb2tgt.getTypeProbability(0), PROB_TOLERANCE);
      assertEquals("M1:Type1: Invalid merge of probability data", 0.45d, cb2tgt.getTypeProbability(1), PROB_TOLERANCE);

      cb2.mergeBelief(cb1);
      assertEquals("M2: Invalid merge of heading data", 9.1, cb2tgt.getHeadingEstimate(), HDG_TOLERANCE);
      assertEquals("M2: Invalid merge of pseudotime data", 510, cb2tgt.getTimestamp());
      assertEquals("M2:Type0: Invalid merge of probability data", 0.325, cb2tgt.getTypeProbability(0), PROB_TOLERANCE);
      assertEquals("M2:Type1: Invalid merge of probability data", 0.585, cb2tgt.getTypeProbability(1), PROB_TOLERANCE);

      //cb1 has newer data than cb2, no merging should occur at all
      double cb1tgt0Hdg = cb1tgt.getHeadingEstimate();
      double cb1tgt0Prob = cb1tgt.getTypeProbability(0);
      long cb1tgt0Time = cb1tgt.getTimestamp();

      cb1.mergeBelief(cb2);
      assertEquals("Merged old hdg data", cb1tgt0Hdg, cb1tgt.getHeadingEstimate(), HDG_TOLERANCE);
      assertEquals("Merged old prob data", cb1tgt0Prob, cb1tgt.getTypeProbability(0), PROB_TOLERANCE);
      assertEquals("Merged old time data", cb1tgt0Time, cb1tgt.getTimestamp());
   }
}
