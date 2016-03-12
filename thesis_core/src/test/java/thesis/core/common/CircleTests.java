package thesis.core.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CircleTests
{

   @Test
   public void minInterceptTangentPose()
   {
      final double HDG_TOLERANCE = 0.01;

      Circle testMe = new Circle();
      testMe.getCenter().setCoordinate(10, 10);
      testMe.setRadius(5);

      WorldPose start = new WorldPose();
      start.getCoordinate().setCoordinate(15, 5);
      start.setHeading(90);//east

      WorldPose end = new WorldPose();
      end.getCoordinate().setCoordinate(13.54, 13.54);
      end.setHeading(99.74);

      WorldPose intercept = testMe.minTravelToTangent(start);
      assertTrue("Invalid minimum tangent intercept", end.getCoordinate().equals(end.getCoordinate()));
      assertEquals("Invalid minimum tangent heading", end.getHeading(), intercept.getHeading(), HDG_TOLERANCE);

   }
}
