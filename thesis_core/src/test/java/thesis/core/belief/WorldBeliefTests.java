package thesis.core.belief;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import thesis.core.common.WorldCoordinate;

public class WorldBeliefTests
{
   @Test
   public void mergeTest()
   {
      final double HDG_TOLERANCE = 0.01;
      final double PROB_TOLERANCE = 0.0001;
      final double DISTANCE_TOLERANCE = 0.000000001;

      WorldBelief.NEWER_TGT_ALPHA = 0.7;
      WorldBelief wb1 = new WorldBelief(1, 1, 2, 0, 0.1);
      WorldBelief wb2 = new WorldBelief(1, 1, 2, 0, 0.1);

      TargetBelief wb1tgt = wb1.getTargetBelief(0);
      TargetBelief wb2tgt = wb2.getTargetBelief(0);

      wb1tgt.setTypeProbability(0, 0.5);
      wb1tgt.setTypeProbability(1, 0.9);
      wb1tgt.setTimestamp(1000);
      wb1tgt.setHeadingEstimate(10);
      wb1tgt.setCoordinate(new WorldCoordinate(10, 20));

      wb2tgt.setTypeProbability(0, 0);
      wb2tgt.setTypeProbability(1, 0);
      wb2tgt.setTimestamp(0);
      wb2tgt.setHeadingEstimate(0);
      wb2tgt.setCoordinate(new WorldCoordinate());

      wb2.mergeBelief(wb1);
      assertEquals("M1: Invalid merge of heading data", 7d, wb2tgt.getHeadingEstimate(), HDG_TOLERANCE);
      assertEquals("M1: Invalid merge of pseudotime data", 300, wb2tgt.getTimestamp());
      assertEquals("M1:Type0: Invalid merge of probability data", 0.25d, wb2tgt.getTypeProbability(0), PROB_TOLERANCE);
      assertEquals("M1:Type1: Invalid merge of probability data", 0.45d, wb2tgt.getTypeProbability(1), PROB_TOLERANCE);
      assertEquals("M1:North: Invalid merge of probability data", 7, wb2tgt.getCoordinate().getNorth(), DISTANCE_TOLERANCE);
      assertEquals("M1:East: Invalid merge of probability data", 14, wb2tgt.getCoordinate().getEast(), DISTANCE_TOLERANCE);

      wb2.mergeBelief(wb1);
      assertEquals("M2: Invalid merge of heading data", 9.1, wb2tgt.getHeadingEstimate(), HDG_TOLERANCE);
      assertEquals("M2: Invalid merge of pseudotime data", 510, wb2tgt.getTimestamp());
      assertEquals("M2:Type0: Invalid merge of probability data", 0.325, wb2tgt.getTypeProbability(0), PROB_TOLERANCE);
      assertEquals("M2:Type1: Invalid merge of probability data", 0.585, wb2tgt.getTypeProbability(1), PROB_TOLERANCE);
      assertEquals("M2:North: Invalid merge of probability data", 9.1, wb2tgt.getCoordinate().getNorth(), DISTANCE_TOLERANCE);
      assertEquals("M2:East: Invalid merge of probability data", 18.2, wb2tgt.getCoordinate().getEast(), DISTANCE_TOLERANCE);

      //cb1 has newer data than cb2, no merging should occur at all
      double cb1tgt0Hdg = wb1tgt.getHeadingEstimate();
      double cb1tgt0Prob = wb1tgt.getTypeProbability(0);
      long cb1tgt0Time = wb1tgt.getTimestamp();

      wb1.mergeBelief(wb2);
      assertEquals("Merged old hdg data", cb1tgt0Hdg, wb1tgt.getHeadingEstimate(), HDG_TOLERANCE);
      assertEquals("Merged old prob data", cb1tgt0Prob, wb1tgt.getTypeProbability(0), PROB_TOLERANCE);
      assertEquals("Merged old time data", cb1tgt0Time, wb1tgt.getTimestamp());
   }
}
