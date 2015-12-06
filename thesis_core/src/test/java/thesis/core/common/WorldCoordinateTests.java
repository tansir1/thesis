package thesis.core.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WorldCoordinateTests
{
   public static final double WC_COMPARISON_THRESHOLD = 0.00001;

   @Test
   public void basicAccessorsAndMutators()
   {
      WorldCoordinate testMe = new WorldCoordinate();

      assertEquals("North did not initialize to zero.", 0, testMe.getNorth(), WC_COMPARISON_THRESHOLD);
      assertEquals("East did not initialize to zero.", 0, testMe.getEast(), WC_COMPARISON_THRESHOLD);

      double north = 43.234;
      double east = 123.567;
      testMe.setNorth(north);
      testMe.setEast(east);

      assertEquals("North did not set to value.", north, testMe.getNorth(), WC_COMPARISON_THRESHOLD);
      assertEquals("East did not set to value.", east, testMe.getEast(), WC_COMPARISON_THRESHOLD);
   }

   @Test
   public void advancedFunctions()
   {
      WorldCoordinate testMe = new WorldCoordinate();

      double north = 843.25734;
      double east = 143.56247;
      testMe.setNorth(north);
      testMe.setEast(east);

      testMe.setCoordinate(north, east);
      assertEquals("North did not set to value.", north, testMe.getNorth(), WC_COMPARISON_THRESHOLD);
      assertEquals("East did not set to value.", east, testMe.getEast(), WC_COMPARISON_THRESHOLD);

      double delNorth = 843.25734;
      double delEast = 143.56247;
      testMe.translateCart(delNorth, delEast);

      double combineN = north + delNorth;
      double combineE = east + delEast;

      assertEquals("North did not translate.", combineN, testMe.getNorth(), WC_COMPARISON_THRESHOLD);
      assertEquals("East did not translate.", combineE, testMe.getEast(), WC_COMPARISON_THRESHOLD);

      testMe.setCoordinate(0, 0);
      WorldCoordinate bearingToMe = new WorldCoordinate(0.5, Math.sqrt(3.0) * 0.5);
      double expectedResult = 30;
      assertEquals("Bearing to incorrect.", expectedResult, testMe.bearingTo(bearingToMe), 0.0000001);

      testMe.setCoordinate(10, 20);

      WorldCoordinate distanceToMe = new WorldCoordinate(15, 30);
      double distanceResult = 11.180339;
      assertEquals("Distance to incorrect", distanceResult, testMe.distanceTo(distanceToMe), WC_COMPARISON_THRESHOLD);
   }
}
