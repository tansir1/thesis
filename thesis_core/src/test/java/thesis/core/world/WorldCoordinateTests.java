package thesis.core.world;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WorldCoordinateTests
{

   @Test
   public void basicAccessorsAndMutators()
   {
      WorldCoordinate testMe = new WorldCoordinate();
      final double EPS_THRESH = 0.0001;

      assertEquals("North did not initialize to zero.", 0, testMe.getNorth(), EPS_THRESH);
      assertEquals("East did not initialize to zero.", 0, testMe.getEast(), EPS_THRESH);

      double north = 43.234;
      double east = 123.567;
      testMe.setNorth(north);
      testMe.setEast(east);
      
      assertEquals("North did not set to value.", north, testMe.getNorth(), EPS_THRESH);
      assertEquals("East did not set to value.", east, testMe.getEast(), EPS_THRESH);
   }
   
   @Test
   public void advancedFunctions()
   {
      WorldCoordinate testMe = new WorldCoordinate();
      final double EPS_THRESH = 0.0001;
      
      double north = 843.25734;
      double east = 143.56247;
      
      testMe.setCoordinate(north, east);
      assertEquals("North did not set to value.", north, testMe.getNorth(), EPS_THRESH);
      assertEquals("East did not set to value.", east, testMe.getEast(), EPS_THRESH);
      
      double delNorth = 87.4;
      double delEast = -123.547;
      testMe.translate(delNorth, delEast);
      
      assertEquals("North did not translate.", north + delNorth, testMe.getNorth(), EPS_THRESH);
      assertEquals("East did not translate.", east + delEast, testMe.getEast(), EPS_THRESH);
      
      testMe.setCoordinate(0, 0);
      WorldCoordinate bearingToMe = new WorldCoordinate(0.5, Math.sqrt(3.0) * 0.5);
      assertEquals("Bearing to incorrect.", 30.0, testMe.bearingTo(bearingToMe), EPS_THRESH);
      
      testMe.setCoordinate(10, 20);
      WorldCoordinate distanceToMe = new WorldCoordinate(15, 30);
      assertEquals("Distance to incorrect", 11.18033, testMe.distanceTo(distanceToMe), EPS_THRESH);
   }
}
