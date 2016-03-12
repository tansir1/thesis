package thesis.core.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AngleTests
{

   @Test
   public void test()
   {
      final double TOLERANCE = 0.000001;
      assertEquals("Failed to convert north", 90d, Angle.cartesianAngleToNorthUp(0), TOLERANCE);
      assertEquals("Failed to convert east", 0d, Angle.cartesianAngleToNorthUp(90), TOLERANCE);
      assertEquals("Failed to convert south", 180d, Angle.cartesianAngleToNorthUp(270), TOLERANCE);
      assertEquals("Failed to convert west", 270d, Angle.cartesianAngleToNorthUp(180), TOLERANCE);
   }
}
