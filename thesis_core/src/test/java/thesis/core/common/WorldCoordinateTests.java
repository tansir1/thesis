package thesis.core.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import thesis.core.common.Angle;
import thesis.core.common.Distance;
import thesis.core.common.WorldCoordinate;

public class WorldCoordinateTests
{

   @Test
   public void basicAccessorsAndMutators()
   {
      WorldCoordinate testMe = new WorldCoordinate();

      Distance zeroDist = new Distance();
      
      assertEquals("North did not initialize to zero.", zeroDist, testMe.getNorth());
      assertEquals("East did not initialize to zero.", zeroDist, testMe.getEast());

      Distance north = new Distance();
      Distance east = new Distance();
      north.setAsMeters(43.234);
      east.setAsMeters(123.567);
      testMe.setNorth(north);
      testMe.setEast(east);
      
      assertEquals("North did not set to value.", north, testMe.getNorth());
      assertEquals("East did not set to value.", east, testMe.getEast());
   }
   
   @Test
   public void advancedFunctions()
   {
      WorldCoordinate testMe = new WorldCoordinate();
      
      Distance north = new Distance();
      Distance east = new Distance();
      north.setAsMeters(843.25734);
      east.setAsMeters(143.56247);
      testMe.setNorth(north);
      testMe.setEast(east);      
            
      testMe.setCoordinate(north, east);
      assertEquals("North did not set to value.", north, testMe.getNorth());
      assertEquals("East did not set to value.", east, testMe.getEast());
      
      Distance delNorth = new Distance();
      Distance delEast = new Distance();
      delNorth.setAsMeters(843.25734);
      delEast.setAsMeters(143.56247);
      testMe.translate(delNorth, delEast);
      
      Distance combineN = new Distance(north);
      Distance combineE = new Distance(east);
      combineN.add(delNorth);
      combineE.add(delEast);
      
      assertEquals("North did not translate.", combineN, testMe.getNorth());
      assertEquals("East did not translate.", combineE, testMe.getEast());
      
      Distance zeroDist = new Distance();
      testMe.setCoordinate(zeroDist, zeroDist);
      Distance pt1 = new Distance();
      pt1.setAsMeters(0.5);
      Distance pt2 = new Distance();
      pt2.setAsMeters(Math.sqrt(3.0) * 0.5);
      WorldCoordinate bearingToMe = new WorldCoordinate(pt1, pt2);
      Angle expectedResult = new Angle();
      expectedResult.setAsDegrees(30);
      assertEquals("Bearing to incorrect.", expectedResult, testMe.bearingTo(bearingToMe));
      
      pt1.setAsMeters(10);
      pt2.setAsMeters(20);
      testMe.setCoordinate(pt1, pt2);
      
      pt1 = new Distance();
      pt2 = new Distance();
      pt1.setAsMeters(15);
      pt2.setAsMeters(30);
      WorldCoordinate distanceToMe = new WorldCoordinate(pt1, pt2);
      Distance distanceResult = new Distance();
      distanceResult.setAsMeters(11.180339);
      assertEquals("Distance to incorrect", distanceResult, testMe.distanceTo(distanceToMe));
   }
}
