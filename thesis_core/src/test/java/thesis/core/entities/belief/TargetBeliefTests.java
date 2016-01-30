package thesis.core.entities.belief;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TargetBeliefTests
{

   @Test
   public void mergeTest()
   {
      TargetBelief tb1 = new TargetBelief(1, false);
      TargetBelief tb2 = new TargetBelief(1, false);

      //Low confidence
      tb1.setConfidence(0.4f);
      tb1.getPose().setHeading(40.f);
      tb1.getPose().getCoordinate().setCoordinate(100, 100);

      //High confidence
      tb2.setConfidence(0.8f);
      tb2.getPose().setHeading(50.f);
      tb2.getPose().getCoordinate().setCoordinate(110, 110);

      //tb2 holds 2/3 of the total confidence between the two beliefs, so tb1
      //should move 2/3 of the way towards tb2
      tb1.merge(tb2);

      assertEquals("Failed to merge north correctly.", 106.67, tb1.getPose().getNorth(), 0.01f);
      assertEquals("Failed to merge east correctly.", 106.67, tb1.getPose().getEast(), 0.01f);
      assertEquals("Failed to merge heading correctly.", 46.67, tb1.getPose().getHeading(), 0.01f);
   }
}
