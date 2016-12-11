package thesis.core.sensors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SensorProbsTests
{

   @Test
   public void lookupTests()
   {
      final double COMPARE_THRESH = 0.000001f;
      final int numSnsrs = 2;
      final int numTgts = 2;

      SensorProbs testMe = new SensorProbs();
      testMe.reset(numSnsrs, numTgts);

      testMe.setSensorDetectTgtProb(0, 1, 0.5f);
      assertEquals("Failed to retrieve detection prob.", 0.5f, testMe.getSensorDetectTgtProb(0, 1), COMPARE_THRESH);
   }
}
