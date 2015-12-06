package thesis.core.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RectangleTests
{

   @Test
   public void axisAlignedPointInOutTest()
   {
      Rectangle testMe = new Rectangle();

      /*
       * 0,10-----------30,10
       *  |               |
       * 0,0------------30,0
       */
      WorldCoordinate.setAsMeters(testMe.getBottomLeft(), 0, 0);
      WorldCoordinate.setAsMeters(testMe.getTopLeft(), 10, 0);
      WorldCoordinate.setAsMeters(testMe.getTopRight(), 10, 30);
      WorldCoordinate.setAsMeters(testMe.getBottomRight(), 0, 30);

      assertEquals("Incorrect width", 30, testMe.getWidth().asMeters(), 0.0000000001);
      assertEquals("Incorrect height", 10, testMe.getHeight().asMeters(), 0.0000000001);

      WorldCoordinate testPt = new WorldCoordinate();
      WorldCoordinate.setAsMeters(testPt, 100, 100);
      assertFalse("Point outside of rectangle marked as inside.", testMe.isCoordinateInRegion(testPt));

      WorldCoordinate.setAsMeters(testPt, 5, 5);
      assertTrue("Point inside of rectangle marked as outside.", testMe.isCoordinateInRegion(testPt));
   }

   @Test
   public void rotatedPointInOutTest()
   {
      Rectangle testMe = new Rectangle();

      WorldCoordinate.setAsMeters(testMe.getBottomLeft(), 1, 2);
      WorldCoordinate.setAsMeters(testMe.getTopLeft(), 2, 1);
      WorldCoordinate.setAsMeters(testMe.getTopRight(), 6, 5);
      WorldCoordinate.setAsMeters(testMe.getBottomRight(), 5, 6);

      WorldCoordinate testPt = new WorldCoordinate();
      WorldCoordinate.setAsMeters(testPt, 100, 100);
      assertFalse("Point outside of rectangle marked as inside.", testMe.isCoordinateInRegion(testPt));

      WorldCoordinate.setAsMeters(testPt, 3, 4);
      assertTrue("Point inside of rectangle marked as outside.", testMe.isCoordinateInRegion(testPt));

   }
}