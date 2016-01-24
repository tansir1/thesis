package thesis.core.sensors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SensorProbsTests
{

   @Test
   public void lookupTests()
   {
      final float COMPARE_THRESH = 0.000001f;
      final int numSnsrs = 2;
      final int numTgts = 2;

      SensorProbs testMe = new SensorProbs();
      testMe.reset(numSnsrs, numTgts);

      testMe.setSensorDetectProb(0, 1, 0.5f);
      assertEquals("Failed to retrieve detection prob.", 0.5f, testMe.getSensorDetectProb(0, 1), COMPARE_THRESH);

      testMe.setSensorConfirmProb(1, 0, 0.1f);
      testMe.setSensorConfirmProb(1, 1, 0.8f);
      assertEquals("Failed to retrieve identification prob1.", 0.1f, testMe.getSensorConfirmProb(1, 0),
            COMPARE_THRESH);
      assertEquals("Failed to retrieve identification prob2.", 0.8f, testMe.getSensorConfirmProb(1, 1),
            COMPARE_THRESH);
   }
}
