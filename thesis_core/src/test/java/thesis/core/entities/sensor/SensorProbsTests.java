package thesis.core.entities.sensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import thesis.core.entities.sensors.SensorProbs;

public class SensorProbsTests
{

   @Test
   public void lookupTests()
   {
      final float COMPARE_THRESH = 0.000001f;

      SensorProbs testMe = new SensorProbs();
      testMe.setDetectionProb(1, 2, 0.5f);
      assertEquals("Failed to retrieve detection prob.", 0.5f, testMe.getDetectionProb(1, 2), COMPARE_THRESH);

      testMe.setIdentificationProb(3, 4, 0.1f);
      testMe.setIdentificationProb(42, 13, 0.8f);
      assertEquals("Failed to retrieve identification prob1.", 0.1f, testMe.getIdentificationProb(3, 4),
            COMPARE_THRESH);
      assertEquals("Failed to retrieve identification prob2.", 0.8f, testMe.getIdentificationProb(42, 13),
            COMPARE_THRESH);

      assertTrue("Returned non-existant detection prob.", testMe.getDetectionProb(10, 10) < 0);
      assertTrue("Returned non-existant identificaiton prob.", testMe.getDetectionProb(10, 10) < 0);
   }
}
