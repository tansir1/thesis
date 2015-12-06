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
      testMe.getBottomLeft().setCoordinate(0,0);
      testMe.getTopLeft().setCoordinate(10, 0);
      testMe.getTopRight().setCoordinate(10, 30);
      testMe.getBottomRight().setCoordinate(0, 30);

      assertEquals("Incorrect width", 30, testMe.getWidth(), 0.0000000001);
      assertEquals("Incorrect height", 10, testMe.getHeight(), 0.0000000001);

      WorldCoordinate testPt = new WorldCoordinate(100, 100);
      assertFalse("Point outside of rectangle marked as inside.", testMe.isCoordinateInRegion(testPt));

      testPt.setCoordinate(5,5);
      assertTrue("Point inside of rectangle marked as outside.", testMe.isCoordinateInRegion(testPt));
   }

   @Test
   public void rotatedPointInOutTest()
   {
      Rectangle testMe = new Rectangle();

      testMe.getBottomLeft().setCoordinate(1, 2);
      testMe.getTopLeft().setCoordinate(2, 1);
      testMe.getTopRight().setCoordinate(6, 5);
      testMe.getBottomRight().setCoordinate(5, 6);

      WorldCoordinate testPt = new WorldCoordinate(100, 100);
      assertFalse("Point outside of rectangle marked as inside.", testMe.isCoordinateInRegion(testPt));

      testPt.setCoordinate(3,4);
      assertTrue("Point inside of rectangle marked as outside.", testMe.isCoordinateInRegion(testPt));

   }
}
