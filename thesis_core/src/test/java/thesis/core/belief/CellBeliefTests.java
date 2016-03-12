package thesis.core.belief;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import thesis.core.common.SimTime;

public class CellBeliefTests
{
   @Test
   public void mergeTest()
   {
      final double PROB_TOLERANCE = 0.0001;
      final double UNCERT_TOLERANCE = 0.00001;

      CellBelief.NEWER_TGT_ALPHA = 0.7;
      CellBelief cb1 = new CellBelief(0, 0, 2, 0);
      CellBelief cb2 = new CellBelief(0, 0, 2, 0);

      // Known values: probEmpty=0.7, uncert= ~0.265295
      cb1.updateEmptyBelief(16, 0.7);
      assertEquals("Did not store new prob empty.", 0.7, cb1.getProbabilityEmptyCell(), PROB_TOLERANCE);
      assertEquals("cb1: Unexpected uncertainty value.", 0.265295, cb1.getUncertainty(), UNCERT_TOLERANCE);
      assertEquals("cb1: Unexpected pseudo timestamp.", 16, cb1.getPseudoTimestamp());

      cb2.mergeBelief(cb1);
      assertEquals("Did not correctly merge prob empty update.", 0.639999, cb2.getProbabilityEmptyCell(),
            PROB_TOLERANCE);
      assertEquals("cb2: Unexpected uncertainty value.", 0.283776, cb2.getUncertainty(), UNCERT_TOLERANCE);
      assertEquals("cb2: Unexpected pseudo timestamp.", 4, cb2.getPseudoTimestamp());
   }

   @Test
   public void decayTest()
   {
      final double decayRate = 0.0001;
      final double PROB_TOLERANCE = decayRate * 0.01;// Get within 1% of
                                                     // expected decay
      final double initProbEmpty = 0.3;

      //Probability decays to 50% empty/not empty, so from 0.3 to 0.5
      final double expectedProb = initProbEmpty + decayRate;

      CellBelief testMe = new CellBelief(0, 0, 2, decayRate);
      testMe.updateEmptyBelief(0, initProbEmpty);
      // Simulate 1 second worth of time
      for (int i = 0; i < SimTime.SIM_STEP_RATE_HZ; ++i)
      {
         testMe.stepSimulation();
      }
      assertEquals("Invalid decay result.", expectedProb, testMe.getProbabilityEmptyCell(), PROB_TOLERANCE);
   }
}
